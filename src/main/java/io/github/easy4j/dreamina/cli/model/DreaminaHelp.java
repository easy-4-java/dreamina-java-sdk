package io.github.easy4j.dreamina.cli.model;

import lombok.Builder;
import lombok.Getter;

/**
 * Parsed body for {@code dreamina help} (topic; full text is in {@link io.github.easy4j.dreamina.cli.DreaminaCliResponse#getStdout()}).
 *
 * @see io.github.easy4j.dreamina.cli.DreaminaCliExecutor#help()
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 */
@Getter
@Builder
public class DreaminaHelp {

    private final String topic;
}
