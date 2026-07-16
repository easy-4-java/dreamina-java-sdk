package io.github.hiwepy.dreamina.cli.opts;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

/**
 * 文生图请求对象。
 * <p>
 * 适配即梦 CLI v1.4.x：
 * <ul>
 *   <li>v1.4.4（2026-06-03）起支持 {@code Seedream 4.7}</li>
 *   <li>v1.4.12（2026-07-15）起支持 {@code Seedream 5.0 Pro}（最强旗舰）</li>
 *   <li>v1.4.10（2026-06-26）起支持 {@code --generate_num} 批量出图（1-10 张）→ {@link #generateNum}</li>
 * </ul>
 * </p>
 *
 * @author wandl
 * @since 1.0.0
 */
@Getter
@Builder
public class DreaminaText2ImageRequest implements DreaminaCliArgumentProvider {

    /**
     * 必填提示词。
     */
    private final String prompt;

    /**
     * 可选宽高比。
     */
    private final DreaminaRatio ratio;

    /**
     * 图像模型版本。
     * <p>
     * 默认使用 {@link DreaminaImageModelVersion#MODEL_5_0}（CLI v1.4.12 旗舰），
     * 调用方仍可显式覆盖为 4.7 / 5.0 Pro / 3.x 等。
     * </p>
     */
    @Builder.Default
    private final DreaminaImageModelVersion modelVersion = DreaminaImageModelVersion.MODEL_5_0;

    /**
     * 可选分辨率。
     */
    private final DreaminaImageResolutionType resolutionType;

    /**
     * 单次生成图片数量。CLI v1.4.10（2026-06-26）起支持，范围 1-10。
     * <p>
     * 留空则不传 {@code --generate_num}，沿用 CLI 默认（1 张）。
     * </p>
     */
    private final Integer generateNum;

    /**
     * 会话 ID。
     */
    private final Long sessionId;

    /**
     * poll 秒数；0 表示异步。
     */
    private final Integer pollSeconds;

    /**
     * 额外原生参数。
     */
    @Singular("additionalArg")
    private final List<String> additionalRawArgs;

    @Override
    public List<String> toCliArgs() {
        List<String> args = new ArrayList<>();
        DreaminaCliRequestSupport.addFlag(args, "--prompt", DreaminaCliRequestSupport.requireNonBlank(prompt, "prompt"));
        DreaminaCliRequestSupport.addFlag(args, "--ratio", ratio == null ? null : ratio.getCliValue());
        DreaminaCliRequestSupport.addFlag(args, "--model_version", modelVersion == null ? null : modelVersion.getCliValue());
        DreaminaCliRequestSupport.addFlag(args, "--resolution_type", resolutionType == null ? null : resolutionType.getCliValue());
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
