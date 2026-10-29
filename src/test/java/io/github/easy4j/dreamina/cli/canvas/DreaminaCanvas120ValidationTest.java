package io.github.easy4j.dreamina.cli.canvas;

import io.github.easy4j.dreamina.cli.opts.DreaminaCanvasSpaceUseRequest;
import io.github.easy4j.dreamina.cli.opts.DreaminaCanvasNodeInterpolateVideoRequest;
import io.github.easy4j.dreamina.cli.opts.DreaminaCanvasNodeCutoutImageRequest;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** v1.2.0 空间选择与媒体后处理边界，不执行远端写操作。 */
class DreaminaCanvas120ValidationTest {
    @Test
    void teamSpaceRequiresCanonicalTeamId() {
        assertThrows(IllegalArgumentException.class, () -> DreaminaCanvasSpaceUseRequest.builder().kind("team").build().toArguments());
        assertThrows(IllegalArgumentException.class, () -> DreaminaCanvasSpaceUseRequest.builder().kind("personal").teamId("123").build().toArguments());
        assertThrows(IllegalArgumentException.class, () -> DreaminaCanvasSpaceUseRequest.builder().kind("team").teamId("01").build().toArguments());
        assertTrue(DreaminaCanvasSpaceUseRequest.builder().kind("team").teamId("123").build().toArguments().contains("123"));
    }
    @Test
    void postProcessingRejectsInvalidFrameRateAndBatchIdentity() {
        assertThrows(IllegalArgumentException.class, () -> DreaminaCanvasNodeInterpolateVideoRequest.builder().addNodeId("node_0000000001").fps(25L).build().toArguments());
        assertThrows(IllegalArgumentException.class, () -> DreaminaCanvasNodeCutoutImageRequest.builder().addNodeId("node_0000000001").addNodeId("node_0000000001").build().toArguments());
        assertThrows(IllegalArgumentException.class, () -> DreaminaCanvasNodeCutoutImageRequest.builder().addNodeId("node_0000000001").addSubmitId("12345678-1234-1234-1234-123456789012").addSubmitId("22345678-1234-1234-1234-123456789012").build().toArguments());
    }
}
