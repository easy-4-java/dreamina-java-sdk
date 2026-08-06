package io.github.easy4j.dreamina.cli.model;

import lombok.Builder;
import lombok.Getter;

/**
 * {@code dreamina logout} 解析体。
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
@Getter
@Builder
public class DreaminaLogout {

    private final Boolean localSessionCleared;
}
