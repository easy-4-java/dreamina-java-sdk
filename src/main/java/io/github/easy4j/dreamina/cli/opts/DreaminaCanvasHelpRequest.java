package io.github.easy4j.dreamina.cli.opts;

import lombok.Builder;
import lombok.Getter;

/**
 * 命令帮助请求。
 */
@Getter
@Builder
public final class DreaminaCanvasHelpRequest {
    private final String topic;
}
