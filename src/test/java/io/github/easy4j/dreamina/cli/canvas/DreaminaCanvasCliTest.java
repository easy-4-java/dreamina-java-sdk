package io.github.easy4j.dreamina.cli.canvas;

import io.github.easy4j.dreamina.DreaminaCanvasCliProperties;
import io.github.easy4j.dreamina.cli.DreaminaCanvasCliExecutor;
import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;
import io.github.easy4j.dreamina.cli.DreaminaCanvasResponse;
import io.github.easy4j.dreamina.cli.opts.DreaminaCanvasContract;
import io.github.easy4j.dreamina.cli.opts.DreaminaCanvasRequest;
import io.github.easy4j.dreamina.exception.DreaminaCanvasCliException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class DreaminaCanvasCliTest {
    @TempDir
    Path dir;

    private DreaminaCanvasCliExecutor fake(String script, long millis) throws Exception {
        Path bin = dir.resolve("canvas " + System.nanoTime());
        Files.write(bin, ("#!/bin/sh\n" + script).getBytes(StandardCharsets.UTF_8));
        assertTrue(bin.toFile().setExecutable(true));
        return new DreaminaCanvasCliExecutor(DreaminaCanvasCliProperties.builder().executable(bin.toString())
                .timeout(Duration.ofMillis(millis)).build());
    }

    private String output(String json, int exit) {
        return "cat <<'ENVELOPE'\n" + json + "\nENVELOPE\nexit " + exit + "\n";
    }

    @Test
    void argvIsLiteralAndDoesNotImplicitlyRunOrApprove() throws Exception {
        Path args = dir.resolve("argv");
        DreaminaCanvasCliExecutor cli = fake("printf '%s\\000' \"$@\" > '" + args + "'\n" + output("{\"schemaVersion\":\"1\",\"ok\":true,\"data\":{}}", 0), 5000);
        String prompt = "中文 'quote' \"double\"\n$(touch SHOULD_NOT_EXIST); *";
        cli.execute(DreaminaCanvasFixtures.request(DreaminaCanvasCommand.NODE_CREATE_IMAGE, "mode=t2i", "prompt=" + prompt, "run=false", "dry-run=true"));
        String raw = new String(Files.readAllBytes(args), StandardCharsets.UTF_8);
        assertTrue(Arrays.asList(raw.split("\u0000")).contains("--prompt=" + prompt));
        assertTrue(raw.contains("--format=json\u0000"));
        assertTrue(raw.contains("--non-interactive\u0000"));
        assertTrue(raw.contains("--run=false\u0000"));
        assertFalse(raw.contains("--yes"));
        assertFalse(raw.contains("--credit-ceiling"));
    }

    @Test
    void repeatedValuesAndPositionalsAreUnambiguous() {
        DreaminaCanvasRequest req = DreaminaCanvasFixtures.request(DreaminaCanvasCommand.NODE_SHOW, "node-id=" + "node_a", "node-id=" + "node_b");
        assertEquals(Arrays.asList("node", "show", "--node-id=node_a", "--node-id=node_b"), req.toArguments());
        assertEquals(Arrays.asList("canvas", "create", "--", "--yes"), DreaminaCanvasFixtures.request(DreaminaCanvasCommand.CANVAS_CREATE, "@" + "--yes").toArguments());
    }

    @Test
    void rejectsInvalidLocalContracts() {
        assertThrows(IllegalArgumentException.class, () -> DreaminaCanvasFixtures.request(DreaminaCanvasCommand.MODEL_LIST));
        assertThrows(IllegalArgumentException.class, () -> DreaminaCanvasFixtures.request(DreaminaCanvasCommand.VERSION, "force=" + true));
        assertThrows(IllegalArgumentException.class, () -> DreaminaCanvasFixtures.request(DreaminaCanvasCommand.MODEL_LIST, "type=" + "image", "type=" + "video"));
        assertThrows(IllegalArgumentException.class, () -> DreaminaCanvasFixtures.request(DreaminaCanvasCommand.CANVAS_LS, "limit=" + 0));
        assertThrows(IllegalArgumentException.class, () -> DreaminaCanvasFixtures.request(DreaminaCanvasCommand.VERSION, "@" + "extra"));
        assertThrows(IllegalArgumentException.class, () -> DreaminaCanvasFixtures.request(DreaminaCanvasCommand.NODE_CREATE_IMAGE, "credit-ceiling=" + 1));
        assertThrows(IllegalArgumentException.class, () -> DreaminaCanvasFixtures.request(DreaminaCanvasCommand.NODE_RUN, "node-id=" + "node_a", "submit-id=" + "bad"));
        assertThrows(IllegalArgumentException.class, () -> DreaminaCanvasFixtures.request(DreaminaCanvasCommand.RESOURCE_UPLOAD, "file=" + "a", "source-url=" + "https://example.com"));
    }

    @Test
    void confirmationAndRecoverySurviveNonzeroExit() throws Exception {
        for (int exit : new int[]{10, 20, 13}) {
            DreaminaCanvasCliExecutor cli = fake(output("{\"schemaVersion\":\"1\",\"ok\":false,\"error\":{\"code\":\"cli.operation_incomplete\",\"requiredAction\":\"resume\",\"retryable\":false},\"partialData\":{\"submitId\":\"original\"},\"creditConfirmation\":{\"minimumCreditCeiling\":2},\"meta\":{\"requestId\":\"r\"},\"futureField\":42}", exit), 5000);
            DreaminaCanvasResponse<?> r = cli.version();
            assertFalse(r.isSuccess());
            assertEquals(exit, r.getExitCode());
            assertEquals("resume", r.getError().getRequiredAction());
            assertEquals("original", r.getPartialData().getSubmitId());
            assertEquals(2, r.getCreditConfirmation().getMinimumCreditCeiling().intValue());
            assertTrue(r.getStdout().contains("futureField"));
            assertEquals("r", r.getMeta().getRequestId());
        }
    }

    @Test
    void strictEnvelopeAndSafeExceptions() throws Exception {
        for (String body : new String[]{"not-json secret", "{}", "{\"schemaVersion\":\"1\",\"ok\":\"true\"}", "{\"schemaVersion\":\"1\",\"ok\":true} trailing"}) {
            DreaminaCanvasCliExecutor cli = fake(output(body, 0), 5000);
            DreaminaCanvasCliException e = assertThrows(DreaminaCanvasCliException.class, cli::version);
            assertEquals(DreaminaCanvasCliException.Reason.INVALID_RESPONSE, e.getReason());
            assertFalse(e.getMessage().contains("secret"));
        }
    }

    @Test
    void businessErrorsWrittenToStderrAreStillStructured() throws Exception {
        DreaminaCanvasCliExecutor cli = fake("exec 1>&2\n" + output("{\"schemaVersion\":\"1\",\"ok\":false,\"error\":{\"code\":\"cli.invalid_node_input\"}}", 2), 5000);
        DreaminaCanvasResponse<?> result = cli.version();
        assertEquals("cli.invalid_node_input", result.getError().getCode());
        assertEquals("", result.getStdout());
        assertFalse(result.getStderr().isEmpty());
    }

    @Test
    void typedBodyAndExitCodeMustBothSucceed() throws Exception {
        DreaminaCanvasCliExecutor cli = fake(output("{\"schemaVersion\":\"1\",\"ok\":true,\"data\":{\"version\":\"1.0.1\",\"future\":1}}", 0), 5000);
        assertEquals("1.0.1", cli.version().getData().getVersion());
        assertFalse(fake(output("{\"schemaVersion\":\"1\",\"ok\":true}", 2), 5000).version().isSuccess());
    }

    @Test
    void timeoutIsBoundedAndClassified() throws Exception {
        DreaminaCanvasCliExecutor cli = fake("exec sleep 20\n", 150);
        long start = System.nanoTime();
        assertThrows(io.github.easy4j.dreamina.exception.DreaminaCliTimeoutException.class, cli::version);
        assertTrue(Duration.ofNanos(System.nanoTime() - start).getSeconds() < 4);
    }

    @Test
    void interruptionRestoresFlag() throws Exception {
        DreaminaCanvasCliExecutor cli = fake("exec sleep 20\n", 5000);
        AtomicBoolean restored = new AtomicBoolean();
        Thread t = new Thread(() -> {
            try {
                cli.version();
            } catch (DreaminaCanvasCliException e) {
                restored.set(e.getReason() == DreaminaCanvasCliException.Reason.INTERRUPTED && Thread.currentThread().isInterrupted());
            }
        });
        t.start();
        Thread.sleep(100);
        t.interrupt();
        t.join(3000);
        assertFalse(t.isAlive());
        assertTrue(restored.get());
    }

    @Test
    void startFailureAndOutputLimit() throws Exception {
        DreaminaCanvasCliExecutor missing = new DreaminaCanvasCliExecutor(DreaminaCanvasCliProperties.builder().executable(dir.resolve("missing").toString()).build());
        assertThrows(io.github.easy4j.dreamina.exception.DreaminaCliExecutableFailureException.class, missing::version);
        DreaminaCanvasCliExecutor noisy = fake("while :; do printf '0123456789'; done\n", 3000);
        DreaminaCanvasCliExecutor bounded = new DreaminaCanvasCliExecutor(DreaminaCanvasCliProperties.builder().executable(noisy.getProperties().getExecutable()).maxOutputBytes(1024).timeout(Duration.ofSeconds(3)).build());
        assertEquals(DreaminaCanvasCliException.Reason.OUTPUT_LIMIT, assertThrows(DreaminaCanvasCliException.class, bounded::version).getReason());
    }

    @Test
    void durationsUseTypedValuesAndValidateRanges() {
        String id = "12345678-1234-1234-1234-123456789012";
        io.github.easy4j.dreamina.cli.opts.DreaminaCanvasOperationWaitRequest request = io.github.easy4j.dreamina.cli.opts.DreaminaCanvasOperationWaitRequest.builder()
                .operationRef(id).timeout(Duration.ofMillis(1234)).interval(Duration.ofSeconds(1)).build();
        assertTrue(request.toArguments().contains("--timeout=1234000000ns"));
        assertThrows(UnsupportedOperationException.class, () -> request.toArguments().clear());
        assertThrows(IllegalArgumentException.class, () -> DreaminaCanvasFixtures.request(DreaminaCanvasCommand.OPERATION_WAIT, "@" + id, "interval=0s"));
        assertThrows(IllegalArgumentException.class, () -> DreaminaCanvasFixtures.request(DreaminaCanvasCommand.OPERATION_WAIT, "@" + id, "timeout=-1s"));
        assertThrows(IllegalArgumentException.class, () -> DreaminaCanvasCliProperties.builder().timeout(Duration.ZERO).build());
        assertThrows(IllegalArgumentException.class, () -> DreaminaCanvasCliProperties.builder().region("us").build());
    }

    @Test
    void concurrencyIsPerClientAndQueueUsesSameTimeout() throws Exception {
        Path marker = dir.resolve("started");
        DreaminaCanvasCliExecutor source = fake("printf x >> '" + marker + "'\nexec /bin/sleep 20\n", 1000);
        DreaminaCanvasCliExecutor one = new DreaminaCanvasCliExecutor(DreaminaCanvasCliProperties.builder().executable(source.getProperties().getExecutable()).timeout(Duration.ofMillis(2000)).maxConcurrentExecutions(1).build());
        java.util.concurrent.atomic.AtomicReference<io.github.easy4j.dreamina.exception.DreaminaCliTimeoutException> firstFailure = new java.util.concurrent.atomic.AtomicReference<>();
        java.util.concurrent.atomic.AtomicReference<io.github.easy4j.dreamina.exception.DreaminaCliTimeoutException> secondFailure = new java.util.concurrent.atomic.AtomicReference<>();
        Thread first = new Thread(() -> {
            try {
                one.version();
            } catch (io.github.easy4j.dreamina.exception.DreaminaCliTimeoutException e) {
                firstFailure.set(e);
            }
        });
        first.start();
        long until = System.nanoTime() + Duration.ofSeconds(3).toNanos();
        while (!Files.exists(marker) && System.nanoTime() < until) {
            Thread.sleep(5);
        }
        assertTrue(Files.exists(marker));
        new DreaminaCanvasCliExecutor(DreaminaCanvasCliProperties.builder().maxConcurrentExecutions(20).build());
        long start = System.nanoTime();
        Thread second = new Thread(() -> {
            try {
                one.version();
            } catch (io.github.easy4j.dreamina.exception.DreaminaCliTimeoutException e) {
                secondFailure.set(e);
            }
        });
        second.start();
        Thread.sleep(100);
        assertEquals(1, Files.size(marker), "second call must still be queued in this client");
        // 另一客户端无需等待 one 的许可。
        assertTrue(fake(output("{\"schemaVersion\":\"1\",\"ok\":true}", 0), 5000).version().isSuccess());
        first.join(3500);
        second.join(3500);
        assertFalse(first.isAlive());
        assertFalse(second.isAlive());
        assertNotNull(firstFailure.get());
        assertNotNull(secondFailure.get());
        long elapsed = Duration.ofNanos(System.nanoTime() - start).toMillis();
        assertTrue(elapsed < 3500, "queue and process must share one budget");
    }

    @Test
    void convenienceMethodsTextHelpersAndExplicitConfirmation() throws Exception {
        DreaminaCanvasCliExecutor cli = fake(output("{\"schemaVersion\":\"1\",\"ok\":true,\"data\":{}}", 0), 5000);
        assertTrue(cli.versionDetails().isSuccess());
        assertTrue(cli.authAccountDetails().isSuccess());
        assertTrue(cli.help("node").isSuccess());
        assertTrue(cli.schema().isSuccess());
        assertTrue(cli.authStatus().isSuccess());
        assertTrue(cli.authAccount().isSuccess());
        assertTrue(cli.modelList("image").isSuccess());
        String id = "12345678-1234-1234-1234-123456789012";
        assertTrue(cli.execute(DreaminaCanvasFixtures.request(DreaminaCanvasCommand.OPERATION_STATUS, "@" + id)).isSuccess());
        assertTrue(cli.execute(DreaminaCanvasFixtures.request(DreaminaCanvasCommand.OPERATION_WAIT, "@" + id, "timeout=0", "interval=1s")).isSuccess());
        assertTrue(cli.help().isSuccess());
        assertTrue(cli.help("node create").isSuccess());
        assertTrue(cli.completion("fish", true).isSuccess());
        assertTrue(cli.completion("bash").isSuccess());
        assertThrows(IllegalArgumentException.class, () -> cli.help("node ; rm"));
        assertThrows(IllegalArgumentException.class, () -> cli.completion("unknown"));
        assertTrue(io.github.easy4j.dreamina.cli.opts.DreaminaCanvasAuthLogoutRequest.builder().yes(true).build().toArguments().contains("--yes"));
        assertThrows(NullPointerException.class, () -> cli.execute(null));
    }

    @Test
    void schemaCoversAllBusinessCommands() {
        assertEquals(37, DreaminaCanvasCommand.values().length);
        for (DreaminaCanvasCommand command : DreaminaCanvasCommand.values()) {
            assertEquals(command.getPath().substring(command.getPath().lastIndexOf(' ') + 1), DreaminaCanvasContract.describe(command).getName());
        }
    }

    public static class Version {
        public String version;
    }
}
