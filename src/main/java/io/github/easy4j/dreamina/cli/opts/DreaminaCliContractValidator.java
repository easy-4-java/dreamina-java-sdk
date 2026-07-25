package io.github.easy4j.dreamina.cli.opts;

import java.util.Objects;

/**
 * Dreamina CLI 易变参数契约的集中校验器。
 *
 * @author wandl
 * @since 2.0.0
 */
public final class DreaminaCliContractValidator {

    private DreaminaCliContractValidator() {
    }

    /**
     * 校验 v1.4.14 自定义图片尺寸契约。
     *
     * @param width  自定义宽度
     * @param height 自定义高度
     * @param ratio  宽高比
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
     * 校验 v1.4.14 自定义图片尺寸的边长与总像素限制。
     *
     * @param width          自定义宽度
     * @param height         自定义高度
     * @param modelVersion   图片模型
     * @param resolutionType 图片分辨率
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
     * 校验并返回图片分辨率。
     *
     * @param resolutionType 图片分辨率
     * @return 非空图片分辨率
     */
    public static DreaminaImageResolutionType requireImageResolution(
        DreaminaImageResolutionType resolutionType) {
        if (Objects.isNull(resolutionType)) {
            throw new IllegalArgumentException("resolutionType is required by Dreamina CLI v1.4.14+");
        }
        return resolutionType;
    }

    /**
     * 校验图片模型与分辨率组合。
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
        if (Objects.isNull(modelVersion)) {
            return;
        }
        if ((modelVersion == DreaminaImageModelVersion.MODEL_3_0
            || modelVersion == DreaminaImageModelVersion.MODEL_3_1)
            && resolutionType != DreaminaImageResolutionType.RESOLUTION_1K
            && resolutionType != DreaminaImageResolutionType.RESOLUTION_2K) {
            throw new IllegalArgumentException("Seedream 3.x only supports 1k or 2k resolution");
        }
        if (modelVersion != DreaminaImageModelVersion.MODEL_3_0
            && modelVersion != DreaminaImageModelVersion.MODEL_3_1
            && modelVersion != DreaminaImageModelVersion.MODEL_5_0_PRO
            && resolutionType == DreaminaImageResolutionType.RESOLUTION_1K) {
            throw new IllegalArgumentException("Seedream 4.x/5.0 only supports 2k or 4k resolution");
        }
    }

    /**
     * 校验视频模型与分辨率组合。
     *
     * @param modelVersion   视频模型
     * @param videoResolution 视频分辨率
     */
    public static void validateVideoModelResolution(
        DreaminaVideoModelVersion modelVersion,
        DreaminaVideoResolutionType videoResolution) {
        if (Objects.isNull(videoResolution)) {
            throw new IllegalArgumentException("videoResolution is required by Dreamina CLI v1.4.14+");
        }
        if (videoResolution != DreaminaVideoResolutionType.RESOLUTION_720P
            && modelVersion != DreaminaVideoModelVersion.SEEDANCE_2_0_VIP) {
            throw new IllegalArgumentException("1080p/4k video requires seedance2.0_vip");
        }
    }

    /**
     * 校验智能多帧分辨率。
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
}
