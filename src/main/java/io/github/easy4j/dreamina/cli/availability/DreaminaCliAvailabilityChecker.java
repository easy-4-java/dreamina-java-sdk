package io.github.easy4j.dreamina.cli.availability;

import io.github.easy4j.dreamina.DreaminaCanvasCliProperties;
import io.github.easy4j.dreamina.DreaminaCliProperties;
import io.github.easy4j.dreamina.cli.DreaminaCanvasCliExecutor;
import io.github.easy4j.dreamina.cli.DreaminaCanvasResponse;
import io.github.easy4j.dreamina.cli.DreaminaCliExecutor;
import io.github.easy4j.dreamina.cli.DreaminaCliResult;
import io.github.easy4j.dreamina.cli.model.DreaminaVersion;
import io.github.easy4j.dreamina.exception.DreaminaCliException;
import io.github.easy4j.dreamina.exception.DreaminaCliExecutableFailureException;
import io.github.easy4j.dreamina.exception.DreaminaCliNonZeroExitException;
import io.github.easy4j.dreamina.exception.DreaminaCliTimeoutException;
import io.github.easy4j.dreamina.util.DreaminaStrings;

import java.io.File;
import java.util.Objects;
import java.util.Optional;

/**
 * Probes whether the local {@code dreamina} CLI is installed and can execute {@code dreamina version}.
 * <p>
 * Can be called by a Spring Boot Starter during startup, or manually in a plain Java application
 * to perform a readiness check.
 * </p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @see DreaminaCliAvailabilityReport
 * @see DreaminaCliAvailabilityStatus
 * @since 3.0.0
 */
public class DreaminaCliAvailabilityChecker {

    /**
     * Copies the configuration and shortens the probe timeout to avoid startup checks consuming the default command timeout.
     */
    private static DreaminaCliProperties copyForProbe(DreaminaCliProperties source) {
        DreaminaCliProperties copy = new DreaminaCliProperties();
        copy.setExecutable(source.getExecutable());
        copy.setWorkingDirectory(source.getWorkingDirectory());
        copy.setMaxConcurrentExecutions(source.getMaxConcurrentExecutions());
        copy.setDefaultPollIntervalSeconds(source.getDefaultPollIntervalSeconds());
        long probeTimeout = source.getStartupProbeTimeoutMillis();
        if (probeTimeout <= 0) {
            probeTimeout = 30_000L;
        }
        copy.setCommandTimeoutMillis(probeTimeout);
        copy.setStartupProbeTimeoutMillis(probeTimeout);
        return copy;
    }

    /**
     * Resolves the executable: checks absolute/relative paths directly; otherwise searches {@code PATH}.
     */
    static Optional<String> resolveExecutablePath(String executable) {
        if (DreaminaStrings.isBlank(executable)) {
            return Optional.empty();
        }
        String trimmed = executable.trim();
        File direct = new File(trimmed);
        if (looksLikePath(trimmed)) {
            if (direct.isFile() && direct.canExecute()) {
                return Optional.of(direct.getAbsolutePath());
            }
            return Optional.empty();
        }
        String pathEnv = System.getenv("PATH");
        if (pathEnv == null || pathEnv.isEmpty()) {
            return Optional.empty();
        }
        for (String dir : pathEnv.split(File.pathSeparator)) {
            if (DreaminaStrings.isBlank(dir)) {
                continue;
            }
            File candidate = new File(dir.trim(), trimmed);
            if (candidate.isFile() && candidate.canExecute()) {
                return Optional.of(candidate.getAbsolutePath());
            }
        }
        return Optional.empty();
    }

    private static boolean looksLikePath(String executable) {
        return executable.contains("/") || executable.contains("\\") || new File(executable).isAbsolute();
    }

    private static DreaminaCliAvailabilityReport unavailable(
            DreaminaCliAvailabilityStatus status,
            String configured,
            String resolved,
            String message,
            DreaminaCliResult partial) {
        return DreaminaCliAvailabilityReport.builder()
                .status(status)
                .available(false)
                .configuredExecutable(configured)
                .resolvedExecutablePath(resolved)
                .message(message)
                .probeResult(partial)
                .build();
    }

    /**
     * Performs the probe using the given executor and its bound configuration (recommended: share the same {@link DreaminaCliExecutor} bean with the runtime).
     *
     * @param executor 已构造的执行器，不得为 null
     * @return 探测报告
     * @deprecated 仅适用于旧 Dreamina CLI 协议；Canvas 请使用 DreaminaCanvasCliExecutor 对应命令或 checkCanvas。
     */
    @Deprecated
    public DreaminaCliAvailabilityReport check(DreaminaCliExecutor executor) {
        Objects.requireNonNull(executor, "executor");
        return check(executor.getProperties());
    }

    /**
     * Constructs a temporary executor from the configuration and probes CLI availability.
     *
     * @param properties CLI 配置，不得为 null
     * @return 探测报告
     * @deprecated 仅适用于旧 Dreamina CLI 协议；Canvas 请使用 DreaminaCanvasCliExecutor 对应命令或 checkCanvas。
     */
    @Deprecated
    public DreaminaCliAvailabilityReport check(DreaminaCliProperties properties) {
        Objects.requireNonNull(properties, "properties");
        String configured = properties.getExecutable();
        if (DreaminaStrings.isBlank(configured)) {
            return unavailable(
                    DreaminaCliAvailabilityStatus.EXECUTABLE_NOT_CONFIGURED,
                    configured,
                    null,
                    "dreamina.cli.executable is blank",
                    null);
        }
        String trimmed = configured.trim();
        Optional<String> resolved = resolveExecutablePath(trimmed);
        if (!resolved.isPresent()) {
            if (looksLikePath(trimmed)) {
                File file = new File(trimmed);
                if (!file.exists()) {
                    return unavailable(
                            DreaminaCliAvailabilityStatus.EXECUTABLE_NOT_FOUND,
                            trimmed,
                            null,
                            "executable file does not exist: " + file.getAbsolutePath(),
                            null);
                }
                return unavailable(
                        DreaminaCliAvailabilityStatus.EXECUTABLE_NOT_EXECUTABLE,
                        trimmed,
                        file.getAbsolutePath(),
                        "executable exists but is not executable: " + file.getAbsolutePath(),
                        null);
            }
            return unavailable(
                    DreaminaCliAvailabilityStatus.EXECUTABLE_NOT_FOUND,
                    trimmed,
                    null,
                    "executable not found on PATH: " + trimmed,
                    null);
        }

        DreaminaCliProperties probeProps = copyForProbe(properties);
        DreaminaCliExecutor probeExecutor = new DreaminaCliExecutor(probeProps);
        try {
            DreaminaCliResult result = probeExecutor.version();
            return DreaminaCliAvailabilityReport.builder()
                    .status(DreaminaCliAvailabilityStatus.AVAILABLE)
                    .available(true)
                    .configuredExecutable(trimmed)
                    .resolvedExecutablePath(resolved.get())
                    .message("dreamina version succeeded")
                    .probeResult(result)
                    .build();
        } catch (DreaminaCliTimeoutException ex) {
            return unavailable(
                    DreaminaCliAvailabilityStatus.TIMEOUT,
                    trimmed,
                    resolved.get(),
                    ex.getMessage(),
                    ex.getPartialResult());
        } catch (DreaminaCliNonZeroExitException ex) {
            return unavailable(
                    DreaminaCliAvailabilityStatus.NON_ZERO_EXIT,
                    trimmed,
                    resolved.get(),
                    ex.getMessage(),
                    ex.getPartialResult());
        } catch (DreaminaCliExecutableFailureException ex) {
            return unavailable(
                    DreaminaCliAvailabilityStatus.SPAWN_FAILED,
                    trimmed,
                    resolved.get(),
                    ex.getMessage(),
                    null);
        } catch (DreaminaCliException ex) {
            return unavailable(
                    DreaminaCliAvailabilityStatus.FAILED,
                    trimmed,
                    resolved.get(),
                    ex.getMessage(),
                    ex.getPartialResult());
        }
    }

    /**
     * 使用同一检查器和报告模型探测 Canvas；不改动旧 check 的行为。
     */
    public DreaminaCliAvailabilityReport checkCanvas(DreaminaCanvasCliProperties properties) {
        Objects.requireNonNull(properties, "properties");
        String executable = properties.getExecutable();
        if (DreaminaStrings.isBlank(executable)) {
            return unavailable(DreaminaCliAvailabilityStatus.EXECUTABLE_NOT_CONFIGURED, executable, null,
                    "Canvas executable is blank", null);
        }
        Optional<String> resolved = resolveExecutablePath(executable);
        if (!resolved.isPresent()) {
            DreaminaCliAvailabilityStatus status = looksLikePath(executable) && new File(executable).exists()
                    ? DreaminaCliAvailabilityStatus.EXECUTABLE_NOT_EXECUTABLE : DreaminaCliAvailabilityStatus.EXECUTABLE_NOT_FOUND;
            return unavailable(status, executable, null, "Canvas executable is unavailable", null);
        }
        DreaminaCanvasCliProperties probe = DreaminaCanvasCliProperties.copyOf(properties);
        long timeout = properties.getStartupProbeTimeoutMillis();
        probe.setCommandTimeoutMillis(timeout > 0 ? timeout : 30000L);
        try {
            DreaminaCanvasResponse<DreaminaVersion> result = new DreaminaCanvasCliExecutor(probe).version();
            if (!result.isSuccess()) {
                return unavailable(DreaminaCliAvailabilityStatus.NON_ZERO_EXIT, executable, resolved.get(),
                        "Canvas version returned an unsuccessful envelope", result.getRaw());
            }
            return DreaminaCliAvailabilityReport.builder().status(DreaminaCliAvailabilityStatus.AVAILABLE)
                    .available(true).configuredExecutable(executable).resolvedExecutablePath(resolved.get())
                    .message("Canvas version succeeded").probeResult(result.getRaw()).build();
        } catch (DreaminaCliTimeoutException ex) {
            return unavailable(DreaminaCliAvailabilityStatus.TIMEOUT, executable, resolved.get(), ex.getMessage(), ex.getPartialResult());
        } catch (DreaminaCliExecutableFailureException ex) {
            return unavailable(DreaminaCliAvailabilityStatus.SPAWN_FAILED, executable, resolved.get(), ex.getMessage(), ex.getPartialResult());
        } catch (DreaminaCliException ex) {
            return unavailable(DreaminaCliAvailabilityStatus.FAILED, executable, resolved.get(), ex.getMessage(), ex.getPartialResult());
        }
    }
}
