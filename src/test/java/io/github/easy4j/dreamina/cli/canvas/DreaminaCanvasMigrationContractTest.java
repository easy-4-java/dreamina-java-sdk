package io.github.easy4j.dreamina.cli.canvas;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DreaminaCanvasMigrationContractTest {
    @Test
    void canvasEntryPointMustExist() {
        assertDoesNotThrow(() -> Class.forName("io.github.easy4j.dreamina.cli.DreaminaCanvasCliExecutor"));
    }

    @Test
    void legacyEntryPointMustDeclareMigration() {
        assertTrue(io.github.easy4j.dreamina.cli.DreaminaCliExecutor.class.isAnnotationPresent(Deprecated.class));
        org.junit.jupiter.api.Assertions.assertFalse(io.github.easy4j.dreamina.DreaminaCliProperties.class.isAnnotationPresent(Deprecated.class));
    }

    @Test
    void supportingTypesMustNotBeBlanketDeprecated() {
        for (Class<?> type : new Class<?>[]{
                io.github.easy4j.dreamina.cli.DreaminaCliResponse.class,
                io.github.easy4j.dreamina.cli.opts.DreaminaRatio.class,
                io.github.easy4j.dreamina.cli.opts.DreaminaImageResolutionType.class,
                io.github.easy4j.dreamina.cli.parser.DreaminaCliStructuredPayloadMapper.class,
                io.github.easy4j.dreamina.cli.availability.DreaminaCliAvailabilityChecker.class,
                io.github.easy4j.dreamina.exception.DreaminaCliException.class}) {
            org.junit.jupiter.api.Assertions.assertFalse(type.isAnnotationPresent(Deprecated.class), type.getName());
        }
    }
}
