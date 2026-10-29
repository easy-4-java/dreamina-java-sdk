package io.github.easy4j.dreamina.cli.opts;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 首尾帧视频First-last-frames video request object.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @see io.github.easy4j.dreamina.cli.DreaminaCliExecutor#frames2video()
 * @since 3.0.0
 */
@Getter
@Builder
public class DreaminaFrames2VideoRequest implements DreaminaCliArgumentProvider {

    /**
     * 起始帧图片。
     */
    private final String firstImagePath;

    /**
     * 结束帧图片。
     */
    private final String lastImagePath;

    /**
     * 过渡提示词。
     */
    private final String prompt;

    /**
     * 时长（秒）。
     */
    private final Integer durationSeconds;

    /** 可选视频比例；省略时由 CLI 使用默认行为。 */
    private final DreaminaRatio ratio;

    /**
     * 模型版本。
     */
    @Builder.Default
    private final DreaminaVideoModelVersion modelVersion = DreaminaVideoModelVersion.SEEDANCE_2_0_VIP;

    /**
     * 视频分辨率。
     */
    @Builder.Default
    private final DreaminaVideoResolutionType videoResolution = DreaminaVideoResolutionType.RESOLUTION_720P;

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
        DreaminaCliContractValidator.validateVideoRatio(ratio, modelVersion, true);
        if (Objects.nonNull(modelVersion) && !modelVersion.supportsFrames2Video()) {
            throw new IllegalArgumentException("frames2video requires a current Seedance model");
        }
        DreaminaCliContractValidator.validateVideoModelResolution(modelVersion, videoResolution);
        List<String> args = new ArrayList<>();
        DreaminaCliRequestSupport.addFlag(
                args, "--first", DreaminaCliRequestSupport.requireReadableFile(firstImagePath, "firstImagePath"));
        DreaminaCliRequestSupport.addFlag(
                args, "--last", DreaminaCliRequestSupport.requireReadableFile(lastImagePath, "lastImagePath"));
        DreaminaCliRequestSupport.addFlag(args, "--prompt", prompt);
        DreaminaCliRequestSupport.requireVideoDuration(durationSeconds, modelVersion, "durationSeconds");
        DreaminaCliRequestSupport.addFlag(args, "--duration", durationSeconds);
        DreaminaCliRequestSupport.addFlag(args, "--ratio", Objects.isNull(ratio) ? null : ratio.getCliValue());
        DreaminaCliRequestSupport.addFlag(args, "--model_version", Objects.isNull(modelVersion) ? null : modelVersion.getCliValue());
        DreaminaCliRequestSupport.addFlag(args, "--video_resolution", videoResolution.getCliValue());
        DreaminaCliRequestSupport.requireSessionId(sessionId);
        DreaminaCliRequestSupport.addFlag(args, "--session", sessionId);
        DreaminaCliRequestSupport.requireNonNegative(pollSeconds, "pollSeconds");
        DreaminaCliRequestSupport.addFlag(args, "--poll", pollSeconds);
        DreaminaCliRequestSupport.addAdditionalArgs(args, additionalRawArgs);
        return args;
    }
}
