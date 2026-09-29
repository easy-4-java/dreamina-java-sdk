package io.github.easy4j.dreamina.cli.canvas;

import io.github.easy4j.dreamina.cli.DreaminaCanvasResponse;
import io.github.easy4j.dreamina.cli.DreaminaCliResult;
import io.github.easy4j.dreamina.cli.model.DreaminaVersion;
import io.github.easy4j.dreamina.cli.parser.DreaminaCanvasResponseParser;
import io.github.easy4j.dreamina.exception.DreaminaCanvasCliException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 进程退出与 CLI 协议相互独立时仍保持可恢复语义。
 */
class DreaminaCanvasParserBoundaryTest {
    private final DreaminaCanvasResponseParser parser = new DreaminaCanvasResponseParser();

    @Test
    void stderrEnvelopeIsUsedOnlyAfterFailedProcessWithoutStdout() {
        String error = "{\"schemaVersion\":\"1\",\"ok\":false,\"error\":{\"code\":\"cli.test\",\"class\":\"temporary\",\"requiredAction\":\"resume\",\"retryable\":true,\"validation\":{\"fieldPath\":\"node-id\"},\"details\":{\"reason\":\"session-expired\"}},\"partialData\":{\"submitId\":\"original\"},\"meta\":{\"requestId\":\"req\"}}";
        DreaminaCanvasResponse<DreaminaVersion> response = parser.parse("version", raw("", error, 20), DreaminaVersion.class);
        assertFalse(response.isSuccess());
        assertEquals("original", response.getPartialData().getSubmitId());
        assertEquals("resume", response.getError().getRequiredAction());
        assertEquals("node-id", response.getError().getValidation().getFieldPath());
        assertEquals("temporary", response.getError().getErrorClass());
        assertEquals("session-expired", response.getError().getDetails().getReason());
        assertEquals("req", response.getMeta().getRequestId());
        assertThrows(DreaminaCanvasCliException.class, () -> parser.parse("version", raw("noise", error, 20), DreaminaVersion.class));
    }

    @Test
    void rejectsMalformedTypesAndProtocolVersions() {
        String[] invalid = {"", "null", "[]", "{}", "{\"schemaVersion\":\"2\",\"ok\":true}",
                "{\"schemaVersion\":\"1\"}", "{\"schemaVersion\":\"1\",\"ok\":\"true\"}",
                "{\"schemaVersion\":\"1\",\"ok\":true,\"data\":{\"version\":7}}"};
        for (String text : invalid) {
            DreaminaCanvasCliException error = assertThrows(DreaminaCanvasCliException.class,
                    () -> parser.parse("version", raw(text, "", 0), DreaminaVersion.class), text);
            assertEquals(DreaminaCanvasCliException.Reason.INVALID_RESPONSE, error.getReason());
        }
    }

    @Test
    void successfulDataStillRequiresZeroExitAndOk() {
        String json = "{\"schemaVersion\":\"1\",\"ok\":true,\"data\":{\"version\":\"1.0.1\"}}";
        assertTrue(parser.parse("version", raw(json, "", 0), DreaminaVersion.class).isSuccess());
        assertFalse(parser.parse("version", raw(json, "", 2), DreaminaVersion.class).isSuccess());
        assertFalse(parser.parse("version", raw(json.replace("true", "false"), "", 0), DreaminaVersion.class).isSuccess());
        DreaminaCanvasResponse<DreaminaVersion> unbound = new DreaminaCanvasResponse<>();
        unbound.setOk(true);
        assertFalse(unbound.isSuccess());
        assertThrows(NullPointerException.class, () -> parser.parse("version", null, DreaminaVersion.class));
        assertThrows(NullPointerException.class, () -> parser.parse("version", raw(json, "", 0), null));
    }

    private DreaminaCliResult raw(String stdout, String stderr, int code) {
        return DreaminaCliResult.builder().stdout(stdout).stderr(stderr).exitCode(code).success(code == 0).build();
    }
}
