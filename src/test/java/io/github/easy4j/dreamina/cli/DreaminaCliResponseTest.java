package io.github.easy4j.dreamina.cli;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import io.github.easy4j.dreamina.cli.parser.DreaminaParsedFields;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DreaminaCliResponseTest {

    @Test
    void shouldCreateResponseFromRawResult() {
        DreaminaCliResult raw = DreaminaCliResult.builder()
            .stdout("hello").stderr("err").exitCode(0).success(true).build();
        DreaminaCliResponse<String> resp = DreaminaCliResponse.of(raw, "body");
        assertEquals("hello", resp.getStdout());
        assertEquals("err", resp.getStderr());
        assertEquals(0, resp.getExitCode());
        assertTrue(resp.isSuccess());
        assertEquals("body", resp.getBody());
        assertTrue(resp.hasBody());
        assertNull(resp.getJson());
    }

    @Test
    void shouldCreateResponseWithJson() {
        DreaminaCliResult raw = DreaminaCliResult.builder()
            .stdout("{}").exitCode(0).success(true).build();
        ObjectMapper om = new JsonMapper();
        JsonNode node = om.createObjectNode();
        DreaminaCliResponse<String> resp = DreaminaCliResponse.of(raw, "body", node);
        assertNotNull(resp.getJson());
    }

    @Test
    void shouldCombineTextWithBothStreams() {
        DreaminaCliResult raw = DreaminaCliResult.builder()
            .stdout("out").stderr("err").build();
        DreaminaCliResponse<Void> resp = DreaminaCliResponse.of(raw, null);
        String combined = resp.getCombinedText();
        assertTrue(combined.contains("out"));
        assertTrue(combined.contains("err"));
    }

    @Test
    void shouldCombineTextWithOnlyStdout() {
        DreaminaCliResult raw = DreaminaCliResult.builder().stdout("out").stderr("").build();
        DreaminaCliResponse<Void> resp = DreaminaCliResponse.of(raw, null);
        assertEquals("out", resp.getCombinedText());
    }

    @Test
    void shouldCombineTextWithOnlyStderr() {
        DreaminaCliResult raw = DreaminaCliResult.builder().stdout("").stderr("err").build();
        DreaminaCliResponse<Void> resp = DreaminaCliResponse.of(raw, null);
        assertEquals("err", resp.getCombinedText());
    }

    @Test
    void shouldReturnEmptyStringWhenBothStreamsEmpty() {
        DreaminaCliResult raw = DreaminaCliResult.builder().stdout("").stderr("").build();
        DreaminaCliResponse<Void> resp = DreaminaCliResponse.of(raw, null);
        assertEquals("", resp.getCombinedText());
    }

    @Test
    void shouldReturnFalseForHasBodyWhenNull() {
        DreaminaCliResult raw = DreaminaCliResult.builder().stdout("").build();
        DreaminaCliResponse<Void> resp = DreaminaCliResponse.of(raw, null);
        assertFalse(resp.hasBody());
    }

    @Test
    void shouldPreserveParsedFields() {
        DreaminaParsedFields parsed = DreaminaParsedFields.builder().submitId("s1").credit(100L).build();
        DreaminaCliResult raw = DreaminaCliResult.builder()
            .stdout("").exitCode(0).success(true).parsed(parsed).build();
        DreaminaCliResponse<String> resp = DreaminaCliResponse.of(raw, "body");
        assertNotNull(resp.getParsed());
        assertEquals("s1", resp.getParsed().getSubmitId());
        assertEquals(100L, resp.getParsed().getCredit());
    }
}
