package io.github.easy4j.dreamina.cli.opts;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

/**
 * 图生图Image-to-image request object.
 * <p>
 * Requires 1-10 local reference images, model version 4.0+, and resolution 2k/4k.
 * </p>
 *
 * @see io.github.easy4j.dreamina.cli.DreaminaCliExecutor#image2Image(String, String, java.util.List)
 * <p>
 * 按 Jimeng 技能约束：必须提供 1-10 张本地图片，且模型版本需为 4.0+，分辨率仅支持 2k/4k。
 * </p>
 * <p>
 * 适配即梦 CLI v1.4.x：
 * <ul>
 *   <li>v1.4.10（2026-06-26）起支持 {@code --generate_num} 批量出图（1-10 张）→ {@link #generateNum}</li>
 *   <li>v1.4.4 起支持 4.7、v1.4.12 起支持 5.0 Pro</li>
 * </ul>
 * </p>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 */
@Getter
@Builder
public class DreaminaImage2ImageRequest implements DreaminaCliArgumentProvider {

    /**
     * 参考图片列表（1-10 张本地路径）。
     */
    @Singular("image")
    private final List<String> images;

    /**
     * 编辑提示词。
     */
    private final String prompt;

    /**
     * 可选宽高比。
     */
    private final DreaminaRatio ratio;

    /**
     * 图生图默认使用 {@link DreaminaImageModelVersion#MODEL_5_0}（CLI v1.4.12 旗舰）。
     */
    @Builder.Default
    private final DreaminaImageModelVersion modelVersion = DreaminaImageModelVersion.MODEL_5_0;

    /**
     * 图生图默认 2k。
     */
    @Builder.Default
    private final DreaminaImageResolutionType resolutionType = DreaminaImageResolutionType.RESOLUTION_2K;

    /** 自定义宽度，必须与 height 成对提供，并与 ratio 互斥。 */
    private final Integer width;

    /** 自定义高度，必须与 width 成对提供，并与 ratio 互斥。 */
    private final Integer height;

    /**
     * 单次生成图片数量。CLI v1.4.10 起支持，范围 1-10。留空沿用 CLI 默认（1 张）。
     */
    private final Integer generateNum;

    /**
     * 会话 ID。
     */
    private final Long sessionId;

    /**
     * poll 秒数。
     */
    private final Integer pollSeconds;

    /**
     * 额外原生参数。
     */
    @Singular("additionalArg")
    private final List<String> additionalRawArgs;

    @Override
    public List<String> toCliArgs() {
        List<String> cleanedImages = DreaminaCliRequestSupport.requireReadableFiles(images, "images", 1, 10);
        DreaminaCliContractValidator.validateCustomImageSize(width, height, ratio);
        DreaminaCliContractValidator.validateImageModelResolution(modelVersion, resolutionType);
        DreaminaCliContractValidator.validateCustomImageBounds(
            width, height, modelVersion, resolutionType);
        if (modelVersion == null || !modelVersion.supportsImageToImage()) {
            throw new IllegalArgumentException("image2image requires modelVersion 4.0+");
        }
        List<String> args = new ArrayList<>();
        DreaminaCliRequestSupport.addFlag(args, "--images", DreaminaCliRequestSupport.csv(cleanedImages));
        DreaminaCliRequestSupport.addFlag(args, "--prompt", DreaminaCliRequestSupport.requireNonBlank(prompt, "prompt"));
        DreaminaCliRequestSupport.addFlag(args, "--ratio", ratio == null ? null : ratio.getCliValue());
        DreaminaCliRequestSupport.addFlag(args, "--model_version", modelVersion.getCliValue());
        DreaminaCliRequestSupport.addFlag(args, "--resolution_type", resolutionType.getCliValue());
        DreaminaCliRequestSupport.addFlag(args, "--width", width);
        DreaminaCliRequestSupport.addFlag(args, "--height", height);
        if (generateNum != null) {
            DreaminaCliRequestSupport.requireRange(generateNum, 1, 10, "generateNum");
            DreaminaCliRequestSupport.addFlag(args, "--generate_num", generateNum);
        }
        DreaminaCliRequestSupport.requireSessionId(sessionId);
        DreaminaCliRequestSupport.addFlag(args, "--session", sessionId);
        DreaminaCliRequestSupport.requireNonNegative(pollSeconds, "pollSeconds");
        DreaminaCliRequestSupport.addFlag(args, "--poll", pollSeconds);
        DreaminaCliRequestSupport.addAdditionalArgs(args, additionalRawArgs);
        return args;
    }
}
