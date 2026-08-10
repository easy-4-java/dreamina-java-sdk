package io.github.easy4j.dreamina.exception;

import io.github.easy4j.dreamina.cli.DreaminaCliResult;
/**
 * Thrown when the CLI process exits with an unexpected non-zero exit code (semantics interpreted by the upper layer).
 *
 * @see DreaminaCliException
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 */
public class DreaminaCliNonZeroExitException extends DreaminaCliException {

    /**
     * @param message Descriptive message
     * @param result  The assembled complete snapshot ({@link DreaminaCliResult#success} is typically false)
     */
    public DreaminaCliNonZeroExitException(String message, DreaminaCliResult result) {
        super(message, result);
    }
}
