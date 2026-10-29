package io.github.easy4j.dreamina.cli.canvas;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.easy4j.dreamina.cli.DreaminaCanvasResponse;
import io.github.easy4j.dreamina.cli.DreaminaCliResult;
import io.github.easy4j.dreamina.cli.model.DreaminaVersion;
import io.github.easy4j.dreamina.cli.parser.DreaminaCanvasResponseParser;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** 受理后本地存储失败必须保留恢复位置及任务身份，不被当作可重新提交。 */
class DreaminaCanvasRecoveryDiagnosticsTest {
    @Test void localStorageDiagnosticsRemainTypedAndRaw() {
        String json = "{\"schemaVersion\":\"1\",\"ok\":false,\"error\":{\"code\":\"cli.local_storage\",\"reason\":\"write_failed\",\"requiredAction\":\"resume\",\"localStorage\":{\"store\":\"operations\",\"stage\":\"write\",\"reason\":\"permission_denied\",\"directory\":\"/test-only\",\"submissionPhase\":\"after_submit\"}},\"partialData\":{\"projectId\":\"project\",\"submitId\":\"original\",\"operationRef\":\"original\"}}";
        DreaminaCanvasResponse<DreaminaVersion> response = new DreaminaCanvasResponseParser().parse("node run",
                DreaminaCliResult.builder().stdout("").stderr(json).exitCode(20).success(false).build(), DreaminaVersion.class);
        assertFalse(response.isSuccess());
        JsonNode error = new ObjectMapper().valueToTree(response.getError());
        assertEquals("write_failed", error.path("reason").asText());
        assertEquals("after_submit", error.path("localStorage").path("submissionPhase").asText());
        assertEquals("original", response.getPartialData().getSubmitId());
        assertEquals("resume", response.getError().getRequiredAction());
        assertEquals(json, response.getStderr());
    }

    @org.junit.jupiter.api.io.TempDir java.nio.file.Path dir;
    @Test void acceptedOperationIsNotAutomaticallyResubmitted() throws Exception {
        java.nio.file.Path count = dir.resolve("calls");
        java.nio.file.Path bin = dir.resolve("canvas");
        String json = "{\"schemaVersion\":\"1\",\"ok\":false,\"error\":{\"code\":\"cli.operation_incomplete\",\"requiredAction\":\"resume\"},\"partialData\":{\"submitId\":\"original\"}}";
        java.nio.file.Files.write(bin, ("#!/bin/sh\nprintf x >> '" + count + "'\nprintf '%s' '" + json + "'\nexit 20\n").getBytes(java.nio.charset.StandardCharsets.UTF_8));
        assertTrue(bin.toFile().setExecutable(true));
        io.github.easy4j.dreamina.cli.DreaminaCanvasCliExecutor cli = new io.github.easy4j.dreamina.cli.DreaminaCanvasCliExecutor(
                io.github.easy4j.dreamina.DreaminaCanvasCliProperties.builder().executable(bin.toString()).build());
        io.github.easy4j.dreamina.cli.DreaminaCanvasResponse<?> response = cli.nodeRun(
                io.github.easy4j.dreamina.cli.opts.DreaminaCanvasNodeRunRequest.builder().addNodeId("node_0000000001").build());
        assertFalse(response.isSuccess());
        assertEquals("original", response.getPartialData().getSubmitId());
        assertEquals("resume", response.getError().getRequiredAction());
        assertEquals("x", new String(java.nio.file.Files.readAllBytes(count), java.nio.charset.StandardCharsets.UTF_8));
    }
    @Test void invalidStaticInputNeverStartsProcess() throws Exception {
        java.nio.file.Path marker = dir.resolve("started");
        java.nio.file.Path bin = dir.resolve("canvas-invalid");
        java.nio.file.Files.write(bin, ("#!/bin/sh\ntouch '" + marker + "'\n").getBytes(java.nio.charset.StandardCharsets.UTF_8));
        assertTrue(bin.toFile().setExecutable(true));
        io.github.easy4j.dreamina.cli.DreaminaCanvasCliExecutor cli = new io.github.easy4j.dreamina.cli.DreaminaCanvasCliExecutor(
                io.github.easy4j.dreamina.DreaminaCanvasCliProperties.builder().executable(bin.toString()).build());
        assertThrows(IllegalArgumentException.class, () -> cli.nodeCreateImage(
                io.github.easy4j.dreamina.cli.opts.DreaminaCanvasNodeCreateImageRequest.builder().title("test").x(java.math.BigDecimal.ONE).build()));
        assertFalse(java.nio.file.Files.exists(marker));
    }

    @Test void serviceErrorIdentifiersRemainTypedWithoutCoercion() {
        String json = "{\"schemaVersion\":\"1\",\"ok\":false,\"error\":{\"code\":\"service.20009\",\"serviceCode\":20009,\"requestId\":\"request-test\",\"traceId\":\"trace-test\"}}";
        DreaminaCanvasResponse<DreaminaVersion> response = new DreaminaCanvasResponseParser().parse("canvas use",
                DreaminaCliResult.builder().stdout("").stderr(json).exitCode(2).success(false).build(), DreaminaVersion.class);
        JsonNode error = new ObjectMapper().valueToTree(response.getError());
        assertEquals(20009, error.path("serviceCode").asLong());
        assertEquals("request-test", error.path("requestId").asText());
        assertEquals("trace-test", error.path("traceId").asText());
    }
}
