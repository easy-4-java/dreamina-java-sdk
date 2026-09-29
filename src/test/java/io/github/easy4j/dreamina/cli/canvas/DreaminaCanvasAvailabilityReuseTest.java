package io.github.easy4j.dreamina.cli.canvas;

import io.github.easy4j.dreamina.DreaminaCanvasCliProperties;
import io.github.easy4j.dreamina.cli.availability.DreaminaCliAvailabilityChecker;
import io.github.easy4j.dreamina.cli.availability.DreaminaCliAvailabilityReport;
import io.github.easy4j.dreamina.cli.availability.DreaminaCliAvailabilityStatus;
import io.github.easy4j.dreamina.exception.DreaminaCliStartupException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class DreaminaCanvasAvailabilityReuseTest {
    private final DreaminaCliAvailabilityChecker checker = new DreaminaCliAvailabilityChecker();
    @TempDir
    Path directory;

    private DreaminaCanvasCliProperties executable(String content) throws Exception {
        Path file = directory.resolve("canvas-" + System.nanoTime());
        Files.write(file, content.getBytes(StandardCharsets.UTF_8));
        assertTrue(file.toFile().setExecutable(true));
        return DreaminaCanvasCliProperties.builder().executable(file.toString()).build();
    }

    @Test
    void existingReportAndStartupExceptionWorkForCanvas() throws Exception {
        DreaminaCanvasCliProperties config = executable("#!/bin/sh\necho '{\"schemaVersion\":\"1\",\"ok\":true,\"data\":{\"version\":\"1.0.1\"}}'\n");
        DreaminaCliAvailabilityReport report = checker.checkCanvas(config);
        assertTrue(report.isAvailable());
        assertEquals(DreaminaCliAvailabilityStatus.AVAILABLE, report.getStatus());
        assertNotNull(report.getProbeResult());
        assertSame(report, new DreaminaCliStartupException("probe", report).getAvailabilityReport());
    }

    @Test
    void missingAndNonExecutablePathsHaveExistingStatuses() throws Exception {
        DreaminaCanvasCliProperties config = new DreaminaCanvasCliProperties();
        config.setExecutable(" ");
        assertEquals(DreaminaCliAvailabilityStatus.EXECUTABLE_NOT_CONFIGURED, checker.checkCanvas(config).getStatus());
        config.setExecutable(directory.resolve("missing").toString());
        assertEquals(DreaminaCliAvailabilityStatus.EXECUTABLE_NOT_FOUND, checker.checkCanvas(config).getStatus());
        Path plain = directory.resolve("plain");
        Files.write(plain, new byte[0]);
        config.setExecutable(plain.toString());
        assertEquals(DreaminaCliAvailabilityStatus.EXECUTABLE_NOT_EXECUTABLE, checker.checkCanvas(config).getStatus());
    }

    @Test
    void timeoutAndMalformedEnvelopeRetainDiagnosticSnapshots() throws Exception {
        DreaminaCanvasCliProperties slow = executable("#!/bin/sh\nexec sleep 10\n");
        slow.setStartupProbeTimeoutMillis(100);
        DreaminaCliAvailabilityReport report = checker.checkCanvas(slow);
        assertEquals(DreaminaCliAvailabilityStatus.TIMEOUT, report.getStatus());
        assertNotNull(report.getProbeResult());
        assertEquals(DreaminaCliAvailabilityStatus.FAILED, checker.checkCanvas(executable("#!/bin/sh\necho invalid\n")).getStatus());
    }

    @Test
    void unsuccessfulEnvelopeIsUnavailableEvenWithExitZero() throws Exception {
        DreaminaCliAvailabilityReport report = checker.checkCanvas(executable("#!/bin/sh\necho '{\"schemaVersion\":\"1\",\"ok\":false,\"error\":{\"code\":\"cli.test\"}}'\n"));
        assertFalse(report.isAvailable());
        assertEquals(DreaminaCliAvailabilityStatus.NON_ZERO_EXIT, report.getStatus());
    }
}
