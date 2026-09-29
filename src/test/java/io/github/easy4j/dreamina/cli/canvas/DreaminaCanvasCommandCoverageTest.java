package io.github.easy4j.dreamina.cli.canvas;

import io.github.easy4j.dreamina.DreaminaCanvasCliProperties;
import io.github.easy4j.dreamina.cli.DreaminaCanvasCliExecutor;
import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;
import io.github.easy4j.dreamina.cli.DreaminaCanvasResponse;
import io.github.easy4j.dreamina.cli.model.DreaminaCanvasSchemaCommand;
import io.github.easy4j.dreamina.cli.opts.DreaminaCanvasContract;
import io.github.easy4j.dreamina.cli.opts.DreaminaCanvasRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 每个 schema 业务入口独立形成测试用例，不能以循环里的一个 assert 代替逐条结果。
 */
class DreaminaCanvasCommandCoverageTest {
    private static final String ID = "12345678-1234-1234-1234-123456789012";
    private static final String NODE = "node_0000000001";
    @TempDir
    Path dir;

    static Stream<Case> commands() {
        return cases().values().stream();
    }

    static Map<DreaminaCanvasCommand, Case> cases() {
        Map<DreaminaCanvasCommand, Case> cases = new LinkedHashMap<>();
        add(cases, DreaminaCanvasCommand.MODEL, "@seedream_4.7");
        add(cases, DreaminaCanvasCommand.MODEL_LIST, "type=image");
        add(cases, DreaminaCanvasCommand.MODEL_FIND, "@4.7", "type=image");
        add(cases, DreaminaCanvasCommand.VOICE_LIST, "count=1", "offset=0");
        for (String action : Arrays.asList("CREATE", "EDIT")) {
            for (String kind : Arrays.asList("IMAGE", "VIDEO", "AUDIO", "ELEMENT", "TEXT", "TIMELINE")) {
                List<String> args = new ArrayList<>(Arrays.asList("project-id=" + ID, "title=中文 '标题'\n$(literal)", "dry-run=true"));
                if ("EDIT".equals(action)) {
                    args.add("node-id=" + NODE);
                }
                if ("ELEMENT".equals(kind)) {
                    args.add("main=" + NODE);
                }
                if ("TEXT".equals(kind)) {
                    args.add("text=第一行\n第二行");
                }
                if ("TIMELINE".equals(kind)) {
                    args.add("clip=" + ID + ",duration=1000");
                }
                add(cases, DreaminaCanvasCommand.valueOf("NODE_" + action + "_" + kind), args.toArray(new String[0]));
            }
        }
        add(cases, DreaminaCanvasCommand.NODE_UPSCALE_IMAGE, "node-id=" + NODE, "mode=normal", "dry-run=true");
        add(cases, DreaminaCanvasCommand.NODE_SHOW, "node-id=" + NODE, "node-id=node_0000000002");
        add(cases, DreaminaCanvasCommand.NODE_FIND, "name=SDK", "type=image", "type=video", "limit=2");
        add(cases, DreaminaCanvasCommand.NODE_QUOTE, "node-id=" + NODE);
        add(cases, DreaminaCanvasCommand.NODE_CONFIRM, "node-id=" + NODE, "credit-ceiling=0");
        add(cases, DreaminaCanvasCommand.NODE_RUN, "node-id=" + NODE, "submit-id=" + ID, "credit-token=test-only-token");
        add(cases, DreaminaCanvasCommand.OPERATION_STATUS, "@" + ID);
        add(cases, DreaminaCanvasCommand.OPERATION_WAIT, "@" + ID, "timeout=0", "interval=1s");
        add(cases, DreaminaCanvasCommand.RESOURCE_DOWNLOAD, "@" + ID, "output=path with spaces.png");
        add(cases, DreaminaCanvasCommand.RESOURCE_GET, "@" + ID);
        add(cases, DreaminaCanvasCommand.RESOURCE_UPLOAD, "file=path with spaces.png", "type=image", "resource-id=" + ID);
        add(cases, DreaminaCanvasCommand.CANVAS_CREATE, "@--a literal name", "project-id=" + ID, "use=false");
        add(cases, DreaminaCanvasCommand.CANVAS_LS, "limit=1");
        add(cases, DreaminaCanvasCommand.AUTH_ACCOUNT);
        add(cases, DreaminaCanvasCommand.AUTH_LOGIN, "force=false");
        add(cases, DreaminaCanvasCommand.AUTH_LOGOUT);
        add(cases, DreaminaCanvasCommand.AUTH_STATUS);
        add(cases, DreaminaCanvasCommand.AUTH_WAIT, "device-code=test-only-code", "timeout=0");
        add(cases, DreaminaCanvasCommand.AUTH_REFRESH);
        add(cases, DreaminaCanvasCommand.SCHEMA);
        add(cases, DreaminaCanvasCommand.VERSION);
        return cases;
    }

    private static void add(Map<DreaminaCanvasCommand, Case> cases, DreaminaCanvasCommand command, String... args) {
        cases.put(command, new Case(command, args));
    }

    private static void collect(DreaminaCanvasSchemaCommand node, String path, Set<String> result) {
        if (node.getSuccessData() != null) {
            result.add(path);
        }
        if (node.getSubcommands() != null) {
            for (DreaminaCanvasSchemaCommand child : node.getSubcommands()) {
                collect(child, path + " " + child.getName(), result);
            }
        }
    }

    @Test
    void schemaEnumAndTestCasesHaveExactlyTheSameRoutes() {
        Set<String> schema = new LinkedHashSet<>();
        for (DreaminaCanvasSchemaCommand c : DreaminaCanvasContract.schema().getSubcommands()) {
            collect(c, c.getName(), schema);
        }
        Set<String> enums = new LinkedHashSet<>();
        Arrays.stream(DreaminaCanvasCommand.values()).forEach(c -> enums.add(c.getPath()));
        Set<String> fixtures = new LinkedHashSet<>();
        cases().keySet().forEach(c -> fixtures.add(c.getPath()));
        assertEquals(schema, enums, "schema versus SDK routes");
        assertEquals(schema, fixtures, "schema versus tested routes");
    }

    @ParameterizedTest(name = "success argv: {0}")
    @MethodSource("commands")
    void everyRoutePreservesLiteralArgumentsAndMapsData(Case testCase) throws Exception {
        DreaminaCanvasResponse<?> response = run(testCase, 0, false);
        assertTrue(response.isSuccess());
        assertEquals(testCase.request().getDataType(), response.getData().getClass());
    }

    @ParameterizedTest(name = "structured failure: {0}")
    @MethodSource("commands")
    void everyRoutePreservesBusinessFailureAndRecovery(Case testCase) throws Exception {
        int exit = DreaminaCanvasContract.describe(testCase.command).getDeclaredExitCodes().get(1).intValue();
        DreaminaCanvasResponse<?> response = run(testCase, exit, true);
        assertFalse(response.isSuccess());
        assertEquals(exit, response.getExitCode());
        assertEquals("test.expected_failure", response.getError().getCode());
        assertEquals("resume", response.getError().getRequiredAction());
        assertEquals(ID, response.getPartialData().getSubmitId());
    }

    private DreaminaCanvasResponse<?> run(Case c, int exit, boolean stderr) throws Exception {
        Path argv = dir.resolve("argv"), bin = dir.resolve("mock canvas");
        String envelope = stderr ? "{\"schemaVersion\":\"1\",\"ok\":false,\"error\":{\"code\":\"test.expected_failure\",\"requiredAction\":\"resume\"},\"partialData\":{\"submitId\":\"" + ID + "\"}}"
                : "{\"schemaVersion\":\"1\",\"ok\":true,\"data\":{\"route\":\"" + c.command.getPath() + "\"}}";
        String script = "#!/bin/sh\nprintf '%s\\000' \"$@\" > '" + argv + "'\nprintf '%s\\n' '" + envelope + "'" + (stderr ? " >&2" : "") + "\nexit " + exit + "\n";
        Files.write(bin, script.getBytes(StandardCharsets.UTF_8));
        assertTrue(bin.toFile().setExecutable(true));
        DreaminaCanvasCliExecutor executor = new DreaminaCanvasCliExecutor(DreaminaCanvasCliProperties.builder().executable(bin.toString()).build());
        DreaminaCanvasRequest<?> request = c.request();
        StringBuilder method = new StringBuilder();
        for (String word : c.command.getPath().split(" ")) {
            method.append(method.length() == 0 ? word : Character.toUpperCase(word.charAt(0)) + word.substring(1));
        }
        DreaminaCanvasResponse<?> response = (DreaminaCanvasResponse<?>) executor.getClass().getMethod(method.toString(), request.getClass()).invoke(executor, request);
        List<String> expected = new ArrayList<>(Arrays.asList("--format=json", "--non-interactive", "--profile=default", "--region=cn"));
        expected.addAll(Arrays.asList(c.command.getPath().split(" ")));
        for (String arg : c.args) {
            if (!arg.startsWith("@")) {
                expected.add("--" + arg.replace("timeout=0", "timeout=0ns").replace("interval=1s", "interval=1000000000ns"));
            }
        }
        boolean first = true;
        for (String arg : c.args) {
            if (arg.startsWith("@")) {
                if (first) {
                    expected.add("--");
                    first = false;
                }
                expected.add(arg.substring(1));
            }
        }
        List<String> actual = Arrays.asList(new String(Files.readAllBytes(argv), StandardCharsets.UTF_8).split("\u0000"));
        assertEquals(expected.size(), actual.size());
        assertEquals(new java.util.HashSet<>(expected), new java.util.HashSet<>(actual));
        return response;
    }

    static final class Case {
        final DreaminaCanvasCommand command;
        final String[] args;

        Case(DreaminaCanvasCommand command, String[] args) {
            this.command = command;
            this.args = args;
        }

        DreaminaCanvasRequest<?> request() {
            return DreaminaCanvasFixtures.request(command, args);
        }

        @Override
        public String toString() {
            return command.getPath();
        }
    }
}
