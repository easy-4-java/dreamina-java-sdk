package io.github.easy4j.dreamina.cli.canvas;

import io.github.easy4j.dreamina.DreaminaCanvasCliProperties;
import io.github.easy4j.dreamina.cli.DreaminaCanvasCliExecutor;
import io.github.easy4j.dreamina.cli.model.DreaminaVersion;
import io.github.easy4j.dreamina.exception.DreaminaCanvasCliException;
import io.github.easy4j.dreamina.exception.DreaminaCliExecutableFailureException;
import io.github.easy4j.dreamina.exception.DreaminaCliTimeoutException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 真实子进程中的双流、工作目录、后代持有管道等边界。
 */
class DreaminaCanvasProcessBoundaryTest {
    @TempDir
    Path dir;

    private DreaminaCanvasCliExecutor fake(String commands, long timeout, int maxOutput, String workdir) throws Exception {
        Path executable = dir.resolve("dreamina canvas mock " + System.nanoTime());
        Files.write(executable, ("#!/bin/sh\n" + commands).getBytes(StandardCharsets.UTF_8));
        assertTrue(executable.toFile().setExecutable(true));
        return new DreaminaCanvasCliExecutor(DreaminaCanvasCliProperties.builder().executable(executable.toString())
                .workingDirectory(workdir).timeout(Duration.ofMillis(timeout)).maxOutputBytes(maxOutput).build());
    }

    @Test
    void stderrOutputIsBoundedIndependently() throws Exception {
        DreaminaCanvasCliExecutor cli = fake("exec 1>&2\nwhile :; do printf '0123456789'; done\n", 3000, 1024, null);
        DreaminaCanvasCliException error = assertThrows(DreaminaCanvasCliException.class, cli::version);
        assertEquals(DreaminaCanvasCliException.Reason.OUTPUT_LIMIT, error.getReason());
        assertTrue(error.getStderr().length() <= 1024);
    }

    @Test
    void inheritedPipeCannotHangAfterParentExits() throws Exception {
        DreaminaCanvasCliExecutor cli = fake("printf '%s\\n' '{\"schemaVersion\":\"1\",\"ok\":true,\"data\":{\"version\":\"1.0.1\"}}'\nsleep 0.5 &\nexit 0\n", 120, 4096, null);
        long start = System.nanoTime();
        assertThrows(DreaminaCliTimeoutException.class, cli::version);
        assertTrue(Duration.ofNanos(System.nanoTime() - start).toMillis() < 1000);
    }

    @Test
    void workingDirectoryIsUsedAndInvalidDirectoryIsClassified() throws Exception {
        Path work = dir.resolve("working");
        Files.createDirectory(work);
        DreaminaCanvasCliExecutor cli = fake("printf '%s\\n' '{\"schemaVersion\":\"1\",\"ok\":true,\"data\":{\"version\":\"1.0.1\"}}'\npwd > cwd.txt\n", 5000, 4096, work.toString());
        DreaminaVersion result = cli.version().getData();
        assertEquals("1.0.1", result.getVersion());
        assertEquals(work.toRealPath(), java.nio.file.Paths.get(new String(Files.readAllBytes(work.resolve("cwd.txt")), StandardCharsets.UTF_8).trim()).toRealPath());
        DreaminaCanvasCliExecutor invalid = fake("exit 0\n", 5000, 4096, dir.resolve("missing").toString());
        assertThrows(DreaminaCliExecutableFailureException.class, invalid::version);
    }

    private DreaminaCanvasCliExecutor javaPipeSpawner(String mode, long timeout, int limit) throws Exception {
        String javaBin = Paths.get(System.getProperty("java.home"), "bin", "java").toString();
        String testClasses = Paths.get("target", "test-classes").toAbsolutePath().toString();
        String script = "exec '" + javaBin + "' -cp '" + testClasses + "' "
                + DreaminaCanvasPipeChildMain.class.getName() + " " + mode + "\n";
        return fake(script, timeout, limit, null);
    }

    @Test
    void detachedDescendantHoldingPipeHitsReaderDeadline() throws Exception {
        DreaminaCanvasCliExecutor cli = javaPipeSpawner("hold", 900, 4096);
        DreaminaCliTimeoutException timeout = assertThrows(DreaminaCliTimeoutException.class, cli::version);
        assertEquals(Integer.valueOf(0), timeout.getPartialResult().getExitCode());
    }

    @Test
    void detachedDescendantCannotOverrunOutputAfterParentExits() throws Exception {
        DreaminaCanvasCliExecutor cli = javaPipeSpawner("overflow", 3000, 1024);
        DreaminaCanvasCliException error = assertThrows(DreaminaCanvasCliException.class, cli::version);
        assertEquals(DreaminaCanvasCliException.Reason.OUTPUT_LIMIT, error.getReason());
        assertTrue(error.getStdout().length() <= 1024);
    }
}
