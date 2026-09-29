package io.github.easy4j.dreamina.cli.opts;

import lombok.Builder;
import lombok.Getter;

/**
 * CompletionPowershell 补全脚本请求。
 */
@Getter
@Builder
public final class DreaminaCanvasCompletionPowershellRequest {
    private final boolean noDescriptions;
}
