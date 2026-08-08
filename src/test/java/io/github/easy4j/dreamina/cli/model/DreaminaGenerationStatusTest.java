package io.github.easy4j.dreamina.cli.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class DreaminaGenerationStatusTest {

    @ParameterizedTest
    @CsvSource({
        "querying, QUERYING",
        "success, SUCCESS",
        "fail, FAIL",
        "failed, FAIL",
        "QUERYING, QUERYING",
        "SUCCESS, SUCCESS",
        "FAIL, FAIL"
    })
    void shouldParseCliValue(String input, DreaminaGenerationStatus expected) {
        assertEquals(expected, DreaminaGenerationStatus.fromCliValue(input));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"unknown", "pending", "xyz"})
    void shouldReturnUnknownForUnrecognizedValues(String input) {
        assertEquals(DreaminaGenerationStatus.UNKNOWN, DreaminaGenerationStatus.fromCliValue(input));
    }

    @Test
    void shouldIdentifyTerminalStates() {
        assertTrue(DreaminaGenerationStatus.SUCCESS.isTerminal());
        assertTrue(DreaminaGenerationStatus.FAIL.isTerminal());
        assertFalse(DreaminaGenerationStatus.QUERYING.isTerminal());
        assertFalse(DreaminaGenerationStatus.UNKNOWN.isTerminal());
    }
}
