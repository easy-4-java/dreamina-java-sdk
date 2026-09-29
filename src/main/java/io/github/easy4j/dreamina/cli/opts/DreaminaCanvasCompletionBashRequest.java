package io.github.easy4j.dreamina.cli.opts;

import lombok.Builder;
import lombok.Getter;

/**
 * CompletionBash 补全脚本请求。
 */
@Getter
@Builder
public final class DreaminaCanvasCompletionBashRequest {
    private final boolean noDescriptions;
}
