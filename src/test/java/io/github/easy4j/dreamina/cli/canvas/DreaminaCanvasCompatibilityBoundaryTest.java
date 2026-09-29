package io.github.easy4j.dreamina.cli.canvas;

import io.github.easy4j.dreamina.DreaminaCanvasCliProperties;
import io.github.easy4j.dreamina.cli.DreaminaCanvasCliExecutor;
import io.github.easy4j.dreamina.cli.opts.*;
import io.github.easy4j.dreamina.exception.DreaminaCanvasCliException;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 验证旧枚举和配置在新请求中被正确复用。
 */
class DreaminaCanvasCompatibilityBoundaryTest {
    @Test
    void existingImageEnumsMapToCanvasWireValues() {
        assertTrue(DreaminaCanvasNodeCreateImageRequest.builder().ratio(DreaminaRatio.RATIO_1_1).build().toCliArgs().contains("--ratio=1:1"));
        assertTrue(DreaminaCanvasNodeCreateImageRequest.builder().resolution(DreaminaImageResolutionType.RESOLUTION_1K).build().toCliArgs().contains("--resolution=1K"));
        assertTrue(DreaminaCanvasNodeEditImageRequest.builder().nodeId("node_a").ratio(DreaminaRatio.RATIO_1_1).build().toCliArgs().contains("--ratio=1:1"));
        assertTrue(DreaminaCanvasNodeEditImageRequest.builder().nodeId("node_a").resolution(DreaminaImageResolutionType.RESOLUTION_1K).build().toCliArgs().contains("--resolution=1K"));
        assertTrue(DreaminaCanvasNodeUpscaleImageRequest.builder().nodeId(java.util.Collections.singletonList("node_a")).resolution(DreaminaImageResolutionType.RESOLUTION_2K).build().toCliArgs().contains("--resolution=2K"));
        assertTrue(DreaminaCanvasNodeCreateImageRequest.builder().ratio((DreaminaRatio) null).resolution((DreaminaImageResolutionType) null).build().toCliArgs().isEmpty());
        assertEquals(java.util.Collections.singletonList("--node-id=node_a"), DreaminaCanvasNodeEditImageRequest.builder().nodeId("node_a").ratio((DreaminaRatio) null).resolution((DreaminaImageResolutionType) null).build().toCliArgs());
        assertEquals(java.util.Collections.singletonList("--node-id=node_a"), DreaminaCanvasNodeUpscaleImageRequest.builder().nodeId(java.util.Collections.singletonList("node_a")).resolution((DreaminaImageResolutionType) null).build().toCliArgs());
        assertTrue(DreaminaCanvasNodeCreateImageRequest.builder().ratio("16:9").build().toCliArgs().contains("--ratio=16:9"));
        assertTrue(DreaminaCanvasNodeCreateImageRequest.builder().resolution("4K").build().toCliArgs().contains("--resolution=4K"));
        assertTrue(DreaminaCanvasNodeEditImageRequest.builder().nodeId("node_a").resolution("4K").build().toCliArgs().contains("--resolution=4K"));
        assertTrue(DreaminaCanvasNodeEditImageRequest.builder().nodeId("node_a").ratio("16:9").build().toCliArgs().contains("--ratio=16:9"));
        assertTrue(DreaminaCanvasNodeUpscaleImageRequest.builder().nodeId(java.util.Collections.singletonList("node_a")).resolution("8K").build().toCliArgs().contains("--resolution=8K"));
    }

    @Test
    void existingVideoEnumsMapToCanvasWireValues() {
        assertTrue(DreaminaCanvasNodeCreateVideoRequest.builder().ratio(DreaminaRatio.RATIO_1_1).build().toCliArgs().contains("--ratio=1:1"));
        assertTrue(DreaminaCanvasNodeEditVideoRequest.builder().nodeId("node_a").ratio(DreaminaRatio.RATIO_1_1).build().toCliArgs().contains("--ratio=1:1"));
        assertTrue(DreaminaCanvasNodeCreateVideoRequest.builder().resolution(DreaminaVideoResolutionType.RESOLUTION_1080P).build().toCliArgs().contains("--resolution=1080p"));
        assertTrue(DreaminaCanvasNodeEditVideoRequest.builder().nodeId("node_a").resolution(DreaminaVideoResolutionType.RESOLUTION_1080P).build().toCliArgs().contains("--resolution=1080p"));
        assertTrue(DreaminaCanvasNodeCreateVideoRequest.builder().ratio((DreaminaRatio) null).resolution((DreaminaVideoResolutionType) null).build().toCliArgs().isEmpty());
        assertEquals(java.util.Collections.singletonList("--node-id=node_a"), DreaminaCanvasNodeEditVideoRequest.builder().nodeId("node_a").ratio((DreaminaRatio) null).resolution((DreaminaVideoResolutionType) null).build().toCliArgs());
        assertTrue(DreaminaCanvasNodeCreateVideoRequest.builder().resolution("4K").build().toCliArgs().contains("--resolution=4K"));
        assertTrue(DreaminaCanvasNodeCreateVideoRequest.builder().ratio("16:9").build().toCliArgs().contains("--ratio=16:9"));
        assertTrue(DreaminaCanvasNodeEditVideoRequest.builder().nodeId("node_a").ratio("16:9").build().toCliArgs().contains("--ratio=16:9"));
        assertTrue(DreaminaCanvasNodeEditVideoRequest.builder().nodeId("node_a").resolution("4K").build().toCliArgs().contains("--resolution=4K"));
    }

    @Test
    void configurationRejectsImpossibleBudgetsBeforeProcessStart() {
        assertThrows(NullPointerException.class, () -> DreaminaCanvasCliProperties.copyOf(null));
        assertThrows(IllegalArgumentException.class, () -> DreaminaCanvasCliProperties.builder().timeout(Duration.ofDays(8)).build());
        assertThrows(IllegalArgumentException.class, () -> DreaminaCanvasCliProperties.builder().timeout(Duration.ofSeconds(-1)).build());
        assertThrows(IllegalArgumentException.class, () -> DreaminaCanvasCliProperties.builder().maxConcurrentExecutions(0).build());
        assertThrows(IllegalArgumentException.class, () -> DreaminaCanvasCliProperties.builder().maxOutputBytes(0).build());
        assertThrows(IllegalArgumentException.class, () -> DreaminaCanvasCliProperties.builder().profile("  ").build());
        assertThrows(IllegalArgumentException.class, () -> DreaminaCanvasCliProperties.builder().executable(" ").build());
        DreaminaCanvasCliProperties invalid = new DreaminaCanvasCliProperties();
        invalid.setCommandTimeoutMillis(Duration.ofDays(8).toMillis());
        assertThrows(IllegalArgumentException.class, () -> DreaminaCanvasCliProperties.copyOf(invalid));
        DreaminaCanvasCliProperties defaults = new DreaminaCanvasCliExecutor().getProperties();
        assertEquals("dreamina-canvas", defaults.getExecutable());
        assertEquals("default", defaults.getProfile());
    }

    @Test
    void exceptionMessageDoesNotIncludeOutputButExplicitGettersDo() {
        DreaminaCanvasCliException failure = new DreaminaCanvasCliException(DreaminaCanvasCliException.Reason.IO_FAILED, "node run", 7, "private-out", "private-err");
        assertEquals(Integer.valueOf(7), failure.getExitCode());
        assertEquals("private-out", failure.getStdout());
        assertEquals("private-err", failure.getStderr());
        assertFalse(failure.getMessage().contains("private"));
    }
}
