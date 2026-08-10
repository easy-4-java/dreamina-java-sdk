package io.github.easy4j.dreamina.image;

import lombok.Builder;
import lombok.Value;

/**
 * Compression options for Dreamina generation results (pure POJO, no Spring coupling).
 *
 * <p>Aligned with the Thumbnailator usage in playwright-spring-boot-starter:
 * {@code scale} controls proportional scaling, {@code quality} controls lossy encoding quality.</p>
 *
 * @see DreaminaImageCompressSupport
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 */
@Value
@Builder
public class DreaminaImageCompressOptions {

    /**
     * Whether compression is enabled; when false, {@link DreaminaImageCompressSupport} returns the input bytes as-is.
     */
    @Builder.Default
    boolean enabled = false;

    /**
     * Proportional scale factor: {@code (0,1]} to shrink, {@code 1} for quality-only compression without resizing, {@code >1} to enlarge.
     */
    @Builder.Default
    double scale = 1.0d;

    /**
     * Output quality (1-100), mapped to Thumbnailator {@code outputQuality(quality/100f)}.
     */
    @Builder.Default
    int quality = 85;

    /**
     * Constructs compression options from business configuration.
     *
     * @param enabled Compression toggle
     * @param scale   Scale factor
     * @param quality Output quality 1-100
     * @return Compression options
     */
    public static DreaminaImageCompressOptions of(boolean enabled, double scale, int quality) {
        return DreaminaImageCompressOptions.builder()
                .enabled(enabled)
                .scale(scale)
                .quality(quality)
                .build();
    }
}
