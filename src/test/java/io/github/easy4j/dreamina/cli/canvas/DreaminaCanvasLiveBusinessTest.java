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
import java.util.UUID;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/** 显式启用的无扣费画布验收；使用独立业务状态目录和本轮资产，不运行生成。 */
class DreaminaCanvasLiveBusinessTest {
    private final ObjectMapper mapper = new ObjectMapper();
    private final DreaminaCanvasCliExecutor cli = new DreaminaCanvasCliExecutor(DreaminaCanvasCliProperties.builder()
            .executable(System.getProperty("dreamina.canvas.executable", "dreamina-canvas")).build());
    private Path directory() throws Exception {
        Path dir = Paths.get(System.getProperty("dual.cli.evidenceDir", "target/dual-cli-live"));
        Files.createDirectories(dir);
        return dir;
    }
    private DreaminaCanvasResponse<?> call(DreaminaCanvasCommand command, String... args) throws Exception {
        DreaminaCanvasResponse<?> response = cli.execute(DreaminaCanvasFixtures.request(command, args));
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("cli", "dreamina-canvas");
        row.put("command", command.getPath());
        row.put("success", response.isSuccess());
        row.put("exitCode", response.getExitCode());
        row.put("errorCode", Objects.isNull(response.getError()) ? null : response.getError().getCode());
        Files.write(directory().resolve("canvas-live.jsonl"), (mapper.writeValueAsString(row) + "\n").getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        assertTrue(response.isSuccess(), command.getPath() + " failed; see structured exit code in evidence");
        return response;
    }
    @Test
    @EnabledIfSystemProperty(named = "dual.cli.live.canvasBootstrap", matches = "true")
    void bootstrapDedicatedCanvasWithoutGeneration() throws Exception {
        String state = System.getenv("DREAMINA_CANVAS_STATE_DIR");
        assertNotNull(state, "isolated official business state directory is required");
        assertTrue(Paths.get(state).toAbsolutePath().startsWith(directory().toAbsolutePath()));
        call(DreaminaCanvasCommand.AUTH_STATUS);
        call(DreaminaCanvasCommand.AUTH_ACCOUNT);
        call(DreaminaCanvasCommand.TEAM_LS, "limit=10");
        call(DreaminaCanvasCommand.SPACE_CURRENT);
        call(DreaminaCanvasCommand.SPACE_USE, "@personal");
        call(DreaminaCanvasCommand.CANVAS_CURRENT);
        DreaminaCanvasResponse<?> models = call(DreaminaCanvasCommand.MODEL_LIST, "type=image");
        call(DreaminaCanvasCommand.MODEL_LIST, "type=video");
        call(DreaminaCanvasCommand.MODEL_LIST, "type=audio");
        JsonNode items = mapper.valueToTree(models.getData()).path("items");
        assertTrue(items.isArray() && items.size() > 0, "no callable image model available");
        String model = items.get(0).path("model").asText();
        assertFalse(model.isEmpty());
        call(DreaminaCanvasCommand.MODEL, "@" + model);
        call(DreaminaCanvasCommand.MODEL_FIND, "@" + model, "type=image");
        call(DreaminaCanvasCommand.VOICE_LIST, "count=1", "offset=0");
        call(DreaminaCanvasCommand.CANVAS_LS, "limit=1");
        Path ledger = directory().resolve("canvas-assets.json");
        assertFalse(Files.exists(ledger), "existing asset ledger must be resumed; never duplicate a canvas");
        String id = UUID.randomUUID().toString();
        Map<String, String> assets = new LinkedHashMap<>();
        assets.put("projectId", id);
        assets.put("name", "SDK-E2E-CONTRACT-20261028");
        assets.put("createState", "pending");
        mapper.writeValue(ledger.toFile(), assets);
        DreaminaCanvasResponse<?> created = call(DreaminaCanvasCommand.CANVAS_CREATE, "project-id=" + id,
                "@" + assets.get("name"), "use=false");
        JsonNode project = mapper.valueToTree(created.getData()).path("project");
        assertEquals(id, project.path("projectId").asText());
        assets.put("createState", "created");
        assets.put("webUrl", project.path("webUrl").asText());
        assets.put("imageModel", model);
        mapper.writeValue(ledger.toFile(), assets);
        call(DreaminaCanvasCommand.CANVAS_USE, "@" + id);
        call(DreaminaCanvasCommand.CANVAS_CURRENT);
    }

    private void saveAssets(Map<String, String> assets) throws Exception {
        mapper.writeValue(directory().resolve("canvas-assets.json").toFile(), assets);
    }
    private String plannedNode(Map<String, String> assets, String key) throws Exception {
        String id = "node_" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        assets.put(key, id);
        saveAssets(assets);
        return id;
    }
    @Test
    @EnabledIfSystemProperty(named = "dual.cli.live.canvasNoFee", matches = "true")
    void isolatedResourceAndNodeWritesWithoutRunOrApproval() throws Exception {
        Map<String, String> a = mapper.readValue(directory().resolve("canvas-assets.json").toFile(),
                new com.fasterxml.jackson.core.type.TypeReference<Map<String, String>>() {});
        assertEquals("created", a.get("createState"));
        boolean resume = Boolean.getBoolean("dual.cli.live.resumeValidatedInput");
        if (a.containsKey("noFeeState")) {
            assertTrue(resume && "pending".equals(a.get("noFeeState")) && !a.containsKey("textNode"), "only the inspected pre-submit ID validation failure may resume here");
            assertFalse(a.get("importedImageNode").matches("node_[0-9abcdefghjkmnpqrstvwxyz]{10}"));
        }
        a.put("noFeeState", "pending");
        saveAssets(a);
        String project = "project-id=" + a.get("projectId");
        String resource;
        if (resume) {
            resource = a.get("imageResource");
            call(DreaminaCanvasCommand.RESOURCE_GET, project, "@" + resource);
        } else {
        java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(512, 512, java.awt.image.BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D graphics = image.createGraphics();
        graphics.setColor(java.awt.Color.WHITE);
        graphics.fillRect(0, 0, 512, 512);
        graphics.setColor(java.awt.Color.BLUE);
        graphics.fillRect(128, 128, 256, 256);
        graphics.dispose();
        Path png = directory().resolve("sdk-test-pattern.png");
        assertTrue(javax.imageio.ImageIO.write(image, "png", png.toFile()));
        resource = UUID.randomUUID().toString();
        a.put("imageResource", resource);
        saveAssets(a);
        call(DreaminaCanvasCommand.RESOURCE_UPLOAD, project, "file=" + png, "type=image", "resource-id=" + resource, "name=SDK-E2E-pattern");
        call(DreaminaCanvasCommand.RESOURCE_GET, project, "@" + resource);
        Path download = directory().resolve("downloads");
        Files.createDirectories(download);
        call(DreaminaCanvasCommand.RESOURCE_DOWNLOAD, project, "@" + resource, "output=" + download);
        }
        String imported = plannedNode(a, "importedImageNode");
        call(DreaminaCanvasCommand.NODE_CREATE_IMAGE, project, "node-id=" + imported, "title=SDK-E2E-import", "resource-id=" + resource, "import-kind=local_upload");
        call(DreaminaCanvasCommand.NODE_EDIT_IMAGE, project, "node-id=" + imported, "title=SDK-E2E-import-verified", "x=10", "y=20");
        String text = plannedNode(a, "textNode");
        call(DreaminaCanvasCommand.NODE_CREATE_TEXT, project, "node-id=" + text, "title=SDK-E2E-text", "text=SDK isolated test");
        call(DreaminaCanvasCommand.NODE_EDIT_TEXT, project, "node-id=" + text, "text=SDK edit verified");
        String video = plannedNode(a, "videoNode");
        call(DreaminaCanvasCommand.NODE_CREATE_VIDEO, project, "node-id=" + video, "title=SDK-E2E-video-draft");
        call(DreaminaCanvasCommand.NODE_EDIT_VIDEO, project, "node-id=" + video, "title=SDK-E2E-video-draft-verified");
        String audio = plannedNode(a, "audioNode");
        call(DreaminaCanvasCommand.NODE_CREATE_AUDIO, project, "node-id=" + audio, "title=SDK-E2E-audio-draft");
        call(DreaminaCanvasCommand.NODE_EDIT_AUDIO, project, "node-id=" + audio, "title=SDK-E2E-audio-draft-verified");
        String element = plannedNode(a, "elementNode");
        call(DreaminaCanvasCommand.NODE_CREATE_ELEMENT, project, "node-id=" + element, "title=SDK-E2E-element", "main=" + imported);
        call(DreaminaCanvasCommand.NODE_EDIT_ELEMENT, project, "node-id=" + element, "description=SDK verified", "clear-voice=true");
        String timeline = plannedNode(a, "timelineNode");
        call(DreaminaCanvasCommand.NODE_CREATE_TIMELINE, project, "node-id=" + timeline, "title=SDK-E2E-timeline", "clip=" + imported + ",duration=1000");
        call(DreaminaCanvasCommand.NODE_EDIT_TIMELINE, project, "node-id=" + timeline, "clear-tracks=true");
        call(DreaminaCanvasCommand.NODE_SHOW, project, "node-id=" + imported, "node-id=" + text);
        call(DreaminaCanvasCommand.NODE_FIND, project, "name=SDK-E2E", "limit=50");
        String generation = plannedNode(a, "imageGenerationNode");
        call(DreaminaCanvasCommand.NODE_CREATE_IMAGE, project, "node-id=" + generation, "title=SDK-E2E-image-generation",
                "mode=t2i", "model=" + a.get("imageModel"), "prompt=A blue square on a plain white background", "resolution=1.5K", "ratio=1:1", "count=1");
        DreaminaCanvasResponse<?> quote = call(DreaminaCanvasCommand.NODE_QUOTE, project, "node-id=" + generation);
        mapper.writeValue(directory().resolve("image-generation-quote.json").toFile(), quote.getData());
        a.put("noFeeState", "complete");
        saveAssets(a);
    }

    @Test
    @EnabledIfSystemProperty(named = "dual.cli.live.canvasDeleteOwned", matches = "true")
    void deleteOnlyThisBatchDisposableTextNode() throws Exception {
        Map<String, String> a = mapper.readValue(directory().resolve("canvas-assets.json").toFile(),
                new com.fasterxml.jackson.core.type.TypeReference<Map<String, String>>() {});
        assertEquals("complete", a.get("noFeeState"));
        boolean resume = Boolean.getBoolean("dual.cli.live.resumeDelete");
        assertFalse("true".equals(a.get("disposableDeleted")), "completed delete batch must not replay");
        assertTrue(!a.containsKey("disposableNode") || resume, "existing delete identity requires explicit resume");
        String project = "project-id=" + a.get("projectId");
        String id = resume ? a.get("disposableNode") : plannedNode(a, "disposableNode");
        String update = resume ? a.get("deleteUpdateId") : UUID.randomUUID().toString();
        a.put("deleteUpdateId", update);
        saveAssets(a);
        if (!resume) {
            call(DreaminaCanvasCommand.NODE_CREATE_TEXT, project, "node-id=" + id, "title=SDK-E2E-delete-only", "text=disposable test node");
        } else {
            call(DreaminaCanvasCommand.NODE_SHOW, project, "node-id=" + id);
        }
        DreaminaCanvasResponse<?> approval = call(DreaminaCanvasCommand.NODE_DELETE_CONFIRM, project, "node-id=" + id, "update-id=" + update);
        String token = mapper.valueToTree(approval.getData()).path("deleteConfirmationToken").asText();
        assertFalse(token.isEmpty(), "deletion needs the exact issued token");
        call(DreaminaCanvasCommand.NODE_DELETE, project, "node-id=" + id, "update-id=" + update, "delete-token=" + token);
        call(DreaminaCanvasCommand.NODE_DELETE_STATUS, project, "node-id=" + id, "update-id=" + update);
        a.put("disposableDeleted", "true");
        saveAssets(a);
    }

    @Test
    @EnabledIfSystemProperty(named = "dual.cli.live.approvedImageBatch", matches = "true")
    void approvedImageBatchUsesOneSubmissionAndOriginalRecoveryIdentity() throws Exception {
        assertEquals("3", System.getProperty("dual.cli.live.approvedCreditCeiling"), "this batch is capped at the quoted three credits");
        Map<String, String> a = mapper.readValue(directory().resolve("canvas-assets.json").toFile(),
                new com.fasterxml.jackson.core.type.TypeReference<Map<String, String>>() {});
        assertEquals("complete", a.get("noFeeState"));
        assertFalse(a.containsKey("paidImageSubmitId"), "existing submit identity must be queried, never resubmitted");
        String project = "project-id=" + a.get("projectId");
        String node = "node-id=" + a.get("imageGenerationNode");
        String submit = UUID.randomUUID().toString();
        a.put("paidImageSubmitId", submit);
        a.put("paidImageState", "before_submit");
        saveAssets(a);
        DreaminaCanvasResponse<?> approval = call(DreaminaCanvasCommand.NODE_CONFIRM, project, node, "credit-ceiling=3");
        String token = mapper.valueToTree(approval.getData()).path("creditConfirmationToken").asText();
        assertFalse(token.isEmpty());
        a.put("paidImageState", "submitting");
        saveAssets(a);
        call(DreaminaCanvasCommand.NODE_RUN, project, node, "submit-id=" + submit, "credit-token=" + token);
        a.put("paidImageState", "accepted");
        saveAssets(a);
        call(DreaminaCanvasCommand.OPERATION_STATUS, project, "@" + submit);
        call(DreaminaCanvasCommand.OPERATION_WAIT, project, "@" + submit, "timeout=180s", "interval=3s");
        DreaminaCanvasResponse<?> shown = call(DreaminaCanvasCommand.NODE_SHOW, project, node);
        java.util.List<String> ids = mapper.valueToTree(shown.getData()).findValuesAsText("resourceId");
        assertFalse(ids.isEmpty(), "generated resource must exist");
        Path download = directory().resolve("generated-image");
        Files.createDirectories(download);
        for (String resource : new java.util.LinkedHashSet<>(ids)) {
            DreaminaCanvasResponse<?> fetched = call(DreaminaCanvasCommand.RESOURCE_GET, project, "@" + resource);
            assertEquals("success", mapper.valueToTree(fetched.getData()).path("status").asText());
            call(DreaminaCanvasCommand.RESOURCE_DOWNLOAD, project, "@" + resource, "output=" + download);
        }
        try (java.util.stream.Stream<Path> paths = Files.list(download)) {
            assertTrue(paths.anyMatch(file -> {
                try { return Files.isRegularFile(file) && Objects.nonNull(javax.imageio.ImageIO.read(file.toFile())); }
                catch (java.io.IOException failure) { return false; }
            }), "download must contain a decodable generated image");
        }
        a.put("paidImageState", "complete");
        saveAssets(a);
    }

    @Test
    @EnabledIfSystemProperty(named = "dual.cli.live.prepareMediaQuotes", matches = "true")
    void prepareVideoSampleAndShortTtsQuotesWithoutRun() throws Exception {
        Map<String, String> a = mapper.readValue(directory().resolve("canvas-assets.json").toFile(),
                new com.fasterxml.jackson.core.type.TypeReference<Map<String, String>>() {});
        assertEquals("complete", a.get("noFeeState"));
        assertFalse(a.containsKey("mediaQuotesPrepared"), "do not repeat a prepared quote batch");
        String project = "project-id=" + a.get("projectId");
        call(DreaminaCanvasCommand.NODE_EDIT_VIDEO, project, "node-id=" + a.get("videoNode"), "title=SDK-E2E-video-sample",
                "mode=t2v", "model=seedance_2.5_draft", "prompt=A blue square moves slowly across a plain white background",
                "ratio=16:9", "resolution=480p", "duration=4", "count=1");
        DreaminaCanvasResponse<?> voices = call(DreaminaCanvasCommand.VOICE_LIST, "model=legacy_tts", "count=1", "offset=0");
        JsonNode catalog = mapper.valueToTree(voices.getData()).path("models").path("legacy_tts").path("items");
        assertTrue(catalog.isArray() && catalog.size() > 0);
        String voice = catalog.get(0).path("voiceName").asText();
        assertFalse(voice.isEmpty());
        call(DreaminaCanvasCommand.NODE_EDIT_AUDIO, project, "node-id=" + a.get("audioNode"), "title=SDK-E2E-tts",
                "mode=tts", "model=legacy_tts", "voice-name=" + voice, "prompt=SDK验收通过。");
        DreaminaCanvasResponse<?> video = call(DreaminaCanvasCommand.NODE_QUOTE, project, "node-id=" + a.get("videoNode"));
        DreaminaCanvasResponse<?> audio = call(DreaminaCanvasCommand.NODE_QUOTE, project, "node-id=" + a.get("audioNode"));
        mapper.writeValue(directory().resolve("video-generation-quote.json").toFile(), video.getData());
        mapper.writeValue(directory().resolve("audio-generation-quote.json").toFile(), audio.getData());
        a.put("mediaQuotesPrepared", "true");
        saveAssets(a);
    }

    @Test
    @EnabledIfSystemProperty(named = "dual.cli.live.prepareTimelineQuote", matches = "true")
    void prepareDedicatedTimelineQuoteWithoutExportSubmission() throws Exception {
        Map<String, String> a = mapper.readValue(directory().resolve("canvas-assets.json").toFile(),
                new com.fasterxml.jackson.core.type.TypeReference<Map<String, String>>() {});
        assertFalse(a.containsKey("timelineQuotePrepared"), "reuse the saved quote rather than repeating preparation");
        String project = "project-id=" + a.get("projectId");
        call(DreaminaCanvasCommand.NODE_EDIT_TIMELINE, project, "node-id=" + a.get("timelineNode"),
                "clip=" + a.get("imageGenerationNode") + ",duration=3000",
                "audio-clip=" + a.get("audioNode") + ",start=0");
        DreaminaCanvasResponse<?> quote = call(DreaminaCanvasCommand.NODE_QUOTE, project, "node-id=" + a.get("timelineNode"));
        mapper.writeValue(directory().resolve("timeline-export-quote.json").toFile(), quote.getData());
        a.put("timelineQuotePrepared", "true");
        saveAssets(a);
    }

    @Test
    @EnabledIfSystemProperty(named = "dual.cli.live.approvedMediaBatch", matches = "true")
    void approvedMediaBatchIsCappedPerNodeAndNeverReplaysSubmissions() throws Exception {
        assertEquals("37", System.getProperty("dual.cli.live.approvedMediaCeiling"));
        for (String kind : new String[]{"Audio", "Video"}) {
            Map<String, String> a = mapper.readValue(directory().resolve("canvas-assets.json").toFile(),
                    new com.fasterxml.jackson.core.type.TypeReference<Map<String, String>>() {});
            assertEquals("true", a.get("mediaQuotesPrepared"));
            String project = "project-id=" + a.get("projectId");
            String node = "node-id=" + a.get(kind.toLowerCase(java.util.Locale.ROOT) + "Node");
            String key = "paid" + kind + "SubmitId";
            String submit = a.get(key);
            if (Objects.isNull(submit)) {
                submit = UUID.randomUUID().toString();
                a.put(key, submit);
                a.put("paid" + kind + "State", "before_submit");
                saveAssets(a);
                int ceiling = "Audio".equals(kind) ? 1 : 36;
                DreaminaCanvasResponse<?> approval = call(DreaminaCanvasCommand.NODE_CONFIRM, project, node, "credit-ceiling=" + ceiling);
                String token = mapper.valueToTree(approval.getData()).path("creditConfirmationToken").asText();
                assertFalse(token.isEmpty());
                a.put("paid" + kind + "State", "submitting");
                saveAssets(a);
                call(DreaminaCanvasCommand.NODE_RUN, project, node, "submit-id=" + submit, "credit-token=" + token);
                a.put("paid" + kind + "State", "accepted");
                saveAssets(a);
            } else {
                assertNotEquals("before_submit", a.get("paid" + kind + "State"), "inspect failed approval explicitly; do not resubmit");
            }
            call(DreaminaCanvasCommand.OPERATION_STATUS, project, "@" + submit);
            call(DreaminaCanvasCommand.OPERATION_WAIT, project, "@" + submit, "timeout=180s", "interval=3s");
            DreaminaCanvasResponse<?> shown = call(DreaminaCanvasCommand.NODE_SHOW, project, node);
            java.util.List<String> ids = mapper.valueToTree(shown.getData()).findValuesAsText("resourceId");
            assertFalse(ids.isEmpty());
            Path download = directory().resolve("generated-" + kind.toLowerCase(java.util.Locale.ROOT));
            Files.createDirectories(download);
            for (String resource : new java.util.LinkedHashSet<>(ids)) {
                DreaminaCanvasResponse<?> fetched = call(DreaminaCanvasCommand.RESOURCE_GET, project, "@" + resource);
                JsonNode data = mapper.valueToTree(fetched.getData());
                assertEquals("success", data.path("status").asText());
                if (data.hasNonNull("creditsAmount")) {
                    assertTrue(data.path("creditsAmount").asLong() <= ("Audio".equals(kind) ? 1 : 36));
                    a.put("paid" + kind + "ActualCredits", data.path("creditsAmount").asText());
                }
                call(DreaminaCanvasCommand.RESOURCE_DOWNLOAD, project, "@" + resource, "output=" + download);
                a.put("generated" + kind + "Resource", resource);
            }
            a.put("paid" + kind + "State", "complete");
            saveAssets(a);
        }
    }

    @Test
    @EnabledIfSystemProperty(named = "dual.cli.live.approvedZeroImageProbes", matches = "true")
    void approvedZeroCeilingImagePostprocessingProbes() throws Exception {
        for (DreaminaCanvasCommand command : new DreaminaCanvasCommand[]{DreaminaCanvasCommand.NODE_UPSCALE_IMAGE, DreaminaCanvasCommand.NODE_CUTOUT_IMAGE}) {
            Map<String, String> a = mapper.readValue(directory().resolve("canvas-assets.json").toFile(),
                    new com.fasterxml.jackson.core.type.TypeReference<Map<String, String>>() {});
            assertEquals("complete", a.get("paidImageState"));
            String name = command == DreaminaCanvasCommand.NODE_UPSCALE_IMAGE ? "imageUpscale" : "imageCutout";
            String submit = a.get(name + "SubmitId");
            if (Objects.nonNull(submit)) {
                assertTrue(Boolean.getBoolean("dual.cli.live.resumeZeroProbe") && "imageUpscale".equals(name));
                JsonNode old = mapper.readTree(directory().resolve(name + "-quote.json").toFile());
                assertEquals("cli.image_upscale_resolution_required", old.path("errorCode").asText(), "only the pre-submit argument failure may be corrected");
            } else {
                submit = UUID.randomUUID().toString();
            }
            a.put(name + "SubmitId", submit);
            a.put(name + "State", "probing-zero-ceiling");
            saveAssets(a);
            java.util.List<String> args = new java.util.ArrayList<>(java.util.Arrays.asList(
                    "project-id=" + a.get("projectId"), "node-id=" + a.get("imageGenerationNode"), "submit-id=" + submit, "credit-ceiling=0"));
            if (command == DreaminaCanvasCommand.NODE_UPSCALE_IMAGE) {
                args.add("mode=normal");
            }
            DreaminaCanvasResponse<?> response = cli.execute(DreaminaCanvasFixtures.request(command, args.toArray(new String[0])));
            Map<String, Object> receipt = new LinkedHashMap<>();
            receipt.put("cli", "dreamina-canvas");
            receipt.put("command", command.getPath());
            receipt.put("success", response.isSuccess());
            receipt.put("exitCode", response.getExitCode());
            receipt.put("approvedCeiling", 0);
            receipt.put("creditConfirmation", response.getCreditConfirmation());
            receipt.put("errorCode", Objects.isNull(response.getError()) ? null : response.getError().getCode());
            receipt.put("requiredAction", Objects.isNull(response.getError()) ? null : response.getError().getRequiredAction());
            mapper.writeValue(directory().resolve(name + "-quote.json").toFile(), receipt);
            Files.write(directory().resolve("canvas-live.jsonl"), (mapper.writeValueAsString(receipt) + "\n").getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            if (response.isSuccess()) {
                a.put(name + "State", "accepted-zero-ceiling");
                saveAssets(a);
                call(DreaminaCanvasCommand.OPERATION_WAIT, "project-id=" + a.get("projectId"), "@" + submit, "timeout=180s", "interval=3s");
                a.put(name + "State", "complete-zero-ceiling");
            } else {
                assertEquals(10, response.getExitCode().intValue(), "positive-price operation must stop at confirmation");
                assertNotNull(response.getCreditConfirmation());
                a.put(name + "State", "awaiting-credit-approval");
            }
            saveAssets(a);
        }
    }

    @Test
    @EnabledIfSystemProperty(named = "dual.cli.live.approvedCutout", matches = "true")
    void approvedOneCreditCutoutResumesOriginalIdentityAndProducesTransparency() throws Exception {
        assertEquals("1", System.getProperty("dual.cli.live.cutoutCeiling"));
        Map<String, String> a = mapper.readValue(directory().resolve("canvas-assets.json").toFile(),
                new com.fasterxml.jackson.core.type.TypeReference<Map<String, String>>() {});
        assertEquals("awaiting-credit-approval", a.get("imageCutoutState"));
        String project = "project-id=" + a.get("projectId");
        String submit = a.get("imageCutoutSubmitId");
        a.put("imageCutoutState", "submitting-approved-one-credit");
        saveAssets(a);
        DreaminaCanvasResponse<?> submitted = call(DreaminaCanvasCommand.NODE_CUTOUT_IMAGE, project,
                "node-id=" + a.get("imageGenerationNode"), "submit-id=" + submit, "credit-ceiling=1");
        String outputNode = mapper.valueToTree(submitted.getData()).path("items").get(0).path("nodeId").asText();
        assertFalse(outputNode.isEmpty());
        assertNotEquals(a.get("imageGenerationNode"), outputNode);
        a.put("cutoutOutputNode", outputNode);
        a.put("imageCutoutState", "accepted");
        saveAssets(a);
        DreaminaCanvasResponse<?> once = cli.execute(DreaminaCanvasFixtures.request(DreaminaCanvasCommand.OPERATION_WAIT,
                project, "@" + submit, "timeout=0s", "interval=1s"));
        assertTrue(once.getExitCode() == 0 || once.getExitCode() == 20);
        Map<String, Object> recovery = new LinkedHashMap<>();
        recovery.put("shortWaitExitCode", once.getExitCode());
        recovery.put("sameSubmitIdentity", true);
        recovery.put("requiredAction", Objects.isNull(once.getError()) ? null : once.getError().getRequiredAction());
        mapper.writeValue(directory().resolve("cutout-short-wait-recovery.json").toFile(), recovery);
        call(DreaminaCanvasCommand.OPERATION_WAIT, project, "@" + submit, "timeout=180s", "interval=2s");
        DreaminaCanvasResponse<?> shown = call(DreaminaCanvasCommand.NODE_SHOW, project, "node-id=" + outputNode);
        java.util.List<String> resources = mapper.valueToTree(shown.getData()).findValuesAsText("resourceId");
        assertFalse(resources.isEmpty());
        Path output = directory().resolve("cutout-image");
        Files.createDirectories(output);
        for (String resource : new java.util.LinkedHashSet<>(resources)) {
            DreaminaCanvasResponse<?> got = call(DreaminaCanvasCommand.RESOURCE_GET, project, "@" + resource);
            assertEquals("success", mapper.valueToTree(got.getData()).path("status").asText());
            call(DreaminaCanvasCommand.RESOURCE_DOWNLOAD, project, "@" + resource, "output=" + output);
            a.put("cutoutResource", resource);
        }
        boolean transparent = false;
        boolean visible = false;
        try (java.util.stream.Stream<Path> files = Files.list(output)) {
            for (Path file : (Iterable<Path>) files::iterator) {
                java.awt.image.BufferedImage image = javax.imageio.ImageIO.read(file.toFile());
                assertNotNull(image);
                assertTrue(image.getColorModel().hasAlpha());
                for (int y = 0; y < image.getHeight(); y++) {
                    for (int x = 0; x < image.getWidth(); x++) {
                        int alpha = image.getRGB(x, y) >>> 24;
                        transparent |= alpha < 255;
                        visible |= alpha > 0;
                    }
                }
            }
        }
        assertTrue(transparent && visible, "cutout must have both transparency and nonempty foreground");
        a.put("imageCutoutState", "complete-one-credit");
        saveAssets(a);
    }
    @Test
    @EnabledIfSystemProperty(named = "dual.cli.live.approvedZeroVideoProbes", matches = "true")
    void approvedThreeVideoProbesNeverExceedZeroCeiling() throws Exception {
        DreaminaCanvasCommand[] commands = {DreaminaCanvasCommand.NODE_PROMOTE_VIDEO_SAMPLE,
                DreaminaCanvasCommand.NODE_UPSCALE_VIDEO, DreaminaCanvasCommand.NODE_INTERPOLATE_VIDEO};
        String[] names = {"videoPromote", "videoUpscale", "videoInterpolate"};
        String[] targets = {"resolution=1080p", "resolution=2K", "fps=60"};
        for (int i = 0; i < commands.length; i++) {
            Map<String, String> a = mapper.readValue(directory().resolve("canvas-assets.json").toFile(),
                    new com.fasterxml.jackson.core.type.TypeReference<Map<String, String>>() {});
            assertEquals("complete", a.get("paidVideoState"));
            assertFalse(a.containsKey(names[i] + "SubmitId"), "resume original operation, never probe a second submission");
            String submit = UUID.randomUUID().toString();
            a.put(names[i] + "SubmitId", submit);
            a.put(names[i] + "State", "probing-zero-ceiling");
            saveAssets(a);
            DreaminaCanvasResponse<?> response = cli.execute(DreaminaCanvasFixtures.request(commands[i],
                    "project-id=" + a.get("projectId"), "node-id=" + a.get("videoNode"), "submit-id=" + submit,
                    "credit-ceiling=0", targets[i]));
            Map<String, Object> receipt = new LinkedHashMap<>();
            receipt.put("cli", "dreamina-canvas");
            receipt.put("command", commands[i].getPath());
            receipt.put("success", response.isSuccess());
            receipt.put("exitCode", response.getExitCode());
            receipt.put("approvedCeiling", 0);
            receipt.put("creditConfirmation", response.getCreditConfirmation());
            receipt.put("error", response.getError());
            receipt.put("errorCode", Objects.isNull(response.getError()) ? null : response.getError().getCode());
            mapper.writeValue(directory().resolve(names[i] + "-quote.json").toFile(), receipt);
            Files.write(directory().resolve("canvas-live.jsonl"), (mapper.writeValueAsString(receipt) + "\n").getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            if (response.isSuccess()) {
                a.put(names[i] + "State", "accepted-zero-ceiling");
                saveAssets(a);
                call(DreaminaCanvasCommand.OPERATION_WAIT, "project-id=" + a.get("projectId"), "@" + submit, "timeout=180s", "interval=3s");
                a.put(names[i] + "State", "complete-zero-ceiling");
            } else if (response.getExitCode().intValue() == 10) {
                assertNotNull(response.getCreditConfirmation());
                a.put(names[i] + "State", "awaiting-credit-approval");
            } else {
                assertNotNull(response.getError(), "service restriction must remain structured");
                a.put(names[i] + "State", "blocked-" + response.getError().getCode());
            }
            saveAssets(a);
        }
    }

    @Test
    @EnabledIfSystemProperty(named = "dual.cli.live.approvedPaidVideoPost", matches = "true")
    void approvedVideoPromotionAndUpscaleReuseOriginalSubmissions() throws Exception {
        assertEquals("280", System.getProperty("dual.cli.live.videoPostCeiling"));
        DreaminaCanvasCommand[] commands = {DreaminaCanvasCommand.NODE_PROMOTE_VIDEO_SAMPLE, DreaminaCanvasCommand.NODE_UPSCALE_VIDEO};
        String[] names = {"videoPromote", "videoUpscale"};
        int[] ceilings = {256, 24};
        String[] targets = {"resolution=1080p", "resolution=2K"};
        for (int i = 0; i < commands.length; i++) {
            Map<String, String> a = mapper.readValue(directory().resolve("canvas-assets.json").toFile(),
                    new com.fasterxml.jackson.core.type.TypeReference<Map<String, String>>() {});
            assertEquals("awaiting-credit-approval", a.get(names[i] + "State"));
            String submit = Objects.requireNonNull(a.get(names[i] + "SubmitId"));
            a.put(names[i] + "State", "approved-resuming-original");
            a.put(names[i] + "ApprovedCeiling", Integer.toString(ceilings[i]));
            saveAssets(a);
            DreaminaCanvasResponse<?> accepted = call(commands[i], "project-id=" + a.get("projectId"),
                    "node-id=" + a.get("videoNode"), "submit-id=" + submit, "credit-ceiling=" + ceilings[i], targets[i]);
            mapper.writeValue(directory().resolve(names[i] + "-accepted.json").toFile(), accepted.getData());
            a.put(names[i] + "State", "accepted");
            saveAssets(a);
            DreaminaCanvasResponse<?> done = call(DreaminaCanvasCommand.OPERATION_WAIT,
                    "project-id=" + a.get("projectId"), "@" + submit, "timeout=180s", "interval=3s");
            mapper.writeValue(directory().resolve(names[i] + "-completed.json").toFile(), done.getData());
            JsonNode resources = mapper.valueToTree(done.getData()).path("resources");
            assertTrue(resources.isArray() && resources.size() > 0);
            String resource = resources.get(0).path("resourceId").asText();
            assertFalse(resource.isEmpty());
            a.put(names[i] + "Resource", resource);
            DreaminaCanvasResponse<?> meta = call(DreaminaCanvasCommand.RESOURCE_GET, "project-id=" + a.get("projectId"), "@" + resource);
            mapper.writeValue(directory().resolve(names[i] + "-resource.json").toFile(), meta.getData());
            Path output = directory().resolve(names[i] + "-download");
            Files.createDirectories(output);
            call(DreaminaCanvasCommand.RESOURCE_DOWNLOAD, "project-id=" + a.get("projectId"), "@" + resource, "output=" + output);
            a.put(names[i] + "State", "complete");
            saveAssets(a);
        }
    }

}
