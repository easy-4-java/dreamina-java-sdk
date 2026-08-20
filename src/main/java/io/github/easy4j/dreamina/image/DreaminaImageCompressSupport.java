package io.github.easy4j.dreamina.image;

import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Objects;

/**
 * Image byte compression utility for Dreamina generation results, based on Thumbnailator.
 *
 * <p>Follows the pattern of playwright-spring-boot-starter {@code WkhtmlToImageBufferRenderStrategy#compressScreenshot}:
 * reads from a byte stream, applies {@code scale} + {@code outputQuality}, and writes back to a byte array.</p>
 *
 * @see DreaminaImageCompressOptions
 * @see DreaminaImageCompressResult
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 */
public final class DreaminaImageCompressSupport {

    private static final int MIN_QUALITY = 1;
    private static final int MAX_QUALITY = 100;
    private static final double MIN_SCALE = 0.01d;
    private static final double MAX_SCALE = 8.0d;

    private DreaminaImageCompressSupport() {
    }

    /**
     * Compresses image bytes per configuration; returns as-is when disabled or input is empty.
     *
     * @param source     Original image bytes
     * @param formatHint Format hint (URL suffix or filename, e.g., {@code .jpg}, {@code png})
     * @param options    压缩选项
     * @return Compressed bytes; returns {@code source} itself when not compressed
     * @throws IOException Image is unreadable or encoding failed
     */
    public static byte[] compressIfEnabled(byte[] source, String formatHint, DreaminaImageCompressOptions options)
            throws IOException {
        DreaminaImageCompressResult result = compress(source, formatHint, options);
        return result.getBytes();
    }

    /**
     * Compresses image bytes per configuration and returns a summary.
     *
     * @param source     原始图片字节
     * @param formatHint 格式提示
     * @param options    压缩选项
     * @return Compression result
     * @throws IOException 图片Unreadable或Encoding failed
     */
    public static DreaminaImageCompressResult compress(byte[] source, String formatHint,
            DreaminaImageCompressOptions options) throws IOException {
        Objects.requireNonNull(source, "source");
        if (options == null || !options.isEnabled() || source.length == 0) {
            return DreaminaImageCompressResult.builder()
                    .bytes(source)
                    .applied(false)
                    .originalSize(source.length)
                    .compressedSize(source.length)
                    .scale(1.0d)
                    .quality(clampQuality(options != null ? options.getQuality() : MAX_QUALITY))
                    .outputFormat(resolveOutputFormat(formatHint, null))
                    .build();
        }

        BufferedImage image = decodeImage(source);
        return compressDecoded(image, source.length, formatHint, options);
    }

    /**
     * Compresses a {@link BufferedImage} to bytes (for testing or local disk-read scenarios).
     *
     * @param image      Original image
     * @param formatHint 格式提示
     * @param options    压缩选项
     * @return 压缩结果
     * @throws IOException 编码失败
     */
    public static DreaminaImageCompressResult compress(BufferedImage image, String formatHint,
            DreaminaImageCompressOptions options) throws IOException {
        Objects.requireNonNull(image, "image");
        if (options == null || !options.isEnabled()) {
            String outputFormat = resolveOutputFormat(formatHint, null);
            byte[] bytes = encodeImage(image, outputFormat);
            return DreaminaImageCompressResult.builder()
                    .bytes(bytes)
                    .applied(false)
                    .originalSize(bytes.length)
                    .compressedSize(bytes.length)
                    .scale(1.0d)
                    .quality(clampQuality(options != null ? options.getQuality() : MAX_QUALITY))
                    .outputFormat(outputFormat)
                    .build();
        }
        String outputFormat = resolveOutputFormat(formatHint, null);
        byte[] baseline = encodeImage(image, outputFormat);
        return compressDecoded(image, baseline.length, formatHint, options);
    }

    /**
     * Decodes bytes into a {@link BufferedImage} in a single pass.
     *
     * @param source 原始图片字节
     * @return 解码后的图片
     * @throws IOException 不可读
     */
    private static BufferedImage decodeImage(byte[] source) throws IOException {
        try (ByteArrayInputStream input = new ByteArrayInputStream(source)) {
            BufferedImage image = ImageIO.read(input);
            if (image == null) {
                throw new IOException("Source bytes are not a readable image");
            }
            return image;
        }
    }

    /**
     * Applies Thumbnailator compression to a decoded image and encodes to bytes (single in-memory pass, no repeated stream reads).
     *
     * @param image           Decoded original image
     * @param originalByteSize Original byte length (for result summary)
     * @param formatHint      格式提示
     * @param options         Compression options (confirmed enabled)
     * @return 压缩结果
     * @throws IOException Compression or encoding failed
     */
    private static DreaminaImageCompressResult compressDecoded(BufferedImage image, int originalByteSize,
            String formatHint, DreaminaImageCompressOptions options) throws IOException {
        double scale = normalizeScale(options.getScale());
        int quality = clampQuality(options.getQuality());
        String outputFormat = resolveOutputFormat(formatHint, null);

        byte[] compressed = encodeCompressed(image, outputFormat, scale, quality);
        if (compressed.length == 0) {
            throw new IOException("Thumbnailator produced empty output");
        }

        boolean applied = compressed.length != originalByteSize
                || Math.abs(scale - 1.0d) >= 1e-6
                || quality < MAX_QUALITY;
        return DreaminaImageCompressResult.builder()
                .bytes(compressed)
                .applied(applied)
                .originalSize(originalByteSize)
                .compressedSize(compressed.length)
                .scale(scale)
                .quality(quality)
                .outputFormat(outputFormat)
                .build();
    }

    /**
     * Writes the Thumbnailator-scaled/quality-adjusted result to a byte array.
     *
     * @param image        原图
     * @param outputFormat ImageIO format name
     * @param scale        缩放比例
     * @param quality      输出质量 1–100
     * @return 压缩后字节
     * @throws IOException Processing failed
     */
    private static byte[] encodeCompressed(BufferedImage image, String outputFormat, double scale, int quality)
            throws IOException {
        Thumbnails.Builder<BufferedImage> builder = Thumbnails.of(image)
                .allowOverwrite(true)
                .outputFormat(outputFormat)
                .outputQuality(quality / 100f);
        if (Math.abs(scale - 1.0d) < 1e-6) {
            builder.scale(1f);
        } else {
            builder.scale((float) scale);
        }
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            builder.toOutputStream(output);
            return output.toByteArray();
        }
    }

    /**
     * Encodes a {@link BufferedImage} to bytes (no scaling).
     *
     * @param image        图片
     * @param outputFormat ImageIO 格式名
     * @return Encoded bytes
     * @throws IOException 编码失败
     */
    private static byte[] encodeImage(BufferedImage image, String outputFormat) throws IOException {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            if (!ImageIO.write(image, outputFormat, output)) {
                throw new IOException("Cannot encode image as " + outputFormat);
            }
            return output.toByteArray();
        }
    }

    /**
     * Normalizes the scale factor to the valid range.
     *
     * @param scale Raw scale factor
     * @return Valid scale factor
     */
    static double normalizeScale(double scale) {
        if (Double.isNaN(scale) || Double.isInfinite(scale)) {
            return 1.0d;
        }
        if (scale < MIN_SCALE) {
            return MIN_SCALE;
        }
        if (scale > MAX_SCALE) {
            return MAX_SCALE;
        }
        return scale;
    }

    /**
     * Clamps the quality to the 1-100 range.
     *
     * @param quality Raw quality
     * @return Valid quality
     */
    static int clampQuality(int quality) {
        if (quality < MIN_QUALITY) {
            return MIN_QUALITY;
        }
        if (quality > MAX_QUALITY) {
            return MAX_QUALITY;
        }
        return quality;
    }

    /**
     * Infers the ImageIO output format name from a URL/filename suffix.
     *
     * @param formatHint   Suffix or format name
     * @param defaultFormat Default format
     * @return jpg / png / gif / bmp
     */
    static String resolveOutputFormat(String formatHint, String defaultFormat) {
        String fallback = defaultFormat != null ? defaultFormat : "jpg";
        if (formatHint == null || formatHint.trim().isEmpty()) {
            return fallback;
        }
        String normalized = formatHint.trim().toLowerCase(Locale.ROOT);
        if (normalized.startsWith(".")) {
            normalized = normalized.substring(1);
        }
        switch (normalized) {
            case "png":
                return "png";
            case "gif":
                return "gif";
            case "bmp":
                return "bmp";
            case "jpeg":
            case "jpg":
            case "webp":
                return "jpg";
            default:
                return fallback;
        }
    }
}
