package io.github.easy4j.dreamina.cli.canvas;

import io.github.easy4j.dreamina.DreaminaCanvasCliProperties;
import io.github.easy4j.dreamina.DreaminaCliProperties;
import io.github.easy4j.dreamina.cli.DreaminaCanvasCliExecutor;
import io.github.easy4j.dreamina.cli.DreaminaCliResult;
import io.github.easy4j.dreamina.exception.DreaminaCanvasCliException;
import io.github.easy4j.dreamina.exception.DreaminaCliException;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class DreaminaCanvasReuseContractTest {
    @Test
    void canvasPropertiesReuseGenericFieldsAndSnapshotExternalMutation() {
        DreaminaCanvasCliProperties properties = new DreaminaCanvasCliProperties();
        assertInstanceOf(DreaminaCliProperties.class, properties);
        properties.setExecutable("canvas-wrapper");
        properties.setCommandTimeoutMillis(1234);
        properties.setWorkingDirectory("/tmp");
        properties.setMaxConcurrentExecutions(2);
        DreaminaCanvasCliExecutor executor = new DreaminaCanvasCliExecutor(properties);
        properties.setCommandTimeoutMillis(1);
        properties.setProfile("changed");
        assertEquals(Duration.ofMillis(1234), executor.getProperties().getTimeout());
        assertEquals("default", executor.getProperties().getProfile());
        assertEquals("/tmp", executor.getProperties().getWorkingDirectory());
        executor.getProperties().setExecutable("also changed");
        assertEquals("canvas-wrapper", executor.getProperties().getExecutable());
        assertEquals("dreamina", new DreaminaCliProperties().getExecutable());
    }

    @Test
    void canvasErrorsUseOriginalExceptionHierarchyAndResultSnapshot() {
        DreaminaCanvasCliException error = new DreaminaCanvasCliException(DreaminaCanvasCliException.Reason.TIMEOUT, "node run", null, "out", "err");
        assertInstanceOf(DreaminaCliException.class, error);
        DreaminaCliResult result = error.getPartialResult();
        assertEquals("out", result.getStdout());
        assertEquals("err", result.getStderr());
        assertNull(result.getExitCode());
        assertFalse(result.isSuccess());
    }

    @Test
    void textHelpersReturnOriginalResultType() throws Exception {
        assertEquals(DreaminaCliResult.class, DreaminaCanvasCliExecutor.class.getMethod("help").getReturnType());
        assertEquals(DreaminaCliResult.class, DreaminaCanvasCliExecutor.class.getMethod("completion", String.class).getReturnType());
    }

    @Test
    void canvasEnvelopeComposesOriginalResultInsteadOfDuplicatingTransportFields() throws Exception {
        DreaminaCliResult raw = DreaminaCliResult.builder().exitCode(0).stdout("{\"schemaVersion\":\"1\",\"ok\":true,\"data\":{}}").stderr("").success(true).build();
        io.github.easy4j.dreamina.cli.DreaminaCanvasResponse<io.github.easy4j.dreamina.cli.model.DreaminaVersion> response =
                new io.github.easy4j.dreamina.cli.parser.DreaminaCanvasResponseParser().parse("version", raw, io.github.easy4j.dreamina.cli.model.DreaminaVersion.class);
        assertSame(raw, response.getRaw());
        assertTrue(response.isSuccess());
    }
}
