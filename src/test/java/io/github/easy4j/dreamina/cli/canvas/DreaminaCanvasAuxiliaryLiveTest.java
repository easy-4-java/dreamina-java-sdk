package io.github.easy4j.dreamina.cli.canvas;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.easy4j.dreamina.cli.DreaminaCanvasCliExecutor;
import io.github.easy4j.dreamina.cli.DreaminaCanvasResponse;
import io.github.easy4j.dreamina.cli.DreaminaCliResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/** 辅助命令实际执行凭证，与业务生成凭证分别记录。 */
class DreaminaCanvasAuxiliaryLiveTest {
    private final DreaminaCanvasCliExecutor cli = new DreaminaCanvasCliExecutor();
    @Test
    @EnabledIfSystemProperty(named = "dual.cli.live.auxiliary", matches = "true")
    void allCanvasAuxiliariesExecuteThroughSdk() throws Exception {
        DreaminaCanvasResponse<?> version = cli.version();
        receipt("version", version.isSuccess(), version.getExitCode());
        DreaminaCanvasResponse<?> schema = cli.schema();
        receipt("schema", schema.isSuccess(), schema.getExitCode());
        DreaminaCliResult help = cli.help("");
        receipt("help", help.isSuccess(), help.getExitCode());
        assertFalse(help.getStdout().isEmpty());
        for (String shell : new String[]{"bash", "zsh", "fish", "powershell"}) {
            DreaminaCliResult completion = cli.completion(shell);
            receipt("completion " + shell, completion.isSuccess(), completion.getExitCode());
            assertFalse(completion.getStdout().isEmpty());
        }
    }
    private void receipt(String command, boolean success, int exit) throws Exception {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("cli", "dreamina-canvas"); row.put("command", command); row.put("success", success); row.put("exitCode", exit);
        Path path = Paths.get(System.getProperty("dual.cli.evidenceDir"), "canvas-live.jsonl");
        Files.write(path, (new ObjectMapper().writeValueAsString(row) + "\n").getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        assertTrue(success, command);
    }
}
