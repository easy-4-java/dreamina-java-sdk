package io.github.easy4j.dreamina.cli.opts;

import lombok.Getter;

/**
 * Image resolution type enum.
 *
 * @see DreaminaText2ImageRequest
 * @see DreaminaImageUpscaleRequest
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 */
@Getter
public enum DreaminaImageResolutionType {

    RESOLUTION_1K("1k"),
    RESOLUTION_2K("2k"),
    RESOLUTION_4K("4k"),
    /** 仅 {@code image_upscale} 支持；4k/8k 需 VIP。 */
    RESOLUTION_8K("8k");

    private final String cliValue;

    DreaminaImageResolutionType(String cliValue) {
        this.cliValue = cliValue;
    }
}
