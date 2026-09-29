package io.github.easy4j.dreamina.cli.support;

import io.github.easy4j.dreamina.DreaminaCanvasCliProperties;
import io.github.easy4j.dreamina.cli.DreaminaCliResult;
import io.github.easy4j.dreamina.exception.DreaminaCanvasCliException;
import io.github.easy4j.dreamina.exception.DreaminaCliException;
import io.github.easy4j.dreamina.exception.DreaminaCliExecutableFailureException;
import io.github.easy4j.dreamina.exception.DreaminaCliTimeoutException;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * 内部执行器：实例隔离并发、共用排队/执行预算、并发有界排空输出。
 */
public final class DreaminaCanvasProcessRunner {
    private final DreaminaCanvasCliProperties config;
    private final Semaphore permits;

    /**
     * 创建只服务于一个客户端的执行器。
     */
    public DreaminaCanvasProcessRunner(DreaminaCanvasCliProperties config) {
        this.config = DreaminaCanvasCliProperties.copyOf(config);
        this.permits = new Semaphore(config.getMaxConcurrentExecutions(), true);
    }

    private static void join(Thread thread, long deadline) throws InterruptedException {
        long remaining = deadline - System.nanoTime();
        if (remaining > 0) {
            TimeUnit.NANOSECONDS.timedJoin(thread, remaining);
        }
    }

    private static void close(InputStream stream) {
        try {
            stream.close();
        } catch (IOException ignored) {
            // 清理失败不覆盖原始错误，也不将可能含凭据的进程数据写入日志。
        }
    }

    private static DreaminaCliException failure(DreaminaCanvasCliException.Reason reason, String command,
                                                Process process, Capture stdout, Capture stderr) {
        Integer exitCode = Objects.nonNull(process) && !process.isAlive() ? process.exitValue() : null;
        if (reason == DreaminaCanvasCliException.Reason.TIMEOUT) {
            return new DreaminaCliTimeoutException("Canvas CLI " + command + ": TIMEOUT", DreaminaCliResult.builder()
                    .exitCode(exitCode).stdout(stdout.text()).stderr(stderr.text()).success(false).build());
        }
        return new DreaminaCanvasCliException(reason, command, exitCode, stdout.text(), stderr.text());
    }

    /**
     * 执行 argv，不启动 shell，也不记录参数；异常保留显式可读的输出快照。
     */
    public DreaminaCliResult run(String command, List<String> arguments) {
        long deadline = System.nanoTime() + config.getTimeout().toNanos();
        boolean acquired = false;
        Process process = null;
        Capture stdout = new Capture(config.getMaxOutputBytes());
        Capture stderr = new Capture(config.getMaxOutputBytes());
        Thread outReader = null;
        Thread errReader = null;
        try {
            acquired = permits.tryAcquire(Math.max(0L, deadline - System.nanoTime()), TimeUnit.NANOSECONDS);
            if (!acquired || System.nanoTime() >= deadline) {
                throw failure(DreaminaCanvasCliException.Reason.TIMEOUT, command, process, stdout, stderr);
            }
            List<String> argv = new ArrayList<>();
            argv.add(config.getExecutable());
            argv.addAll(arguments);
            ProcessBuilder builder = new ProcessBuilder(argv);
            if (StringUtils.isNotBlank(config.getWorkingDirectory())) {
                builder.directory(new File(config.getWorkingDirectory()));
            }
            try {
                process = builder.start();
            } catch (IOException e) {
                throw new DreaminaCliExecutableFailureException("Canvas CLI " + command + ": START_FAILED", null);
            }
            process.getOutputStream().close();
            outReader = stdout.read(process.getInputStream());
            errReader = stderr.read(process.getErrorStream());
            while (true) {
                if (stdout.overflow || stderr.overflow) {
                    throw failure(DreaminaCanvasCliException.Reason.OUTPUT_LIMIT, command, process, stdout, stderr);
                }
                long remaining = deadline - System.nanoTime();
                if (remaining <= 0) {
                    throw failure(DreaminaCanvasCliException.Reason.TIMEOUT, command, process, stdout, stderr);
                }
                if (process.waitFor(Math.min(remaining, TimeUnit.MILLISECONDS.toNanos(25)), TimeUnit.NANOSECONDS)) {
                    break;
                }
            }
            // 等待两个流到 EOF，但后台后代继承管道时也不能无限阻塞。
            join(outReader, deadline);
            join(errReader, deadline);
            if (outReader.isAlive() || errReader.isAlive()) {
                throw failure(DreaminaCanvasCliException.Reason.TIMEOUT, command, process, stdout, stderr);
            }
            if (stdout.overflow || stderr.overflow) {
                throw failure(DreaminaCanvasCliException.Reason.OUTPUT_LIMIT, command, process, stdout, stderr);
            }
            if (stdout.failed || stderr.failed) {
                throw failure(DreaminaCanvasCliException.Reason.IO_FAILED, command, process, stdout, stderr);
            }
            return DreaminaCliResult.builder().exitCode(process.exitValue()).stdout(stdout.text()).stderr(stderr.text())
                    .success(process.exitValue() == 0).build();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw failure(DreaminaCanvasCliException.Reason.INTERRUPTED, command, process, stdout, stderr);
        } catch (IOException e) {
            throw failure(DreaminaCanvasCliException.Reason.IO_FAILED, command, process, stdout, stderr);
        } finally {
            if (Objects.nonNull(process)) {
                if (process.isAlive()) {
                    process.destroyForcibly();
                }
                close(process.getInputStream());
                close(process.getErrorStream());
            }
            if (acquired) {
                permits.release();
            }
        }
    }

    private static final class Capture {
        private final int limit;
        private final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        private volatile boolean overflow;
        private volatile boolean failed;

        private Capture(int limit) {
            this.limit = limit;
        }

        private Thread read(InputStream stream) {
            Thread thread = new Thread(() -> {
                byte[] buffer = new byte[8192];
                try {
                    int count;
                    while ((count = stream.read(buffer)) != -1) {
                        append(buffer, count);
                    }
                } catch (IOException e) {
                    failed = true;
                }
            }, "dreamina-canvas-output");
            thread.setDaemon(true);
            thread.start();
            return thread;
        }

        private synchronized void append(byte[] buffer, int count) {
            int remaining = limit - bytes.size();
            if (count > remaining) {
                overflow = true;
            }
            bytes.write(buffer, 0, Math.min(count, remaining));
        }

        private synchronized String text() {
            return new String(bytes.toByteArray(), StandardCharsets.UTF_8);
        }
    }
}
