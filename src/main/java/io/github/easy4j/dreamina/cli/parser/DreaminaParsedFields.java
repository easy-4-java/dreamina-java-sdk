package io.github.easy4j.dreamina.cli.parser;

import lombok.Builder;
import lombok.Getter;

/**
 * Best-effort structured parse snapshot of Dreamina CLI output.
 * <p>
 * The actual CLI copy or format may evolve; when parsing fails the caller must be allowed
 * to degrade to relying solely on the raw text from {@link DreaminaCliResult#getStdout()}
 * and {@link DreaminaCliResult#getStderr()}; all fields in this type may be null.
 * </p>
 *
 * @see DreaminaCliOutputParser#parseBestEffort(String, String)
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 */
@Getter
@Builder
public class DreaminaParsedFields {

    /**
     * The submit ID that may appear after a successful task submission.
     */
    private final String submitId;

    /**
     * User credit/quota value identified from the output (semantics defined by the Dreamina CLI).
     */
    private final Long credit;

    /**
     * Whether follow-up polling is recommended (best-effort boolean hint from the output; null when unrecognizable).
     */
    private final Boolean pollRecommended;
}
