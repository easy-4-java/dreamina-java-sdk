package io.github.easy4j.dreamina.cli.canvas;

import io.github.easy4j.dreamina.DreaminaCanvasCliProperties;
import io.github.easy4j.dreamina.cli.DreaminaCanvasCliExecutor;
import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;
import io.github.easy4j.dreamina.cli.DreaminaCanvasResponse;
import io.github.easy4j.dreamina.cli.DreaminaCliResult;
import io.github.easy4j.dreamina.cli.model.DreaminaCanvasSchema;
import io.github.easy4j.dreamina.cli.model.DreaminaVersion;
import io.github.easy4j.dreamina.cli.opts.DreaminaCanvasContract;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 仅显式启用；所有测试只读或使用官方本地 dry-run，不生成或扣积分。
 */
@EnabledIfSystemProperty(named = "dreamina.canvas.live", matches = "true")
class DreaminaCanvasInstalledCliTest {
    private final DreaminaCanvasCliExecutor cli = new DreaminaCanvasCliExecutor(DreaminaCanvasCliProperties.builder()
            .executable(System.getProperty("dreamina.canvas.executable", "dreamina-canvas")).build());

    @Test
    void installedVersionAndWholeSchemaMatchBundledContract() throws Exception {
        DreaminaCanvasResponse<DreaminaVersion> version = cli.version();
        assertTrue(version.isSuccess());
        assertEquals(version.getData().getBuildTime(), cli.versionDetails().getData().getBuildTime());
        assertTrue(new io.github.easy4j.dreamina.cli.availability.DreaminaCliAvailabilityChecker().checkCanvas(cli.getProperties()).isAvailable());
        assertEquals("1.0.1", version.getData().getVersion());
        DreaminaCanvasResponse<DreaminaCanvasSchema> schema = cli.schema();
        assertTrue(schema.isSuccess());
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        assertEquals(mapper.writeValueAsString(DreaminaCanvasContract.schema()), mapper.writeValueAsString(schema.getData()));
    }

    @Test
    void everyBusinessPathAndGroupHasWorkingHelp() {
        Set<String> paths = new HashSet<>();
        for (DreaminaCanvasCommand command : DreaminaCanvasCommand.values()) {
            String path = command.getPath();
            paths.add(path);
            while (path.contains(" ")) {
                path = path.substring(0, path.lastIndexOf(' '));
                paths.add(path);
            }
        }
        paths.add("");
        paths.add("help");
        paths.add("completion");
        for (String shell : Arrays.asList("bash", "zsh", "fish", "powershell")) {
            paths.add("completion " + shell);
        }
        for (String path : paths) {
            DreaminaCliResult help = cli.help(path);
            assertTrue(help.isSuccess(), path);
            assertFalse(help.getStdout().isEmpty(), path);
        }
    }

    @Test
    void allShellCompletionsExecute() {
        assertTrue(cli.help(io.github.easy4j.dreamina.cli.opts.DreaminaCanvasHelpRequest.builder().topic("node").build()).isSuccess());
        assertTrue(cli.completionBash(io.github.easy4j.dreamina.cli.opts.DreaminaCanvasCompletionBashRequest.builder().build()).isSuccess());
        assertTrue(cli.completionZsh(io.github.easy4j.dreamina.cli.opts.DreaminaCanvasCompletionZshRequest.builder().build()).isSuccess());
        assertTrue(cli.completionFish(io.github.easy4j.dreamina.cli.opts.DreaminaCanvasCompletionFishRequest.builder().build()).isSuccess());
        assertTrue(cli.completionPowershell(io.github.easy4j.dreamina.cli.opts.DreaminaCanvasCompletionPowershellRequest.builder().build()).isSuccess());
        for (String shell : Arrays.asList("bash", "zsh", "fish", "powershell")) {
            assertTrue(cli.completion(shell).isSuccess(), shell);
            assertTrue(cli.completion(shell, true).isSuccess(), shell);
        }
    }

    @Test
    void localImageDraftDryRunThroughSdk() {
        io.github.easy4j.dreamina.cli.opts.DreaminaCanvasNodeCreateImageRequest request = io.github.easy4j.dreamina.cli.opts.DreaminaCanvasNodeCreateImageRequest.builder()
                .title("SDK 验收").mode("t2i").prompt("Java SDK 验收：一只橘猫，白色背景").dryRun(true).build();
        assertTrue(cli.nodeCreateImage(request).isSuccess());
        assertTrue(cli.nodeCreateImage(request).getData().getDryRun());
    }

    @Test
    void localTextAndTimelineDraftsThroughSdk() {
        assertTrue(cli.execute(DreaminaCanvasFixtures.request(DreaminaCanvasCommand.NODE_CREATE_TEXT, "dry-run=true", "title=SDK 验收", "text=中文\n第二行")).isSuccess());
        assertTrue(cli.execute(DreaminaCanvasFixtures.request(DreaminaCanvasCommand.NODE_CREATE_TIMELINE, "dry-run=true", "title=SDK 验收", "clip=12345678-1234-1234-1234-123456789012,duration=1000")).isSuccess());
    }
}
