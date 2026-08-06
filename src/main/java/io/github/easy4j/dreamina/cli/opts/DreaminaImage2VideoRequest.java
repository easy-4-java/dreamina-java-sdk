package io.github.easy4j.dreamina.cli.opts;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

/**
 * 单图生视频请求对象。
 *
 * @author wandl
 * @since 1.0.0
 */
@Getter
@Builder
public class DreaminaImage2VideoRequest implements DreaminaCliArgumentProvider {

    /**
     * 单张本地参考图。
     */
    private final String imagePath;

    /**
     * 运动提示词，可选。
     */
    private final String prompt;

    /**
     * 时长（秒）。
     */
    private final Integer durationSeconds;

    /**
     * 视频模型版本。
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
        if (modelVersion != null && !modelVersion.supportsImage2Video()) {
            throw new IllegalArgumentException("image2video requires a current Seedance model");
        }
        DreaminaCliContractValidator.validateVideoModelResolution(modelVersion, videoResolution);
        List<String> args = new ArrayList<>();
        DreaminaCliRequestSupport.addFlag(
            args, "--image", DreaminaCliRequestSupport.requireReadableFile(imagePath, "imagePath"));
        DreaminaCliRequestSupport.addFlag(
            args, "--prompt", DreaminaCliRequestSupport.requireNonBlank(prompt, "prompt"));
        DreaminaCliRequestSupport.requireVideoDuration(durationSeconds, modelVersion, "durationSeconds");
        DreaminaCliRequestSupport.addFlag(args, "--duration", durationSeconds);
        DreaminaCliRequestSupport.addFlag(args, "--model_version", modelVersion == null ? null : modelVersion.getCliValue());
        DreaminaCliRequestSupport.addFlag(args, "--video_resolution", videoResolution.getCliValue());
        DreaminaCliRequestSupport.requireSessionId(sessionId);
        DreaminaCliRequestSupport.addFlag(args, "--session", sessionId);
        DreaminaCliRequestSupport.requireNonNegative(pollSeconds, "pollSeconds");
        DreaminaCliRequestSupport.addFlag(args, "--poll", pollSeconds);
        DreaminaCliRequestSupport.addAdditionalArgs(args, additionalRawArgs);
        return args;
    }
}
