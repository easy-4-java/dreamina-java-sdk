package io.github.hiwepy.dreamina.cli;

import io.github.hiwepy.dreamina.cli.support.MockDreaminaCli;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * CLI v1.4.14 对原始参数快捷入口的兼容契约。
 *
 * @author wandl
 * @since 2.0.0
 */
class DreaminaCliExecutorV1414ContractMockTest {

    private MockDreaminaCli mockCli;
    private DreaminaCliExecutor executor;
    private Path image;

    @BeforeEach
    void setUp() throws Exception {
        mockCli = MockDreaminaCli.install();
        mockCli.resetLog();
        executor = mockCli.newExecutor();
        image = mockCli.newTinyPng("v1414.png");
    }

    @Test
    void rawImageGenerationOverloads_shouldSupplyRequiredResolution() throws Exception {
        assertTrue(executor.text2Image("prompt").isSuccess());
        assertTrue(mockCli.lastInvocation().contains("--resolution_type=2k"));

        assertTrue(executor.image2Image(image.toString(), "prompt", Collections.<String>emptyList()).isSuccess());
        assertTrue(mockCli.lastInvocation().contains("--resolution_type=2k"));

        assertTrue(executor.imageUpscale(Arrays.asList("--image=" + image)).isSuccess());
        assertTrue(mockCli.lastInvocation().contains("--resolution_type=2k"));
    }

    @Test
    void rawVideoGenerationOverloads_shouldSupplyRequiredResolution() throws Exception {
        assertTrue(executor.text2video("prompt").isSuccess());
        assertTrue(mockCli.lastInvocation().contains("--video_resolution=720p"));

        assertTrue(executor.frames2video(Arrays.asList(
            "--first=" + image, "--last=" + image, "--prompt=prompt")).isSuccess());
        assertTrue(mockCli.lastInvocation().contains("--video_resolution=720p"));

        assertTrue(executor.multiframe2video(Arrays.asList(
            "--images=" + image + "," + image, "--prompt=prompt")).isSuccess());
        assertTrue(mockCli.lastInvocation().contains("--video_resolution=720p"));

        assertTrue(executor.multimodal2video(Arrays.asList("--image=" + image)).isSuccess());
        assertTrue(mockCli.lastInvocation().contains("--video_resolution=720p"));
    }

    @Test
    void image2VideoRawOverload_shouldRequirePromptAndSupplyResolution() throws Exception {
        assertThrows(IllegalArgumentException.class,
            () -> executor.image2video(image.toString(), Collections.<String>emptyList()));

        assertTrue(executor.image2video(
            image.toString(),
            Arrays.asList("--prompt=push in", "--poll=0")).isSuccess());
        assertTrue(mockCli.lastInvocation().contains("--video_resolution=720p"));
    }

    @Test
    void explicitResolution_shouldNotBeOverriddenOrDuplicated() throws Exception {
        assertTrue(executor.text2video(
            "prompt",
            Arrays.asList("--video_resolution=1080p", "--poll=0")).isSuccess());
        String invocation = mockCli.lastInvocation();
        assertTrue(invocation.contains("--video_resolution=1080p"));
        assertTrue(!invocation.contains("--video_resolution=720p"));
    }
}
