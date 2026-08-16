package io.github.easy4j.dreamina.cli.model;

import java.util.Locale;
import java.util.Objects;

/**
 * Dreamina CLI generation task status.
 *
 * @see DreaminaGenerateSubmit
 * @see DreaminaQueryResult
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 */
public enum DreaminaGenerationStatus {

    UNKNOWN,
    QUERYING,
    SUCCESS,
    FAIL;

    /**
     * Converts the CLI status string to a stable SDK enum value.
     *
     * @param cliValue The gen_status value returned by the CLI
     * @return The SDK status; unknown values return {@link #UNKNOWN}
     */
    public static DreaminaGenerationStatus fromCliValue(String cliValue) {
        if (Objects.isNull(cliValue) || cliValue.trim().isEmpty()) {
            return UNKNOWN;
        }
        String normalized = cliValue.trim().toLowerCase(Locale.ROOT);
        if ("querying".equals(normalized)) {
            return QUERYING;
        }
        if ("success".equals(normalized)) {
            return SUCCESS;
        }
        if ("fail".equals(normalized) || "failed".equals(normalized)) {
            return FAIL;
        }
        return UNKNOWN;
    }

    /**
     * @return Whether the status is terminal (success or fail).
     */
    public boolean isTerminal() {
        return this == SUCCESS || this == FAIL;
    }
}
