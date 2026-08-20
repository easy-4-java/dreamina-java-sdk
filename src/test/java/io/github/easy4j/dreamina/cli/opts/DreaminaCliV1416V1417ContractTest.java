package io.github.easy4j.dreamina.cli.opts;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Dreamina CLI v1.4.16 与 v1.4.17 分辨率契约测试。
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 */
class DreaminaCliV1416V1417ContractTest {

    @TempDir
    Path tempDir;

    @Test
    void imageResolution_shouldExpose15kCliToken() {
        assertTrue(Arrays.stream(DreaminaImageResolutionType.values())
            .anyMatch(resolution -> "1.5k".equals(resolution.getCliValue())));
    }

    @Test
    void seedream50Pro_shouldAccept15kAndRejectRemoved1k() {
        DreaminaText2ImageRequest supported = DreaminaText2ImageRequest.builder()
            .prompt("1.5k 海报")
            .modelVersion(DreaminaImageModelVersion.MODEL_5_0_PRO)
            .resolutionType(DreaminaImageResolutionType.RESOLUTION_1_5K)
            .build();
        DreaminaText2ImageRequest removed = DreaminaText2ImageRequest.builder()
            .prompt("旧 1k 参数")
            .modelVersion(DreaminaImageModelVersion.MODEL_5_0_PRO)
            .resolutionType(DreaminaImageResolutionType.RESOLUTION_1K)
            .build();
        DreaminaText2ImageRequest preservedSeedream31 = DreaminaText2ImageRequest.builder()
            .prompt("3.1 模型继续支持 1k")
            .modelVersion(DreaminaImageModelVersion.MODEL_3_1)
            .resolutionType(DreaminaImageResolutionType.RESOLUTION_1K)
            .build();

        assertTrue(supported.toCliArgs().contains("--resolution_type=1.5k"));
        assertThrows(IllegalArgumentException.class, removed::toCliArgs);
        assertTrue(preservedSeedream31.toCliArgs().contains("--resolution_type=1k"));
    }

    @Test
    void seedream50Pro_shouldApply15kCustomSizeBounds() {
        DreaminaText2ImageRequest minimum = DreaminaText2ImageRequest.builder()
            .prompt("最小尺寸")
            .modelVersion(DreaminaImageModelVersion.MODEL_5_0_PRO)
            .resolutionType(DreaminaImageResolutionType.RESOLUTION_1_5K)
            .width(972)
            .height(972)
            .build();
        DreaminaText2ImageRequest sideTooLarge = DreaminaText2ImageRequest.builder()
            .prompt("边长超限")
            .modelVersion(DreaminaImageModelVersion.MODEL_5_0_PRO)
            .resolutionType(DreaminaImageResolutionType.RESOLUTION_1_5K)
            .width(2269)
            .height(972)
            .build();
        DreaminaText2ImageRequest sideTooSmall = DreaminaText2ImageRequest.builder()
            .prompt("边长低于下限")
            .modelVersion(DreaminaImageModelVersion.MODEL_5_0_PRO)
            .resolutionType(DreaminaImageResolutionType.RESOLUTION_1_5K)
            .width(971)
            .height(972)
            .build();
        DreaminaText2ImageRequest maximumSide = DreaminaText2ImageRequest.builder()
            .prompt("边长恰好达到上限")
            .modelVersion(DreaminaImageModelVersion.MODEL_5_0_PRO)
            .resolutionType(DreaminaImageResolutionType.RESOLUTION_1_5K)
            .width(2268)
            .height(972)
            .build();
        DreaminaText2ImageRequest pixelsTooLarge = DreaminaText2ImageRequest.builder()
            .prompt("像素超限")
            .modelVersion(DreaminaImageModelVersion.MODEL_5_0_PRO)
            .resolutionType(DreaminaImageResolutionType.RESOLUTION_1_5K)
            .width(2268)
            .height(2268)
            .build();
        DreaminaText2ImageRequest exactPixelBudget = DreaminaText2ImageRequest.builder()
            .prompt("恰好达到像素预算")
            .modelVersion(DreaminaImageModelVersion.MODEL_5_0_PRO)
            .resolutionType(DreaminaImageResolutionType.RESOLUTION_1_5K)
            .width(1536)
            .height(1536)
            .build();
        DreaminaText2ImageRequest justOverPixelBudget = DreaminaText2ImageRequest.builder()
            .prompt("刚刚超过像素预算")
            .modelVersion(DreaminaImageModelVersion.MODEL_5_0_PRO)
            .resolutionType(DreaminaImageResolutionType.RESOLUTION_1_5K)
            .width(1536)
            .height(1537)
            .build();

        List<String> args = assertDoesNotThrow(minimum::toCliArgs);
        assertTrue(args.contains("--width=972"));
        assertTrue(args.contains("--height=972"));
        assertThrows(IllegalArgumentException.class, sideTooSmall::toCliArgs);
        assertThrows(IllegalArgumentException.class, sideTooLarge::toCliArgs);
        assertThrows(IllegalArgumentException.class, pixelsTooLarge::toCliArgs);
        assertDoesNotThrow(maximumSide::toCliArgs);
        assertDoesNotThrow(exactPixelBudget::toCliArgs);
        assertThrows(IllegalArgumentException.class, justOverPixelBudget::toCliArgs);
    }

    @Test
    void imageGeneration_shouldReject15kForModelsOtherThanSeedream50Pro() {
        DreaminaText2ImageRequest seedream50 = DreaminaText2ImageRequest.builder()
            .prompt("不支持 1.5k")
            .modelVersion(DreaminaImageModelVersion.MODEL_5_0)
            .resolutionType(DreaminaImageResolutionType.RESOLUTION_1_5K)
            .build();
        DreaminaText2ImageRequest seedream31 = DreaminaText2ImageRequest.builder()
            .prompt("不支持 1.5k")
            .modelVersion(DreaminaImageModelVersion.MODEL_3_1)
            .resolutionType(DreaminaImageResolutionType.RESOLUTION_1_5K)
            .build();

        assertThrows(IllegalArgumentException.class, seedream50::toCliArgs);
        assertThrows(IllegalArgumentException.class, seedream31::toCliArgs);
    }

    @Test
    void imageGeneration_shouldUseCliDefaultModelContractWhenModelIsNull() {
        DreaminaText2ImageRequest removed1k = DreaminaText2ImageRequest.builder()
            .prompt("默认模型不能使用 1k")
            .modelVersion(null)
            .resolutionType(DreaminaImageResolutionType.RESOLUTION_1K)
            .build();
        DreaminaText2ImageRequest unsupported15k = DreaminaText2ImageRequest.builder()
            .prompt("默认模型不能使用 1.5k")
            .modelVersion(null)
            .resolutionType(DreaminaImageResolutionType.RESOLUTION_1_5K)
            .build();
        DreaminaText2ImageRequest supported2k = DreaminaText2ImageRequest.builder()
            .prompt("默认模型支持 2k")
            .modelVersion(null)
            .resolutionType(DreaminaImageResolutionType.RESOLUTION_2K)
            .build();

        assertThrows(IllegalArgumentException.class, removed1k::toCliArgs);
        assertThrows(IllegalArgumentException.class, unsupported15k::toCliArgs);
        List<String> args = assertDoesNotThrow(supported2k::toCliArgs);
        assertFalse(args.stream().anyMatch(argument -> argument.startsWith("--model_version=")));
    }

    @Test
    void image2Image_shouldAccept15kOnlyForSeedream50Pro() throws IOException {
        Path image = createTempFile("image2image-15k.png");
        DreaminaImage2ImageRequest supported = DreaminaImage2ImageRequest.builder()
            .image(image.toString())
            .prompt("1.5k 重绘")
            .modelVersion(DreaminaImageModelVersion.MODEL_5_0_PRO)
            .resolutionType(DreaminaImageResolutionType.RESOLUTION_1_5K)
            .build();
        DreaminaImage2ImageRequest unsupported = DreaminaImage2ImageRequest.builder()
            .image(image.toString())
            .prompt("1.5k 重绘")
            .modelVersion(DreaminaImageModelVersion.MODEL_4_7)
            .resolutionType(DreaminaImageResolutionType.RESOLUTION_1_5K)
            .build();

        assertTrue(supported.toCliArgs().contains("--resolution_type=1.5k"));
        assertThrows(IllegalArgumentException.class, unsupported::toCliArgs);
    }

    @Test
    void imageUpscale_shouldReject15k() throws IOException {
        Path image = createTempFile("upscale-15k.png");
        DreaminaImageUpscaleRequest request = DreaminaImageUpscaleRequest.builder()
            .imagePath(image.toString())
            .resolutionType(DreaminaImageResolutionType.RESOLUTION_1_5K)
            .build();

        assertThrows(IllegalArgumentException.class, request::toCliArgs);
    }

    @Test
    void seedance25_shouldEmit1080pAcrossSupportedVideoCommands() throws IOException {
        Path first = createTempFile("first.png");
        Path last = createTempFile("last.png");
        Path audio = createTempFile("audio.mp3");

        List<String> textArgs = DreaminaText2VideoRequest.builder()
            .prompt("1080p 文生视频")
            .modelVersion(DreaminaVideoModelVersion.SEEDANCE_2_5)
            .videoResolution(DreaminaVideoResolutionType.RESOLUTION_1080P)
            .build()
            .toCliArgs();
        List<String> imageArgs = DreaminaImage2VideoRequest.builder()
            .imagePath(first.toString())
            .prompt("1080p 图生视频")
            .modelVersion(DreaminaVideoModelVersion.SEEDANCE_2_5)
            .videoResolution(DreaminaVideoResolutionType.RESOLUTION_1080P)
            .build()
            .toCliArgs();
        List<String> framesArgs = DreaminaFrames2VideoRequest.builder()
            .firstImagePath(first.toString())
            .lastImagePath(last.toString())
            .prompt("1080p 首尾帧")
            .modelVersion(DreaminaVideoModelVersion.SEEDANCE_2_5)
            .videoResolution(DreaminaVideoResolutionType.RESOLUTION_1080P)
            .build()
            .toCliArgs();
        List<String> multimodalArgs = DreaminaMultimodal2VideoRequest.builder()
            .audio(audio.toString())
            .modelVersion(DreaminaVideoModelVersion.SEEDANCE_2_5)
            .videoResolution(DreaminaVideoResolutionType.RESOLUTION_1080P)
            .build()
            .toCliArgs();

        assertTrue(textArgs.contains("--video_resolution=1080p"));
        assertTrue(imageArgs.contains("--video_resolution=1080p"));
        assertTrue(framesArgs.contains("--video_resolution=1080p"));
        assertTrue(multimodalArgs.contains("--video_resolution=1080p"));
        assertFalse(multimodalArgs.stream().anyMatch(argument -> argument.startsWith("--image=")));
    }

    private Path createTempFile(String fileName) throws IOException {
        Path file = tempDir.resolve(fileName);
        Files.write(file, "contract".getBytes(StandardCharsets.UTF_8));
        return file;
    }
}
