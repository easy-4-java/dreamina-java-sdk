package io.github.easy4j.dreamina.exception;

import io.github.easy4j.dreamina.cli.DreaminaCliResult;
import lombok.Getter;

/**
 * Base exception for Dreamina CLI execution-layer failures.
 * <p>
 * Wraps diagnostic information and an optional snapshot from a single process invocation,
 * allowing the upper layer to decide whether to retry, degrade, or alert; does not couple business copy.
 * </p>
 *
 * @see io.github.easy4j.dreamina.cli.DreaminaCliResult
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 */
@Getter
public class DreaminaCliException extends RuntimeException {

    /** The latest observable result (e.g., output captured before a timeout or non-zero exit) */
    private final DreaminaCliResult partialResult;

    /**
     * Constructs a Dreamina CLI execution exception.
     *
     * @param message        Technical description (for logging)
     * @param cause          Root cause
     * @param partialResult  The result snapshot formed before the failure, or null if unavailable
     */
    public DreaminaCliException(String message, Throwable cause, DreaminaCliResult partialResult) {
        super(message, cause);
        this.partialResult = partialResult;
    }

    /**
     * Constructs an execution exception without a {@link #cause}.
     *
     * @param message       技术性说明（面向日志）
     * @param partialResult Partial result snapshot
     */
    public DreaminaCliException(String message, DreaminaCliResult partialResult) {
        super(message);
        this.partialResult = partialResult;
    }
}
