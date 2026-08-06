package io.github.easy4j.dreamina.cli.model;

import java.util.Locale;
import java.util.Objects;

/**
 * Dreamina CLI 生成任务状态。
 *
 * @author wandl
 * @since 2.0.0
 */
public enum DreaminaGenerationStatus {

    UNKNOWN,
    QUERYING,
    SUCCESS,
    FAIL;

    /**
     * 将 CLI 状态转换为稳定的 SDK 枚举。
     *
     * @param cliValue CLI 返回的 gen_status
     * @return SDK 状态；未知值返回 {@link #UNKNOWN}
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
     * @return 成功或失败均为终态
     */
    public boolean isTerminal() {
        return this == SUCCESS || this == FAIL;
    }
}
