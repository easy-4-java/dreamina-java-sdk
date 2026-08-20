package io.github.easy4j.dreamina.cli.opts;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Dreamina CLI v1.4.15 新增 Seedance 2.5 / 480P / 多模态纯音频契约测试。
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 2.0.0
 */
class DreaminaCliV1415ContractTest {

    @TempDir
    Path tempDir;

    @Test
    void videoResolution_shouldAccept480pForSeedance25() {
        DreaminaText2VideoRequest valid = DreaminaText2VideoRequest.builder()
            .prompt("480P 测试")
            .modelVersion(DreaminaVideoModelVersion.SEEDANCE_2_5)
            .videoResolution(DreaminaVideoResolutionType.RESOLUTION_480P)
            .durationSeconds(10)
            .build();

        List<String> args = valid.toCliArgs();
        assertTrue(args.contains("--model_version=seedance2.5"));
        assertTrue(args.contains("--video_resolution=480p"));
        assertTrue(args.contains("--duration=10"));
    }

    @Test
    void videoResolution_shouldReject480pForOtherModels() {
        DreaminaText2VideoRequest fastWith480p = DreaminaText2VideoRequest.builder()
            .prompt("尝试 480p")
            .modelVersion(DreaminaVideoModelVersion.SEEDANCE_2_0_FAST)
            .videoResolution(DreaminaVideoResolutionType.RESOLUTION_480P)
            .build();
        DreaminaText2VideoRequest vipWith480p = DreaminaText2VideoRequest.builder()
            .prompt("尝试 480p")
            .modelVersion(DreaminaVideoModelVersion.SEEDANCE_2_0_VIP)
            .videoResolution(DreaminaVideoResolutionType.RESOLUTION_480P)
            .build();

        assertThrows(IllegalArgumentException.class, fastWith480p::toCliArgs);
        assertThrows(IllegalArgumentException.class, vipWith480p::toCliArgs);
    }

    @Test
    void seedance25_shouldAllowDurationRange4To30() throws IOException {
        Path image = createTempFile("seedance25-first.png");
        Path image2 = createTempFile("seedance25-last.png");
        Path audio = createTempFile("seedance25.mp3");

        DreaminaText2VideoRequest minDuration = DreaminaText2VideoRequest.builder()
            .prompt("4 秒")
            .modelVersion(DreaminaVideoModelVersion.SEEDANCE_2_5)
            .videoResolution(DreaminaVideoResolutionType.RESOLUTION_720P)
            .durationSeconds(4)
            .build();
        DreaminaText2VideoRequest maxDuration = DreaminaText2VideoRequest.builder()
            .prompt("30 秒")
            .modelVersion(DreaminaVideoModelVersion.SEEDANCE_2_5)
            .videoResolution(DreaminaVideoResolutionType.RESOLUTION_720P)
            .durationSeconds(30)
            .build();
        DreaminaFrames2VideoRequest fullRange = DreaminaFrames2VideoRequest.builder()
            .firstImagePath(image.toString())
            .lastImagePath(image2.toString())
            .prompt("过渡")
            .modelVersion(DreaminaVideoModelVersion.SEEDANCE_2_5)
            .videoResolution(DreaminaVideoResolutionType.RESOLUTION_720P)
            .durationSeconds(30)
            .build();
        DreaminaMultimodal2VideoRequest multimodalMax = DreaminaMultimodal2VideoRequest.builder()
            .image(image.toString())
            .audio(audio.toString())
            .prompt("30s + 音频")
            .modelVersion(DreaminaVideoModelVersion.SEEDANCE_2_5)
            .videoResolution(DreaminaVideoResolutionType.RESOLUTION_720P)
            .durationSeconds(30)
            .build();

        assertTrue(minDuration.toCliArgs().contains("--duration=4"));
        assertTrue(maxDuration.toCliArgs().contains("--duration=30"));
        assertTrue(fullRange.toCliArgs().contains("--duration=30"));
        assertTrue(multimodalMax.toCliArgs().contains("--duration=30"));
    }

    @Test
    void seedance25_shouldRejectDuration3And31() {
        DreaminaText2VideoRequest belowMin = DreaminaText2VideoRequest.builder()
            .prompt("3 秒")
            .modelVersion(DreaminaVideoModelVersion.SEEDANCE_2_5)
            .videoResolution(DreaminaVideoResolutionType.RESOLUTION_720P)
            .durationSeconds(3)
            .build();
        DreaminaText2VideoRequest aboveMax = DreaminaText2VideoRequest.builder()
            .prompt("31 秒")
            .modelVersion(DreaminaVideoModelVersion.SEEDANCE_2_5)
            .videoResolution(DreaminaVideoResolutionType.RESOLUTION_720P)
            .durationSeconds(31)
            .build();

        assertThrows(IllegalArgumentException.class, belowMin::toCliArgs);
        assertThrows(IllegalArgumentException.class, aboveMax::toCliArgs);
    }

    @Test
    void multimodal2Video_shouldAcceptAudioOnlyWhenSeedance25() throws IOException {
        Path audio = createTempFile("seedance25-only.mp3");
        DreaminaMultimodal2VideoRequest request = DreaminaMultimodal2VideoRequest.builder()
            .audio(audio.toString())
            .prompt("纯音频生成")
            .modelVersion(DreaminaVideoModelVersion.SEEDANCE_2_5)
            .videoResolution(DreaminaVideoResolutionType.RESOLUTION_720P)
            .durationSeconds(10)
            .build();

        List<String> args = request.toCliArgs();
        assertTrue(args.contains("--audio=" + audio.toString()));
        assertTrue(args.contains("--model_version=seedance2.5"));
        assertFalse(args.stream().anyMatch(arg -> arg.startsWith("--image=")));
        assertFalse(args.stream().anyMatch(arg -> arg.startsWith("--video=")));
    }

    @Test
    void multimodal2Video_shouldStillRequireImageOrVideoForOtherModels() throws IOException {
        Path audio = createTempFile("non-seedance25.mp3");
        DreaminaMultimodal2VideoRequest audioOnlyLegacy = DreaminaMultimodal2VideoRequest.builder()
            .audio(audio.toString())
            .modelVersion(DreaminaVideoModelVersion.SEEDANCE_2_0_VIP)
            .videoResolution(DreaminaVideoResolutionType.RESOLUTION_720P)
            .build();
        DreaminaMultimodal2VideoRequest empty = DreaminaMultimodal2VideoRequest.builder()
            .modelVersion(DreaminaVideoModelVersion.SEEDANCE_2_0_VIP)
            .videoResolution(DreaminaVideoResolutionType.RESOLUTION_720P)
            .build();

        assertThrows(IllegalArgumentException.class, audioOnlyLegacy::toCliArgs);
        assertThrows(IllegalArgumentException.class, empty::toCliArgs);
    }

    @Test
    void modelTokens_shouldMatchV1415CliContract() {
        assertEquals("seedance2.5", DreaminaVideoModelVersion.SEEDANCE_2_5.getCliValue());
        assertEquals("480p", DreaminaVideoResolutionType.RESOLUTION_480P.getCliValue());
    }

    @Test
    void seedance25_shouldSupportTextVideoImageAndMultimodalCommands() {
        assertTrue(DreaminaVideoModelVersion.SEEDANCE_2_5.supportsText2Video());
        assertTrue(DreaminaVideoModelVersion.SEEDANCE_2_5.supportsImage2Video());
        assertTrue(DreaminaVideoModelVersion.SEEDANCE_2_5.supportsFrames2Video());
        assertTrue(DreaminaVideoModelVersion.SEEDANCE_2_5.supportsMultimodal2Video());
    }

    private Path createTempFile(String fileName) throws IOException {
        Path file = tempDir.resolve(fileName);
        Files.write(file, "demo".getBytes(StandardCharsets.UTF_8));
        return file;
    }
}
