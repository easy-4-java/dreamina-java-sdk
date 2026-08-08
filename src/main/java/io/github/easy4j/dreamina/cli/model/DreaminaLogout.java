package io.github.easy4j.dreamina.cli.model;

import lombok.Builder;
import lombok.Getter;

/**
 * Parsed body for {@code dreamina logout}.
 *
 * @see io.github.easy4j.dreamina.cli.DreaminaCliExecutor#logout()
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 */
@Getter
@Builder
public class DreaminaLogout {

    private final Boolean localSessionCleared;
}
