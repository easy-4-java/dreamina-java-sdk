package io.github.easy4j.dreamina.cli.opts;

import lombok.Getter;

/**
 * Image resolution type enum.
 *
 * @see DreaminaText2ImageRequest
 * @see DreaminaImageUpscaleRequest
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 */
@Getter
public enum DreaminaImageResolutionType {

    RESOLUTION_1K("1k"),
    RESOLUTION_2K("2k"),
    RESOLUTION_4K("4k"),
    /** 仅 {@code image_upscale} 支持；4k/8k 需 VIP。 */
    RESOLUTION_8K("8k"),
    /** CLI v1.4.16（2026-08-14）新增，仅 Seedream 5.0 Pro 支持。 */
    RESOLUTION_1_5K("1.5k");

    private final String cliValue;

    DreaminaImageResolutionType(String cliValue) {
        this.cliValue = cliValue;
    }
}
