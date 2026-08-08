package io.github.easy4j.dreamina.cli.support;

import lombok.Getter;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Subprocess execution support based on Apache Commons Exec: Watchdog timeout, bounded {@code waitFor}, and concurrency throttling.
 * <p>
 * Follows the {@code DefaultExecutor.builder()} + {@link ExecuteWatchdog} pattern from Playwright Starter,
 * supplemented with an async handler and Semaphore to prevent threads from being permanently blocked
 * by zombie subprocesses under high concurrency.
 * </p>
 *
 * @see io.github.easy4j.dreamina.cli.DreaminaCliExecutor
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 */
public final class SubprocessExecutionSupport {

    /** Maximum wait time (milliseconds) for handler cleanup after Watchdog triggers. */
    public static final long WAIT_GRACE_MILLIS = 5_000L;

    private static final int DEFAULT_MAX_CONCURRENT = Math.max(2, Runtime.getRuntime().availableProcessors());

    private static final AtomicReference<Semaphore> CONCURRENCY_LIMIT =
            new AtomicReference<>(new Semaphore(DEFAULT_MAX_CONCURRENT));

    private SubprocessExecutionSupport() {
    }

    /**
     * Configures the global concurrency limit for local CLI subprocesses; restores the default when {@code maxConcurrent <= 0}.
     *
     * @param maxConcurrent Number of subprocesses allowed to run concurrently
     */
    public static void configureMaxConcurrentExecutions(int maxConcurrent) {
        if (maxConcurrent <= 0) {
            CONCURRENCY_LIMIT.set(new Semaphore(DEFAULT_MAX_CONCURRENT));
            return;
        }
        CONCURRENCY_LIMIT.set(new Semaphore(maxConcurrent));
    }

    /**
     * @return Default concurrency limit when not explicitly configured
     */
    public static int defaultMaxConcurrentExecutions() {
        return DEFAULT_MAX_CONCURRENT;
    }

    /**
     * Starts a subprocess within the concurrency permit and blocks until completion, timeout, or forced destruction.
     *
     * @param request Command line, working directory, environment, and timeout
     * @return Captured output and handler state
     */
    public static RunSession execute(ExecutionRequest request) throws IOException, InterruptedException {
        Objects.requireNonNull(request, "request");
        Semaphore limit = CONCURRENCY_LIMIT.get();
        limit.acquire();
        try {
            return executeWithinLimit(request);
        } finally {
            limit.release();
        }
    }

    private static RunSession executeWithinLimit(ExecutionRequest request) throws IOException, InterruptedException {
        long timeoutMs = Math.max(1L, request.getTimeoutMillis());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();

        DefaultExecutor.Builder builder = DefaultExecutor.builder();
        if (request.getWorkingDirectory() != null) {
            builder.setWorkingDirectory(request.getWorkingDirectory());
        }
        DefaultExecutor executor = builder.get();
        executor.setStreamHandler(new PumpStreamHandler(out, err));

        ExecuteWatchdog watchdog =
                ExecuteWatchdog.builder().setTimeout(Duration.ofMillis(timeoutMs)).get();
        executor.setWatchdog(watchdog);

        DefaultExecuteResultHandler handler = new DefaultExecuteResultHandler();
        Map<String, String> environment = request.getEnvironment();
        if (environment != null) {
            executor.execute(request.getCommandLine(), environment, handler);
        } else {
            executor.execute(request.getCommandLine(), handler);
        }

        boolean finished = awaitResult(handler, timeoutMs + WAIT_GRACE_MILLIS);
        boolean waitTimedOut = !finished;
        if (waitTimedOut) {
            watchdog.destroyProcess();
            awaitResult(handler, WAIT_GRACE_MILLIS);
        }

        return new RunSession(out, err, handler, watchdog, timeoutMs, waitTimedOut);
    }

    private static boolean awaitResult(DefaultExecuteResultHandler handler, long timeoutMillis)
            throws InterruptedException {
        long deadline = System.currentTimeMillis() + Math.max(1L, timeoutMillis);
        while (!handler.hasResult()) {
            if (System.currentTimeMillis() >= deadline) {
                return false;
            }
            Thread.sleep(Math.min(50L, deadline - System.currentTimeMillis()));
        }
        return true;
    }

    /**
     * A single subprocess execution request.
     */
    @Getter
    public static final class ExecutionRequest {

        private final CommandLine commandLine;
        private final File workingDirectory;
        private final Map<String, String> environment;
        private final long timeoutMillis;

        /**
         * @param commandLine      待执行命令行
         * @param workingDirectory 可选工作目录
         * @param environment      可选环境变量覆盖；null 表示继承 JVM 环境
         * @param timeoutMillis    Watchdog 超时（毫秒）
         */
        public ExecutionRequest(
                CommandLine commandLine,
                File workingDirectory,
                Map<String, String> environment,
                long timeoutMillis) {
            this.commandLine = Objects.requireNonNull(commandLine, "commandLine");
            this.workingDirectory = workingDirectory;
            this.environment = environment;
            this.timeoutMillis = timeoutMillis;
        }
    }

    /**
     * Subprocess execution session result (output streams are retained for the upper layer to convert to strings on demand).
     */
    @Getter
    public static final class RunSession {

        private final ByteArrayOutputStream stdout;
        private final ByteArrayOutputStream stderr;
        private final DefaultExecuteResultHandler handler;
        private final ExecuteWatchdog watchdog;
        private final long timeoutMillis;
        private final boolean waitTimedOut;

        RunSession(
                ByteArrayOutputStream stdout,
                ByteArrayOutputStream stderr,
                DefaultExecuteResultHandler handler,
                ExecuteWatchdog watchdog,
                long timeoutMillis,
                boolean waitTimedOut) {
            this.stdout = stdout;
            this.stderr = stderr;
            this.handler = handler;
            this.watchdog = watchdog;
            this.timeoutMillis = timeoutMillis;
            this.waitTimedOut = waitTimedOut;
        }

        /**
         * @return Whether the Watchdog killed the process or the handler wait timed out.
         */
        public boolean timedOut() {
            return waitTimedOut || watchdog.killedProcess();
        }
    }
}
