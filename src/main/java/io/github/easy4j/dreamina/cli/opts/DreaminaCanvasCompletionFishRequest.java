package io.github.easy4j.dreamina.cli.opts;

import lombok.Builder;
import lombok.Getter;

/**
 * CompletionFish 补全脚本请求。
 */
@Getter
@Builder
public final class DreaminaCanvasCompletionFishRequest {
    private final boolean noDescriptions;
}
