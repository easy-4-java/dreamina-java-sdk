package io.github.easy4j.dreamina.cli.opts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

/**
 * 多模态视频请求对象。
 * <p>
 * 适配即梦 CLI v1.4.15（2026-08-01）新增 Seedance 2.5：当 {@link #modelVersion} 显式指定为
 * {@link DreaminaVideoModelVersion#SEEDANCE_2_5} 时，{@code audios} 字段可单独提供（即「纯音频输入」），
 * 并允许时长范围 4～30 秒；其他模型仍要求至少一个 {@code --image} 或 {@code --video} 输入。
 * </p>
 *
 * @author wandl
 * @since 1.0.0
 */
@Getter
@Builder
public class DreaminaMultimodal2VideoRequest implements DreaminaCliArgumentProvider {

    @Singular("image")
    private final List<String> images;

    @Singular("video")
    private final List<String> videos;

    @Singular("audio")
    private final List<String> audios;

    private final String prompt;
    private final Integer durationSeconds;
    private final DreaminaRatio ratio;

    @Builder.Default
    private final DreaminaVideoModelVersion modelVersion = DreaminaVideoModelVersion.SEEDANCE_2_0_VIP;

    @Builder.Default
    private final DreaminaVideoResolutionType videoResolution = DreaminaVideoResolutionType.RESOLUTION_720P;

    private final Long sessionId;
    private final Integer pollSeconds;

    @Singular("additionalArg")
    private final List<String> additionalRawArgs;

    @Override
    public List<String> toCliArgs() {
        // CLI v1.4.15（2026-08-01）：Seedance 2.5 放宽输入数量上限
        //   - image≤30 / video≤10 / audio≤10（其它模型维持 image≤9 / video≤3 / audio≤3）
        boolean seedance25 = modelVersion == DreaminaVideoModelVersion.SEEDANCE_2_5;
        int maxImages = seedance25 ? 30 : 9;
        int maxVideos = seedance25 ? 10 : 3;
        int maxAudios = seedance25 ? 10 : 3;
        List<String> cleanedImages = images == null || images.isEmpty() ? Collections.emptyList()
            : DreaminaCliRequestSupport.requireReadableFiles(images, "images", 1, maxImages);
        List<String> cleanedVideos = videos == null || videos.isEmpty() ? Collections.emptyList()
            : DreaminaCliRequestSupport.requireReadableFiles(videos, "videos", 1, maxVideos);
        List<String> cleanedAudios = audios == null || audios.isEmpty() ? Collections.emptyList()
            : DreaminaCliRequestSupport.requireReadableFiles(audios, "audios", 1, maxAudios);
        if (cleanedImages.isEmpty() && cleanedVideos.isEmpty() && cleanedAudios.isEmpty()) {
            throw new IllegalArgumentException(
                "multimodal2video requires at least one image/video/audio input");
        }
        if (cleanedImages.isEmpty() && cleanedVideos.isEmpty() && cleanedAudios.isEmpty() == false) {
            // cleanedAudios non-empty here; only Seedance 2.5 (v1.4.15) allows audio-only.
            if (modelVersion != DreaminaVideoModelVersion.SEEDANCE_2_5) {
                throw new IllegalArgumentException(
                    "multimodal2video audio-only input requires seedance2.5 (Dreamina CLI v1.4.15+)");
            }
        }
        if (modelVersion != null && !modelVersion.supportsMultimodal2Video()) {
            throw new IllegalArgumentException("multimodal2video requires seedance model family");
        }
        DreaminaCliContractValidator.validateVideoModelResolution(modelVersion, videoResolution);
        List<String> args = new ArrayList<>();
        DreaminaCliRequestSupport.addRepeatedFlag(args, "--image", cleanedImages);
        DreaminaCliRequestSupport.addRepeatedFlag(args, "--video", cleanedVideos);
        DreaminaCliRequestSupport.addRepeatedFlag(args, "--audio", cleanedAudios);
        DreaminaCliRequestSupport.addFlag(args, "--prompt", prompt);
        DreaminaCliRequestSupport.requireVideoDuration(durationSeconds, modelVersion, "durationSeconds");
        DreaminaCliRequestSupport.addFlag(args, "--duration", durationSeconds);
        DreaminaCliRequestSupport.addFlag(args, "--ratio", ratio == null ? null : ratio.getCliValue());
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
