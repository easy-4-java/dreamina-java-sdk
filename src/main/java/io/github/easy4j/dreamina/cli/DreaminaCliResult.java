package io.github.easy4j.dreamina.cli;

import io.github.easy4j.dreamina.cli.DreaminaCliExecutor;
import io.github.easy4j.dreamina.cli.parser.DreaminaParsedFields;
import lombok.Builder;
import lombok.Getter;

/**
 * Standard result carrier for a single Dreamina CLI invocation.
 * <p>
 * Always preserves raw stdout / stderr along with the exit code and success flag;
 * structured fields are accessible on demand via {@link #getParsed()}.
 * When the underlying layer determines a non-zero exit, timeout, or executable unavailability,
 * the exception model carries the latest snapshot. {@link DreaminaCliExecutor} guarantees
 * {@link #success} is {@code true} on the normal return path.
 * </p>
 *
 * @see DreaminaCliExecutor
 * @see DreaminaCliResponse
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 */
@Getter
@Builder
public class DreaminaCliResult {

    /**
     * Full standard output text.
     */
    private final String stdout;

    /**
     * Full standard error text.
     */
    private final String stderr;

    /**
     * Process exit code; may be {@code null} if the process did not produce a normal exit code.
     */
    private final Integer exitCode;

    /**
     * Whether the result represents success consistent with the current execution-layer contract.
     */
    private final boolean success;

    /**
     * Best-effort parsed structured summary.
     */
    private final DreaminaParsedFields parsed;
}
