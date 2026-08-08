package io.github.easy4j.dreamina.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class DreaminaStringsTest {

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n", "   \t\n  "})
    void shouldReturnTrueForBlankStrings(String value) {
        assertTrue(DreaminaStrings.isBlank(value));
    }

    @ParameterizedTest
    @ValueSource(strings = {"a", " a ", "hello", "x"})
    void shouldReturnFalseForNonBlankStrings(String value) {
        assertFalse(DreaminaStrings.isBlank(value));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "\t", "\n"})
    void shouldReturnFalseForIsNotBlankWhenBlank(String value) {
        assertFalse(DreaminaStrings.isNotBlank(value));
    }

    @ParameterizedTest
    @ValueSource(strings = {"a", " a ", "hello", "x"})
    void shouldReturnTrueForIsNotBlankWhenNonBlank(String value) {
        assertTrue(DreaminaStrings.isNotBlank(value));
    }

    @Test
    void shouldHavePrivateConstructor() throws ReflectiveOperationException {
        var ctor = DreaminaStrings.class.getDeclaredConstructor();
        assertFalse(ctor.canAccess(null));
    }
}
