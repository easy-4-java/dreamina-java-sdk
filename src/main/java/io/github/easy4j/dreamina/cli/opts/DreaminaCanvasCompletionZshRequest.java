package io.github.easy4j.dreamina.cli.opts;

import lombok.Builder;
import lombok.Getter;

/**
 * CompletionZsh 补全脚本请求。
 */
@Getter
@Builder
public final class DreaminaCanvasCompletionZshRequest {
    private final boolean noDescriptions;
}
