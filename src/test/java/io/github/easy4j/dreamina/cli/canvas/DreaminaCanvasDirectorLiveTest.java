package io.github.easy4j.dreamina.cli.canvas;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.easy4j.dreamina.DreaminaCanvasCliProperties;
import io.github.easy4j.dreamina.cli.DreaminaCanvasCliExecutor;
import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;
import io.github.easy4j.dreamina.cli.DreaminaCanvasResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/** 仅显式启用；专用画布导演台与本地渲染，不触发生成模型。 */
@EnabledIfSystemProperty(named = "dual.cli.live.director", matches = "true")
class DreaminaCanvasDirectorLiveTest {
    private final ObjectMapper mapper = new ObjectMapper();
    private final DreaminaCanvasCliExecutor cli = new DreaminaCanvasCliExecutor(DreaminaCanvasCliProperties.builder().build());
    private Path directory() {
        return Paths.get(System.getProperty("dual.cli.evidenceDir"));
    }
    private DreaminaCanvasResponse<?> call(DreaminaCanvasCommand command, String... arguments) throws Exception {
        DreaminaCanvasResponse<?> result = cli.execute(DreaminaCanvasFixtures.request(command, arguments));
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("cli", "dreamina-canvas");
        row.put("command", command.getPath());
        row.put("success", result.isSuccess());
        row.put("exitCode", result.getExitCode());
        row.put("errorCode", Objects.isNull(result.getError()) ? null : result.getError().getCode());
        Files.write(directory().resolve("canvas-live.jsonl"), (mapper.writeValueAsString(row) + "\n").getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        if (!result.isSuccess()) {
            Map<String, Object> diagnostics = new LinkedHashMap<>();
            diagnostics.put("command", command.getPath());
            diagnostics.put("error", result.getError());
            diagnostics.put("partialData", result.getPartialData());
            mapper.writeValue(directory().resolve(command.name().toLowerCase(java.util.Locale.ROOT) + "-error.json").toFile(), diagnostics);
        }
        assertTrue(result.isSuccess(), command + " failed; preserve original work file and inspect structured diagnostics");
        return result;
    }
    @Test
    @EnabledIfSystemProperty(named = "dual.cli.live.directorCreate", matches = "true")
    void createEditPushPullDedicatedDirector() throws Exception {
        JsonNode assets = mapper.readTree(directory().resolve("canvas-assets.json").toFile());
        Path ledger = directory().resolve("director-assets.json");
        assertFalse(Files.exists(ledger), "resume existing director ledger; do not repeat create");
        String project = assets.path("projectId").asText();
        call(DreaminaCanvasCommand.SCHEMA_DIRECTOR);
        for (String kind : new String[]{"builtin", "character", "motion"}) {
            DreaminaCanvasResponse<?> catalog = call(DreaminaCanvasCommand.DIRECTOR_CATALOG, "kind=" + kind, "count=1", "offset=0");
            mapper.writeValue(directory().resolve("director-catalog-" + kind + ".json").toFile(), catalog.getData());
        }
        Path work = directory().resolve("director-scene.json");
        Map<String, String> saved = new LinkedHashMap<>();
        saved.put("projectId", project);
        saved.put("file", work.toString());
        saved.put("state", "create-pending");
        mapper.writeValue(ledger.toFile(), saved);
        DreaminaCanvasResponse<?> created = call(DreaminaCanvasCommand.DIRECTOR_CREATE, "project-id=" + project, "title=SDK-E2E-director", "file=" + work);
        JsonNode receipt = mapper.valueToTree(created.getData());
        assertTrue(receipt.path("savedDraftVersion").asLong() > 0);
        assertFalse(receipt.path("versionId").asText().isEmpty());
        String node = receipt.path("nodeId").asText();
        saved.put("nodeId", node);
        saved.put("versionId", receipt.path("versionId").asText());
        saved.put("state", "created");
        mapper.writeValue(ledger.toFile(), saved);
        call(DreaminaCanvasCommand.DIRECTOR_SHOW, "file=" + work);
        Path input = directory().resolve("director-commands.json");
        Files.write(input, ("[{\"type\":\"document.title\",\"title\":\"SDK-E2E-director-verified\"},"
                + "{\"type\":\"object.primitive\",\"primitive\":\"cube\",\"id\":\"sdk-cube\",\"name\":\"SDK Cube\"},"
                + "{\"type\":\"workspace.performance\",\"copyStaging\":true},"
                + "{\"type\":\"timeline.set\",\"durationFrames\":60},"
                + "{\"type\":\"keyframe.set\",\"id\":\"sdk-cube\",\"channel\":\"rotation.y\",\"frame\":0,\"value\":0},"
                + "{\"type\":\"keyframe.set\",\"id\":\"sdk-cube\",\"channel\":\"rotation.y\",\"frame\":60,\"value\":180}]").getBytes(StandardCharsets.UTF_8));
        call(DreaminaCanvasCommand.DIRECTOR_APPLY, "file=" + work, "input=" + input);
        DreaminaCanvasResponse<?> pushed = call(DreaminaCanvasCommand.DIRECTOR_PUSH, "file=" + work, "project-id=" + project);
        String version = mapper.valueToTree(pushed.getData()).path("versionId").asText();
        assertFalse(version.isEmpty());
        DreaminaCanvasResponse<?> shown = call(DreaminaCanvasCommand.DIRECTOR_SHOW, "project-id=" + project, "node-id=" + node);
        assertEquals(version, mapper.valueToTree(shown.getData()).path("versionId").asText());
        call(DreaminaCanvasCommand.DIRECTOR_PULL, "project-id=" + project, "node-id=" + node, "file=" + directory().resolve("director-pulled.json"));
        saved.put("versionId", version);
        saved.put("state", "edit-push-pull-complete");
        mapper.writeValue(ledger.toFile(), saved);
    }
    @Test
    @EnabledIfSystemProperty(named = "dual.cli.live.directorResume", matches = "true")
    void resumePendingWriteWithSameFileWithoutRecreating() throws Exception {
        Path ledger = directory().resolve("director-assets.json");
        Map<String, String> saved = mapper.readValue(ledger.toFile(), new com.fasterxml.jackson.core.type.TypeReference<Map<String, String>>() {});
        assertEquals("create-pending", saved.get("state"));
        Path work = Paths.get(saved.get("file"));
        JsonNode pending = mapper.readTree(work.toFile());
        assertEquals("create", pending.path("pending").path("action").asText());
        String node = pending.path("nodeId").asText();
        String originalWrite = pending.path("pending").path("writeId").asText();
        DreaminaCanvasResponse<?> recovered = call(DreaminaCanvasCommand.DIRECTOR_PUSH,
                "file=" + work, "project-id=" + saved.get("projectId"));
        JsonNode receipt = mapper.valueToTree(recovered.getData());
        assertEquals(node, receipt.path("nodeId").asText());
        saved.put("nodeId", node);
        saved.put("originalWriteId", originalWrite);
        saved.put("versionId", receipt.path("versionId").asText());
        saved.put("state", "create-recovered");
        mapper.writeValue(ledger.toFile(), saved);
    }

    @Test
    @EnabledIfSystemProperty(named = "dual.cli.live.directorEdit", matches = "true")
    void editRecoveredDirectorAndVerifySavedVersion() throws Exception {
        Path ledger = directory().resolve("director-assets.json");
        Map<String, String> saved = mapper.readValue(ledger.toFile(), new com.fasterxml.jackson.core.type.TypeReference<Map<String, String>>() {});
        assertEquals("create-recovered", saved.get("state"));
        String project = saved.get("projectId");
        String node = saved.get("nodeId");
        Path work = Paths.get(saved.get("file"));
        call(DreaminaCanvasCommand.DIRECTOR_SHOW, "file=" + work);
        Path input = directory().resolve("director-commands.json");
        Files.write(input, ("[{\"type\":\"document.title\",\"title\":\"SDK-E2E-director-verified\"},"
                + "{\"type\":\"object.primitive\",\"primitive\":\"cube\",\"id\":\"sdk-cube\",\"name\":\"SDK Cube\"},"
                + "{\"type\":\"workspace.performance\",\"copyStaging\":true},"
                + "{\"type\":\"timeline.set\",\"durationFrames\":60},"
                + "{\"type\":\"keyframe.set\",\"id\":\"sdk-cube\",\"channel\":\"rotation.y\",\"frame\":0,\"value\":0},"
                + "{\"type\":\"keyframe.set\",\"id\":\"sdk-cube\",\"channel\":\"rotation.y\",\"frame\":60,\"value\":180}]").getBytes(StandardCharsets.UTF_8));
        call(DreaminaCanvasCommand.DIRECTOR_APPLY, "file=" + work, "input=" + input);
        DreaminaCanvasResponse<?> pushed = call(DreaminaCanvasCommand.DIRECTOR_PUSH, "file=" + work, "project-id=" + project);
        String version = mapper.valueToTree(pushed.getData()).path("versionId").asText();
        assertFalse(version.isEmpty());
        DreaminaCanvasResponse<?> shown = call(DreaminaCanvasCommand.DIRECTOR_SHOW, "project-id=" + project, "node-id=" + node);
        assertEquals(version, mapper.valueToTree(shown.getData()).path("versionId").asText());
        call(DreaminaCanvasCommand.DIRECTOR_PULL, "project-id=" + project, "node-id=" + node, "file=" + directory().resolve("director-pulled.json"));
        saved.put("versionId", version);
        saved.put("state", "edit-push-pull-complete");
        mapper.writeValue(ledger.toFile(), saved);
    }

    @Test
    @EnabledIfSystemProperty(named = "dual.cli.live.directorRender", matches = "true")
    void renderPreviewImageVideoAndResumeCanvasDelivery() throws Exception {
        Path ledger = directory().resolve("director-assets.json");
        Map<String, String> saved = mapper.readValue(ledger.toFile(), new com.fasterxml.jackson.core.type.TypeReference<Map<String, String>>() {});
        assertEquals("edit-push-pull-complete", saved.get("state"));
        assertFalse(saved.containsKey("renderState"), "use saved delivery identity rather than repeating export");
        saved.put("renderState", "started");
        mapper.writeValue(ledger.toFile(), saved);
        String file = "file=" + saved.get("file");
        DreaminaCanvasResponse<?> preview = call(DreaminaCanvasCommand.DIRECTOR_PREVIEW, file,
                "workspace=performance", "frame=30", "output=" + directory().resolve("director-preview.png"));
        mapper.writeValue(directory().resolve("director-preview-receipt.json").toFile(), preview.getData());
        DreaminaCanvasResponse<?> image = call(DreaminaCanvasCommand.DIRECTOR_EXPORT, file,
                "type=image", "workspace=performance", "frame=30", "output=" + directory().resolve("director-image.png"));
        mapper.writeValue(directory().resolve("director-image-receipt.json").toFile(), image.getData());
        DreaminaCanvasResponse<?> video = call(DreaminaCanvasCommand.DIRECTOR_EXPORT, file,
                "type=video", "workspace=performance", "output=" + directory().resolve("director-video.mp4"));
        mapper.writeValue(directory().resolve("director-video-receipt.json").toFile(), video.getData());
        DreaminaCanvasResponse<?> canvas = call(DreaminaCanvasCommand.DIRECTOR_EXPORT, file,
                "type=image", "workspace=performance", "frame=30", "to-canvas=true");
        JsonNode receipt = mapper.valueToTree(canvas.getData());
        mapper.writeValue(directory().resolve("director-canvas-export-receipt.json").toFile(), canvas.getData());
        String reference = receipt.path("operationRef").asText();
        assertFalse(reference.isEmpty());
        saved.put("exportOperationRef", reference);
        saved.put("exportResourceId", receipt.path("canvas").path("resourceId").asText());
        saved.put("renderState", "exported");
        mapper.writeValue(ledger.toFile(), saved);
        DreaminaCanvasResponse<?> resumed = call(DreaminaCanvasCommand.DIRECTOR_RESUME_EXPORT, "operation-ref=" + reference);
        assertEquals(saved.get("exportResourceId"), mapper.valueToTree(resumed.getData()).path("canvas").path("resourceId").asText());
        call(DreaminaCanvasCommand.DIRECTOR_FREEZE, file, "frame=30");
        call(DreaminaCanvasCommand.DIRECTOR_PUSH, file, "project-id=" + saved.get("projectId"));
        saved.put("renderState", "preview-export-resume-freeze-complete");
        mapper.writeValue(ledger.toFile(), saved);
    }

    @Test
    @EnabledIfSystemProperty(named = "dual.cli.live.directorConflict", matches = "true")
    void concurrentVersionConflictPreservesDiagnosticsAndRestoresTitleWithoutForce() throws Exception {
        JsonNode saved = mapper.readTree(directory().resolve("director-assets.json").toFile());
        String project = "project-id=" + saved.path("projectId").asText();
        String node = "node-id=" + saved.path("nodeId").asText();
        Path first = directory().resolve("director-conflict-a.json");
        Path stale = directory().resolve("director-conflict-b.json");
        assertFalse(Files.exists(first), "resume recorded conflict files rather than repeating the scenario");
        call(DreaminaCanvasCommand.DIRECTOR_PULL, project, node, "file=" + first);
        call(DreaminaCanvasCommand.DIRECTOR_PULL, project, node, "file=" + stale);
        Path firstCommand = directory().resolve("director-conflict-a-commands.json");
        Path staleCommand = directory().resolve("director-conflict-b-commands.json");
        Files.write(firstCommand, "[{\"type\":\"document.title\",\"title\":\"SDK-E2E-conflict-a\"}]".getBytes(StandardCharsets.UTF_8));
        Files.write(staleCommand, "[{\"type\":\"document.title\",\"title\":\"SDK-E2E-conflict-b\"}]".getBytes(StandardCharsets.UTF_8));
        call(DreaminaCanvasCommand.DIRECTOR_APPLY, "file=" + first, "input=" + firstCommand);
        DreaminaCanvasResponse<?> latest = call(DreaminaCanvasCommand.DIRECTOR_PUSH, project, "file=" + first);
        String version = mapper.valueToTree(latest.getData()).path("versionId").asText();
        call(DreaminaCanvasCommand.DIRECTOR_APPLY, "file=" + stale, "input=" + staleCommand);
        DreaminaCanvasResponse<?> rejected = cli.execute(DreaminaCanvasFixtures.request(DreaminaCanvasCommand.DIRECTOR_PUSH, project, "file=" + stale));
        mapper.writeValue(directory().resolve("director-version-conflict-error.json").toFile(), rejected.getError());
        assertFalse(rejected.isSuccess());
        assertEquals("cli.director_version_conflict", rejected.getError().getCode());
        assertNotNull(rejected.getDirectorConflict());
        assertEquals(version, rejected.getDirectorConflict().getLatestVersionId());
        Path restored = directory().resolve("director-conflict-restored.json");
        Path restoreCommand = directory().resolve("director-restore-commands.json");
        call(DreaminaCanvasCommand.DIRECTOR_PULL, project, node, "file=" + restored);
        Files.write(restoreCommand, "[{\"type\":\"document.title\",\"title\":\"SDK-E2E-director-verified\"}]".getBytes(StandardCharsets.UTF_8));
        call(DreaminaCanvasCommand.DIRECTOR_APPLY, "file=" + restored, "input=" + restoreCommand);
        call(DreaminaCanvasCommand.DIRECTOR_PUSH, project, "file=" + restored);
        DreaminaCanvasResponse<?> readback = call(DreaminaCanvasCommand.DIRECTOR_SHOW, project, node);
        assertTrue(mapper.valueToTree(readback.getData()).path("document").toString().contains("SDK-E2E-director-verified"));
        mapper.writeValue(directory().resolve("director-version-conflict-proof.json").toFile(), java.util.Collections.singletonMap("state", "rejected-stale-version-and-restored-without-force"));
    }

    @Test
    @EnabledIfSystemProperty(named = "dual.cli.live.directorConflictResume", matches = "true")
    void parseRealTopLevelConflictAndRestoreThroughFreshVersion() throws Exception {
        Path ledger = directory().resolve("director-assets.json");
        Map<String, String> saved = mapper.readValue(ledger.toFile(), new com.fasterxml.jackson.core.type.TypeReference<Map<String, String>>() {});
        String error = new String(Files.readAllBytes(directory().resolve("director-version-conflict-envelope.json")), StandardCharsets.UTF_8);
        DreaminaCanvasResponse<?> rejected = new io.github.easy4j.dreamina.cli.parser.DreaminaCanvasResponseParser().parse("director push",
                io.github.easy4j.dreamina.cli.DreaminaCliResult.builder().stdout("").stderr(error).exitCode(2).success(false).build(),
                io.github.easy4j.dreamina.cli.model.DreaminaCanvasDirectorPushResult.class);
        assertNotNull(rejected.getDirectorConflict());
        assertEquals(saved.get("nodeId"), rejected.getDirectorConflict().getNodeId());
        String latest = rejected.getDirectorConflict().getLatestVersionId();
        assertNotEquals(rejected.getDirectorConflict().getBaseVersionId(), latest);
        String project = "project-id=" + saved.get("projectId");
        String node = "node-id=" + saved.get("nodeId");
        DreaminaCanvasResponse<?> current = call(DreaminaCanvasCommand.DIRECTOR_SHOW, project, node);
        assertEquals(latest, mapper.valueToTree(current.getData()).path("versionId").asText());
        Path restored = directory().resolve("director-conflict-restored.json");
        assertFalse(Files.exists(restored), "do not overwrite a work file");
        Path commands = directory().resolve("director-restore-commands.json");
        call(DreaminaCanvasCommand.DIRECTOR_PULL, project, node, "version-id=" + latest, "file=" + restored);
        Files.write(commands, "[{\"type\":\"document.title\",\"title\":\"SDK-E2E-director-verified\"}]".getBytes(StandardCharsets.UTF_8));
        call(DreaminaCanvasCommand.DIRECTOR_APPLY, "file=" + restored, "input=" + commands);
        call(DreaminaCanvasCommand.DIRECTOR_PUSH, project, "file=" + restored);
        DreaminaCanvasResponse<?> readback = call(DreaminaCanvasCommand.DIRECTOR_SHOW, project, node);
        assertTrue(mapper.valueToTree(readback.getData()).path("document").toString().contains("SDK-E2E-director-verified"));
        saved.put("file", restored.toString());
        saved.put("versionId", mapper.valueToTree(readback.getData()).path("versionId").asText());
        mapper.writeValue(ledger.toFile(), saved);
        mapper.writeValue(directory().resolve("director-version-conflict-proof.json").toFile(), java.util.Collections.singletonMap("state", "root-conflict-preserved-and-restored-without-force"));
    }

}
