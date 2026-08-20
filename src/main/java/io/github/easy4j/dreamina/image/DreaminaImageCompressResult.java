package io.github.easy4j.dreamina.image;

import lombok.Builder;
import lombok.Value;

/**
 * Image compression result summary.
 *
 * @see DreaminaImageCompressSupport#compress(byte[], String, DreaminaImageCompressOptions)
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 */
@Value
@Builder
public class DreaminaImageCompressResult {

    /** Compressed image bytes. */
    byte[] bytes;

    /** Whether compression was actually applied (toggle enabled and output differs from input or size/quality adjusted). */
    boolean applied;

    /** Byte length before compression. */
    int originalSize;

    /** Byte length after compression. */
    int compressedSize;

    /** Scale factor actually used. */
    double scale;

    /** Output quality actually used (1-100). */
    int quality;

    /** Output ImageIO format name (e.g., jpg, png). */
    String outputFormat;
}
