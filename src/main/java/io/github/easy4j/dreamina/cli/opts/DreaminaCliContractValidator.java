package io.github.easy4j.dreamina.cli.opts;

import io.github.easy4j.dreamina.util.DreaminaStrings;
import java.util.List;
import java.util.Objects;

/**
 * Centralized validator for Dreamina CLI's volatile parameter contracts.
 *
 * @see DreaminaCliRequestSupport
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 */
public final class DreaminaCliContractValidator {

    private DreaminaCliContractValidator() {
    }

    /**
     * Validates the v1.4.14 custom image size contract.
     *
     * @param width  Custom width
     * @param height Custom height
     * @param ratio  Aspect ratio
     */
    public static void validateCustomImageSize(Integer width, Integer height, DreaminaRatio ratio) {
        boolean hasWidth = Objects.nonNull(width);
        boolean hasHeight = Objects.nonNull(height);
        if (hasWidth != hasHeight) {
            throw new IllegalArgumentException("width and height must be provided together");
        }
        if (hasWidth && Objects.nonNull(ratio)) {
            throw new IllegalArgumentException("width and height cannot be used with ratio");
        }
        if (hasWidth && (width <= 0 || height <= 0)) {
            throw new IllegalArgumentException("width and height must be positive");
        }
    }

    /**
     * Validates the side-length and total-pixel limits for v1.4.14～v1.4.16 custom image sizes.
     *
     * @param width          自定义宽度
     * @param height         自定义高度
     * @param modelVersion   Image model
     * @param resolutionType Image resolution
     */
    public static void validateCustomImageBounds(
        Integer width,
        Integer height,
        DreaminaImageModelVersion modelVersion,
        DreaminaImageResolutionType resolutionType) {
        if (Objects.isNull(width) || Objects.isNull(height)) {
            return;
        }
        requireImageResolution(resolutionType);
        if ((modelVersion == DreaminaImageModelVersion.MODEL_3_0
            || modelVersion == DreaminaImageModelVersion.MODEL_3_1)
            && resolutionType != DreaminaImageResolutionType.RESOLUTION_2K) {
            throw new IllegalArgumentException("Seedream 3.x custom width/height requires 2k resolution");
        }

        int minSide;
        int maxSide;
        long maxPixels;
        switch (resolutionType) {
            case RESOLUTION_1K:
                minSide = 512;
                maxSide = 2016;
                maxPixels = 1_763_584L;
                break;
            case RESOLUTION_1_5K:
                minSide = 972;
                maxSide = 2268;
                maxPixels = 2_359_296L;
                break;
            case RESOLUTION_2K:
                minSide = 768;
                maxSide = 3072;
                maxPixels = 4_194_304L;
                break;
            case RESOLUTION_4K:
                minSide = 1536;
                maxSide = 6240;
                maxPixels = 16_777_216L;
                break;
            default:
                throw new IllegalArgumentException("custom width/height does not support 8k resolution");
        }
        if (width < minSide || width > maxSide || height < minSide || height > maxSide) {
            throw new IllegalArgumentException(
                "width and height must each be in range [" + minSide + ", " + maxSide + "]");
        }
        if ((long) width * height > maxPixels) {
            throw new IllegalArgumentException(
                "width * height must not exceed " + maxPixels + " pixels for " + resolutionType.getCliValue());
        }
    }

    /**
     * Validates and returns the image resolution.
     *
     * @param resolutionType 图片分辨率
     * @return Non-null image resolution
     */
    public static DreaminaImageResolutionType requireImageResolution(
        DreaminaImageResolutionType resolutionType) {
        if (Objects.isNull(resolutionType)) {
            throw new IllegalArgumentException("resolutionType is required by Dreamina CLI v1.4.14+");
        }
        return resolutionType;
    }

    /**
     * Validates the image model and resolution combination.
     *
     * @param modelVersion   图片模型
     * @param resolutionType 图片分辨率
     */
    public static void validateImageModelResolution(
        DreaminaImageModelVersion modelVersion,
        DreaminaImageResolutionType resolutionType) {
        requireImageResolution(resolutionType);
        if (resolutionType == DreaminaImageResolutionType.RESOLUTION_8K) {
            throw new IllegalArgumentException("image generation does not support 8k resolution");
        }
        DreaminaImageModelVersion effectiveModel = Objects.isNull(modelVersion)
            ? DreaminaImageModelVersion.MODEL_5_0
            : modelVersion;
        if ((effectiveModel == DreaminaImageModelVersion.MODEL_3_0
            || effectiveModel == DreaminaImageModelVersion.MODEL_3_1)
            && resolutionType != DreaminaImageResolutionType.RESOLUTION_1K
            && resolutionType != DreaminaImageResolutionType.RESOLUTION_2K) {
            throw new IllegalArgumentException("Seedream 3.x only supports 1k or 2k resolution");
        }
        if (effectiveModel == DreaminaImageModelVersion.MODEL_5_0_PRO
            && resolutionType != DreaminaImageResolutionType.RESOLUTION_1_5K
            && resolutionType != DreaminaImageResolutionType.RESOLUTION_2K
            && resolutionType != DreaminaImageResolutionType.RESOLUTION_4K) {
            throw new IllegalArgumentException("Seedream 5.0 Pro only supports 1.5k, 2k or 4k resolution");
        }
        if (effectiveModel != DreaminaImageModelVersion.MODEL_3_0
            && effectiveModel != DreaminaImageModelVersion.MODEL_3_1
            && effectiveModel != DreaminaImageModelVersion.MODEL_5_0_PRO
            && (resolutionType == DreaminaImageResolutionType.RESOLUTION_1K
                || resolutionType == DreaminaImageResolutionType.RESOLUTION_1_5K)) {
            throw new IllegalArgumentException("Seedream 4.x/5.0 only supports 2k or 4k resolution");
        }
    }

    /**
     * Validates the video model and resolution combination.
     *
     * @param modelVersion    Video model
     * @param videoResolution Video resolution
     */
    public static void validateVideoModelResolution(
        DreaminaVideoModelVersion modelVersion,
        DreaminaVideoResolutionType videoResolution) {
        if (Objects.isNull(videoResolution)) {
            throw new IllegalArgumentException("videoResolution is required by Dreamina CLI v1.4.14+");
        }
        if (videoResolution == DreaminaVideoResolutionType.RESOLUTION_480P
            && modelVersion != DreaminaVideoModelVersion.SEEDANCE_2_5) {
            throw new IllegalArgumentException("480p video requires seedance2.5");
        }
        if (videoResolution == DreaminaVideoResolutionType.RESOLUTION_4K
            && modelVersion != DreaminaVideoModelVersion.SEEDANCE_2_0_VIP) {
            throw new IllegalArgumentException("4k video requires seedance2.0_vip");
        }
        if (videoResolution == DreaminaVideoResolutionType.RESOLUTION_1080P
            && modelVersion != DreaminaVideoModelVersion.SEEDANCE_2_0_VIP
            && modelVersion != DreaminaVideoModelVersion.SEEDANCE_2_5) {
            throw new IllegalArgumentException("1080p video requires seedance2.0_vip or seedance2.5");
        }
    }

    /**
     * Validates the multiframe video resolution.
     *
     * @param videoResolution 视频分辨率
     */
    public static void validateMultiframeResolution(DreaminaVideoResolutionType videoResolution) {
        if (Objects.isNull(videoResolution)) {
            throw new IllegalArgumentException("videoResolution is required by Dreamina CLI v1.4.14+");
        }
        if (videoResolution == DreaminaVideoResolutionType.RESOLUTION_4K) {
            throw new IllegalArgumentException("multiframe2video only supports 720p or 1080p resolution");
        }
    }

    /**
     * Validates the per-segment duration for two-image multiframe videos.
     *
     * @param durationSeconds Per-segment duration; defaults to 3 seconds in CLI when omitted
     */
    public static void validateMultiframeDuration(Double durationSeconds) {
        if (Objects.isNull(durationSeconds)) {
            return;
        }
        validateFiniteDuration(durationSeconds, "durationSeconds");
        if (durationSeconds < 1.0 || durationSeconds > 8.0) {
            throw new IllegalArgumentException("durationSeconds must be in range [1.0, 8.0]");
        }
        if (durationSeconds < 2.0) {
            throw new IllegalArgumentException("multiframe2video total duration must be at least 2 seconds");
        }
    }

    /**
     * Validates per-segment durations for three or more images.
     *
     * @param transitionDurations Per-segment durations in CLI stringArray format
     */
    public static void validateMultiframeTransitionDurations(List<String> transitionDurations) {
        if (Objects.isNull(transitionDurations) || transitionDurations.isEmpty()) {
            return;
        }
        double totalDuration = 0.0;
        for (int i = 0; i < transitionDurations.size(); i++) {
            String rawDuration = transitionDurations.get(i);
            if (DreaminaStrings.isBlank(rawDuration)) {
                throw new IllegalArgumentException("transitionDurations[" + i + "] must not be blank");
            }
            double duration;
            try {
                duration = Double.parseDouble(rawDuration.trim());
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException(
                    "transitionDurations[" + i + "] must be a number", ex);
            }
            validateFiniteDuration(duration, "transitionDurations[" + i + "]");
            if (duration < 1.0 || duration > 8.0) {
                throw new IllegalArgumentException(
                    "transitionDurations[" + i + "] must be in range [1.0, 8.0]");
            }
            totalDuration += duration;
        }
        if (totalDuration < 2.0) {
            throw new IllegalArgumentException("multiframe2video total duration must be at least 2 seconds");
        }
    }

    private static void validateFiniteDuration(double duration, String label) {
        if (Double.isNaN(duration) || Double.isInfinite(duration)) {
            throw new IllegalArgumentException(label + " must be a finite number");
        }
    }
}
