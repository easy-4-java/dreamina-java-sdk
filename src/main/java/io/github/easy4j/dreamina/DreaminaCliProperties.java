package io.github.easy4j.dreamina;

import lombok.Data;

/**
 * Runtime configuration POJO for the Dreamina CLI subprocess client (no Spring coupling).
 * <p>
 * Describes the executable path, working directory, per-command timeout, and optional
 * default polling interval for orchestration layers. This class solely handles
 * "how to launch a subprocess" and does not carry business orchestration logic.
 * In a Spring Boot application, the upper layer can use
 * {@code @ConfigurationProperties(prefix = "dreamina.cli")} to bind the same fields.
 * </p>
 *
 * @see io.github.easy4j.dreamina.cli.DreaminaCliExecutor
 * @see io.github.easy4j.dreamina.cli.support.SubprocessExecutionSupport
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 */
@Data
public class DreaminaCliProperties {

    /**
     * Dreamina CLI executable name or absolute path.
     * <p>Defaults to assuming {@code dreamina} is directly callable from {@code PATH}.</p>
     */
    private String executable = "dreamina";

    /**
     * Working directory for the subprocess; when empty, the current JVM working directory is used.
     */
    private String workingDirectory;

    /**
     * Per CLI invocation timeout in milliseconds.
     * <p>
     * Used by {@link org.apache.commons.exec.ExecuteWatchdog}; upon timeout the subprocess
     * is terminated and mapped to an execution-layer timeout exception.
     * </p>
     */
    private long commandTimeoutMillis = 120_000L;

    /**
     * Maximum concurrent CLI subprocess executions; when less than or equal to 0, defaults to the greater of the CPU core count and 2.
     */
    private int maxConcurrentExecutions = 0;

    /**
     * Startup probe timeout in milliseconds ({@code dreamina version}); when less than or equal to 0, defaults to 30 seconds.
     * <p>Does not affect the business-call {@link #commandTimeoutMillis}.</p>
     */
    private long startupProbeTimeoutMillis = 30_000L;

    /**
     * Default polling interval for orchestration layers (seconds).
     * <p>
     * A reusable default for sleep intervals between asynchronous chains such as
     * {@code query_result} and {@code text2image}; this executor only exposes
     * the configuration and does not implement the actual polling logic.
     * </p>
     */
    private int defaultPollIntervalSeconds = 5;
}
