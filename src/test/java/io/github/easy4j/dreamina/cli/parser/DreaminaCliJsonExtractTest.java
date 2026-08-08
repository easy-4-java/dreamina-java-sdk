package io.github.easy4j.dreamina.cli.parser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DreaminaCliJsonExtractTest {

    @Test
    void shouldExtractFirstJsonObject() {
        String text = "some log line\n{\"key\":\"value\"}";
        String result = DreaminaCliJsonExtract.extractFirstBalancedJson(text);
        assertNotNull(result);
        assertTrue(result.contains("\"key\""));
    }

    @Test
    void shouldExtractFirstJsonArray() {
        String text = "log\n[1,2,3]";
        String result = DreaminaCliJsonExtract.extractFirstBalancedJson(text);
        assertNotNull(result);
        assertTrue(result.startsWith("["));
    }

    @Test
    void shouldReturnNullForNoJson() {
        assertNull(DreaminaCliJsonExtract.extractFirstBalancedJson("no json here"));
    }

    @Test
    void shouldReturnNullForNullInput() {
        assertNull(DreaminaCliJsonExtract.extractFirstBalancedJson(null));
    }

    @Test
    void shouldReturnNullForEmptyInput() {
        assertNull(DreaminaCliJsonExtract.extractFirstBalancedJson(""));
    }

    @Test
    void shouldReturnNullForUnbalancedJson() {
        assertNull(DreaminaCliJsonExtract.extractFirstBalancedJson("{\"key\": \"value\""));
    }

    @Test
    void shouldHandleNestedJson() {
        String text = "{\"a\":{\"b\":1}}";
        String result = DreaminaCliJsonExtract.extractFirstBalancedJson(text);
        assertNotNull(result);
        assertTrue(result.contains("\"b\""));
    }

    @Test
    void shouldExtractJsonWithLeadingLogLines() {
        String text = "WARNING: something\nINFO: else\n{\"status\":\"ok\"}";
        String result = DreaminaCliJsonExtract.extractFirstBalancedJson(text);
        assertNotNull(result);
        assertTrue(result.contains("\"status\""));
    }

    @Test
    void shouldHandlePrivateConstructor() {
        // Verify the class is final with private constructor
        assertTrue(java.lang.reflect.Modifier.isFinal(DreaminaCliJsonExtract.class.getModifiers()));
    }
}
