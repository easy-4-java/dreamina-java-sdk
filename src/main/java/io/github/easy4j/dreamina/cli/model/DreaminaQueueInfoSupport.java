package io.github.easy4j.dreamina.cli.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.easy4j.dreamina.util.DreaminaStrings;

/**
 * Post-mapping support for {@link DreaminaQueryQueueInfo} (parses the embedded {@code debug_info}).
 *
 * @see DreaminaQueryQueueInfo#parsedDebugInfo
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 */
public final class DreaminaQueueInfoSupport {

    private DreaminaQueueInfoSupport() {
    }

    /**
     * If {@code debug_info} is a non-empty JSON string, populates {@link DreaminaQueryQueueInfo#getParsedDebugInfo()}.
     *
     * @param objectMapper Jackson ObjectMapper; must not be null
     * @param queueInfo    Queue info object; may be null
     */
    public static void enrichParsedDebugInfo(ObjectMapper objectMapper, DreaminaQueryQueueInfo queueInfo) {
        if (queueInfo == null || DreaminaStrings.isBlank(queueInfo.getDebugInfo())) {
            return;
        }
        try {
            DreaminaQueryQueueDebugInfo parsed =
                objectMapper.readValue(queueInfo.getDebugInfo().trim(), DreaminaQueryQueueDebugInfo.class);
            queueInfo.setParsedDebugInfo(parsed);
        } catch (Exception ignored) {
            // 保留原始 debug_info 字符串，不阻断主流程
        }
    }
}
