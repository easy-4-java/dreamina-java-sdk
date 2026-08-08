package io.github.easy4j.dreamina.exception;

import io.github.easy4j.dreamina.cli.DreaminaCliResult;
/**
 * Thrown when the CLI cannot be started at the OS level (e.g., command not found or invalid path).
 *
 * @see DreaminaCliException
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 */
public class DreaminaCliExecutableFailureException extends DreaminaCliException {

    /**
     * @param message Failure description for logging
     * @param cause   Typically a {@link java.io.IOException}
     */
    public DreaminaCliExecutableFailureException(String message, Throwable cause) {
        super(message, cause, null);
    }
}
