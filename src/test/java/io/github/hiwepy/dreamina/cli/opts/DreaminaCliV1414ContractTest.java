package io.github.hiwepy.dreamina.cli.opts;

import io.github.hiwepy.dreamina.cli.model.DreaminaGenerationStatus;
import io.github.hiwepy.dreamina.cli.model.DreaminaQueryResult;
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
 * Dreamina CLI v1.4.14 参数与状态契约测试。
 *
 * @author wandl
 * @since 2.0.0
 */
class DreaminaCliV1414ContractTest {

    @TempDir
    Path tempDir;

    @Test
    void text2Image_shouldAlwaysEmitResolutionType() {
        DreaminaText2ImageRequest request = DreaminaText2ImageRequest.builder()
            .prompt("一只橘猫")
            .build();

        assertTrue(request.toCliArgs().contains("--resolution_type=2k"));
    }

    @Test
    void text2Image_shouldSupportCustomWidthAndHeightWithoutRatio() {
        DreaminaText2ImageRequest request = DreaminaText2ImageRequest.builder()
            .prompt("产品海报")
            .width(1536)
            .height(2048)
            .build();

        List<String> args = request.toCliArgs();
        assertTrue(args.contains("--width=1536"));
        assertTrue(args.contains("--height=2048"));
        assertFalse(args.stream().anyMatch(arg -> arg.startsWith("--ratio=")));
    }

    @Test
    void text2Image_shouldRejectIncompleteOrConflictingCustomSize() {
        DreaminaText2ImageRequest widthOnly = DreaminaText2ImageRequest.builder()
            .prompt("产品海报")
            .width(1536)
            .build();
        DreaminaText2ImageRequest conflicting = DreaminaText2ImageRequest.builder()
            .prompt("产品海报")
            .ratio(DreaminaRatio.RATIO_3_4)
            .width(1536)
            .height(2048)
            .build();

        assertThrows(IllegalArgumentException.class, widthOnly::toCliArgs);
        assertThrows(IllegalArgumentException.class, conflicting::toCliArgs);
    }

    @Test
    void text2Image_shouldEnforceCustomSizeBoundsAndPixelBudget() {
        DreaminaText2ImageRequest sideTooSmall = DreaminaText2ImageRequest.builder()
            .prompt("产品海报")
            .resolutionType(DreaminaImageResolutionType.RESOLUTION_2K)
            .width(512)
            .height(1024)
            .build();
        DreaminaText2ImageRequest tooManyPixels = DreaminaText2ImageRequest.builder()
            .prompt("产品海报")
            .resolutionType(DreaminaImageResolutionType.RESOLUTION_2K)
            .width(3072)
            .height(3072)
            .build();
        DreaminaText2ImageRequest legacyModelWith1KCustomSize = DreaminaText2ImageRequest.builder()
            .prompt("产品海报")
            .modelVersion(DreaminaImageModelVersion.MODEL_3_1)
            .resolutionType(DreaminaImageResolutionType.RESOLUTION_1K)
            .width(1024)
            .height(1024)
            .build();

        assertThrows(IllegalArgumentException.class, sideTooSmall::toCliArgs);
        assertThrows(IllegalArgumentException.class, tooManyPixels::toCliArgs);
        assertThrows(IllegalArgumentException.class, legacyModelWith1KCustomSize::toCliArgs);
    }

    @Test
    void image2Image_shouldApplyTheSameCustomSizeContract() throws IOException {
        Path image = createTempFile("input.png");
        DreaminaImage2ImageRequest request = DreaminaImage2ImageRequest.builder()
            .image(image.toString())
            .prompt("改成水彩风格")
            .width(1536)
            .height(2048)
            .build();

        List<String> args = request.toCliArgs();
        assertTrue(args.contains("--width=1536"));
        assertTrue(args.contains("--height=2048"));
        assertTrue(args.contains("--resolution_type=2k"));
    }

    @Test
    void multiframe2Video_shouldEmitSupportedResolution() throws IOException {
        Path first = createTempFile("first.png");
        Path second = createTempFile("second.png");
        DreaminaMultiframe2VideoRequest defaultRequest = DreaminaMultiframe2VideoRequest.builder()
            .image(first.toString())
            .image(second.toString())
            .prompt("从白天过渡到夜晚")
            .build();
        DreaminaMultiframe2VideoRequest fullHdRequest = DreaminaMultiframe2VideoRequest.builder()
            .image(first.toString())
            .image(second.toString())
            .prompt("从白天过渡到夜晚")
            .videoResolution(DreaminaVideoResolutionType.RESOLUTION_1080P)
            .build();
        DreaminaMultiframe2VideoRequest invalidRequest = DreaminaMultiframe2VideoRequest.builder()
            .image(first.toString())
            .image(second.toString())
            .prompt("从白天过渡到夜晚")
            .videoResolution(DreaminaVideoResolutionType.RESOLUTION_4K)
            .build();

        assertTrue(defaultRequest.toCliArgs().contains("--video_resolution=720p"));
        assertTrue(fullHdRequest.toCliArgs().contains("--video_resolution=1080p"));
        assertThrows(IllegalArgumentException.class, invalidRequest::toCliArgs);
    }

    @Test
    void modelTokens_shouldMatchCurrentCliContract() {
        assertEquals("5.0Pro", DreaminaImageModelVersion.MODEL_5_0_PRO.getCliValue());
        assertEquals("seedance1.0fast", DreaminaVideoModelVersion.SEEDANCE_1_0_FAST.getCliValue());
        assertEquals("seedance1.0", DreaminaVideoModelVersion.SEEDANCE_1_0.getCliValue());
        assertEquals("seedance1.5pro", DreaminaVideoModelVersion.SEEDANCE_1_5_PRO.getCliValue());
    }

    @Test
    void videoResolution_shouldBeValidatedAgainstModel() {
        DreaminaText2VideoRequest invalid = DreaminaText2VideoRequest.builder()
            .prompt("镜头缓慢推进")
            .modelVersion(DreaminaVideoModelVersion.SEEDANCE_2_0_FAST)
            .videoResolution(DreaminaVideoResolutionType.RESOLUTION_1080P)
            .build();
        DreaminaText2VideoRequest valid = DreaminaText2VideoRequest.builder()
            .prompt("镜头缓慢推进")
            .modelVersion(DreaminaVideoModelVersion.SEEDANCE_2_0_VIP)
            .videoResolution(DreaminaVideoResolutionType.RESOLUTION_4K)
            .build();

        assertThrows(IllegalArgumentException.class, invalid::toCliArgs);
        assertTrue(valid.toCliArgs().contains("--video_resolution=4k"));
    }

    @Test
    void image2Video_shouldFollowCurrentModelPromptAndDurationContract() throws IOException {
        Path image = createTempFile("first-frame.png");
        DreaminaImage2VideoRequest missingPrompt = DreaminaImage2VideoRequest.builder()
            .imagePath(image.toString())
            .build();
        DreaminaImage2VideoRequest unsupportedModel = DreaminaImage2VideoRequest.builder()
            .imagePath(image.toString())
            .prompt("镜头推进")
            .modelVersion(DreaminaVideoModelVersion.SEEDANCE_1_0)
            .build();
        DreaminaImage2VideoRequest invalidDuration = DreaminaImage2VideoRequest.builder()
            .imagePath(image.toString())
            .prompt("镜头推进")
            .modelVersion(DreaminaVideoModelVersion.SEEDANCE_1_0_FAST)
            .durationSeconds(4)
            .build();

        assertThrows(IllegalArgumentException.class, missingPrompt::toCliArgs);
        assertThrows(IllegalArgumentException.class, unsupportedModel::toCliArgs);
        assertThrows(IllegalArgumentException.class, invalidDuration::toCliArgs);
    }

    @Test
    void multimodal2Video_shouldRejectAudioOnlyInput() throws IOException {
        Path audio = createTempFile("audio.mp3");
        DreaminaMultimodal2VideoRequest request = DreaminaMultimodal2VideoRequest.builder()
            .audio(audio.toString())
            .build();

        assertThrows(IllegalArgumentException.class, request::toCliArgs);
    }

    @Test
    void multiframe2Video_shouldRequireAtLeastTwoSecondsTotalDuration() throws IOException {
        Path first = createTempFile("story-first.png");
        Path second = createTempFile("story-second.png");
        DreaminaMultiframe2VideoRequest belowSegmentMinimum = DreaminaMultiframe2VideoRequest.builder()
            .image(first.toString())
            .image(second.toString())
            .prompt("过渡")
            .durationSeconds(0.5)
            .build();
        DreaminaMultiframe2VideoRequest belowTotalMinimum = DreaminaMultiframe2VideoRequest.builder()
            .image(first.toString())
            .image(second.toString())
            .prompt("过渡")
            .durationSeconds(1.0)
            .build();
        DreaminaMultiframe2VideoRequest valid = DreaminaMultiframe2VideoRequest.builder()
            .image(first.toString())
            .image(second.toString())
            .prompt("过渡")
            .durationSeconds(2.0)
            .build();

        assertThrows(IllegalArgumentException.class, belowSegmentMinimum::toCliArgs);
        assertThrows(IllegalArgumentException.class, belowTotalMinimum::toCliArgs);
        assertTrue(valid.toCliArgs().contains("--duration=2.0"));
    }

    @Test
    void multiframe2Video_shouldValidateEveryTransitionDuration() throws IOException {
        Path first = createTempFile("transition-first.png");
        Path second = createTempFile("transition-second.png");
        Path third = createTempFile("transition-third.png");
        DreaminaMultiframe2VideoRequest nonNumeric = DreaminaMultiframe2VideoRequest.builder()
            .image(first.toString())
            .image(second.toString())
            .image(third.toString())
            .transitionDuration("invalid")
            .transitionDuration("3")
            .build();
        DreaminaMultiframe2VideoRequest outOfRange = DreaminaMultiframe2VideoRequest.builder()
            .image(first.toString())
            .image(second.toString())
            .image(third.toString())
            .transitionDuration("0.5")
            .transitionDuration("3")
            .build();
        DreaminaMultiframe2VideoRequest valid = DreaminaMultiframe2VideoRequest.builder()
            .image(first.toString())
            .image(second.toString())
            .image(third.toString())
            .transitionDuration("1")
            .transitionDuration("1")
            .build();

        assertThrows(IllegalArgumentException.class, nonNumeric::toCliArgs);
        assertThrows(IllegalArgumentException.class, outOfRange::toCliArgs);
        assertTrue(valid.toCliArgs().contains("--transition-duration=1"));
    }

    @Test
    void queryResult_shouldExposeFailAsTerminalStatus() {
        DreaminaQueryResult result = new DreaminaQueryResult();
        result.setGenStatus("fail");
        result.setFailReason("insufficient credit");

        assertEquals(DreaminaGenerationStatus.FAIL, result.generationStatus());
        assertTrue(result.isGenFailed());
        assertTrue(result.isTerminal());
        assertFalse(result.isGenQuerying());
    }

    private Path createTempFile(String fileName) throws IOException {
        Path file = tempDir.resolve(fileName);
        Files.write(file, "demo".getBytes(StandardCharsets.UTF_8));
        return file;
    }
}
