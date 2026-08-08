package io.github.easy4j.dreamina.exception;

import io.github.easy4j.dreamina.cli.DreaminaCliResult;
/**
 * Thrown when the ExecuteWatchdog triggers: the subprocess did not finish within the configured timeout.
 *
 * @see DreaminaCliException
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 */
public class DreaminaCliTimeoutException extends DreaminaCliException {

    /**
     * @param message        说明性消息
     * @param partialResult  Partial output captured before the timeout, or null if none was available
     */
    public DreaminaCliTimeoutException(String message, DreaminaCliResult partialResult) {
        super(message, partialResult);
    }
}
