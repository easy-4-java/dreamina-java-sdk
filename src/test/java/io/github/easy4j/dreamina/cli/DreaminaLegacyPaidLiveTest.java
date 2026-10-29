package io.github.easy4j.dreamina.cli;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.easy4j.dreamina.DreaminaCliProperties;
import io.github.easy4j.dreamina.cli.model.DreaminaGenerateSubmit;
import io.github.easy4j.dreamina.cli.model.DreaminaQueryResult;
import io.github.easy4j.dreamina.cli.opts.DreaminaImageModelVersion;
import io.github.easy4j.dreamina.cli.opts.DreaminaImageResolutionType;
import io.github.easy4j.dreamina.cli.opts.DreaminaRatio;
import io.github.easy4j.dreamina.cli.opts.DreaminaText2ImageRequest;
import io.github.easy4j.dreamina.cli.opts.DreaminaImage2ImageRequest;
import io.github.easy4j.dreamina.cli.opts.DreaminaText2VideoRequest;
import io.github.easy4j.dreamina.cli.opts.DreaminaImage2VideoRequest;
import io.github.easy4j.dreamina.cli.opts.DreaminaFrames2VideoRequest;
import io.github.easy4j.dreamina.cli.opts.DreaminaMultimodal2VideoRequest;
import io.github.easy4j.dreamina.cli.opts.DreaminaVideoModelVersion;
import io.github.easy4j.dreamina.cli.opts.DreaminaVideoResolutionType;
import io.github.easy4j.dreamina.cli.opts.DreaminaMultiframe2VideoRequest;
import io.github.easy4j.dreamina.cli.opts.DreaminaImageUpscaleRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/** 旧 CLI 无服务端额度闸门；必须单独批准具体任务后才启用，不自动重提。 */
class DreaminaLegacyPaidLiveTest {
    private final ObjectMapper mapper = new ObjectMapper();
    private final DreaminaCliExecutor cli = new DreaminaCliExecutor(new DreaminaCliProperties());
    private Path directory() { return Paths.get(System.getProperty("dual.cli.evidenceDir")); }
    @Test
    @EnabledIfSystemProperty(named = "dual.cli.live.approvedLegacyTextImageNoHardCap", matches = "true")
    void exactlyOneApprovedTextImageThenQueryOriginalTask() throws Exception {
        assertEquals("8", System.getProperty("dual.cli.live.legacyTextImageExpectedCredits"));
        Path ledger = directory().resolve("legacy-text-image.json");
        assertFalse(Files.exists(ledger), "unknown or accepted submission must only be queried, never submitted again");
        String session = mapper.readTree(directory().resolve("legacy-sessions.json").toFile()).path("generationSession").asText();
        assertFalse(session.isEmpty());
        Map<String, Object> saved = new LinkedHashMap<>();
        saved.put("state", "submitting-once"); saved.put("expectedCredits", 8); saved.put("serverHardCapAvailable", false);
        saved.put("balanceBefore", cli.userCreditInfo().getBody().getTotalCredit());
        mapper.writeValue(ledger.toFile(), saved);
        DreaminaCliResponse<DreaminaGenerateSubmit> accepted = cli.text2ImageSubmit(DreaminaText2ImageRequest.builder()
                .prompt("A blue square on a plain white background")
                .modelVersion(DreaminaImageModelVersion.MODEL_5_0_PRO).resolutionType(DreaminaImageResolutionType.RESOLUTION_2K)
                .ratio(DreaminaRatio.RATIO_1_1).generateNum(1).sessionId(Long.valueOf(session)).pollSeconds(0).build());
        String submit = Objects.requireNonNull(accepted.getBody().getSubmitId(), "preserve submit identity before querying");
        saved.put("submitId", submit); saved.put("state", "accepted");
        saved.put("submitCreditCount", accepted.getBody().getCreditCount());
        mapper.writeValue(ledger.toFile(), saved);
        DreaminaQueryResult result = null;
        for (int attempt = 0; attempt < 60; attempt++) {
            DreaminaCliResponse<DreaminaQueryResult> queried = cli.queryResultInfo(submit);
            assertTrue(queried.isSuccess());
            result = queried.getBody();
            assertNotNull(result);
            if (result.isTerminal()) { break; }
            Thread.sleep(3000L);
        }
        assertNotNull(result);
        assertTrue(result.isGenSuccess(), "accepted task must reach actual success; do not resubmit on timeout");
        String url = Objects.requireNonNull(result.firstImageUrl());
        Path output = directory().resolve("legacy-text-image-output.bin");
        try (InputStream stream = new URL(url).openStream()) { Files.copy(stream, output); }
        java.awt.image.BufferedImage image = javax.imageio.ImageIO.read(output.toFile());
        assertNotNull(image); assertTrue(image.getWidth() > 0 && image.getHeight() > 0);
        saved.put("state", "complete"); saved.put("queryCreditCount", result.getCreditCount());
        saved.put("balanceAfter", cli.userCreditInfo().getBody().getTotalCredit());
        saved.put("width", image.getWidth()); saved.put("height", image.getHeight());
        mapper.writeValue(ledger.toFile(), saved);
        record("text2image", submit); record("query_result", submit);
        DreaminaCliResponse<?> listed = cli.listTaskInfo();
        assertTrue(listed.isSuccess());
        record("list_task", submit);
    }

    @Test
    @EnabledIfSystemProperty(named = "dual.cli.live.approvedLegacyImageAndFourVideos", matches = "true")
    void approvedOneImageAndFourMiniVideosAreSubmittedOncePerCommand() throws Exception {
        assertEquals("104", System.getProperty("dual.cli.live.legacyImageAndVideoExpectedCredits"));
        String source = directory().resolve("sdk-test-pattern.png").toString();
        String last = directory().resolve("generated-image/dreamina-ef17e510-4435-4575-b469-fe007c7ee068.png").toString();
        long session = mapper.readTree(directory().resolve("legacy-sessions.json").toFile()).path("generationSession").asLong();
        assertTrue(session > 0);
        submitAndComplete("image2image", 8, () -> cli.image2ImageSubmit(DreaminaImage2ImageRequest.builder()
                .image(source).prompt("Keep the blue square and white background, add a tiny blue dot")
                .modelVersion(DreaminaImageModelVersion.MODEL_5_0_PRO).resolutionType(DreaminaImageResolutionType.RESOLUTION_2K)
                .ratio(DreaminaRatio.RATIO_1_1).generateNum(1).sessionId(session).pollSeconds(0).build()));
        submitAndComplete("text2video", 24, () -> cli.text2VideoSubmit(DreaminaText2VideoRequest.builder()
                .prompt("A blue square moves slowly across a plain white background")
                .modelVersion(DreaminaVideoModelVersion.SEEDANCE_2_0_MINI).videoResolution(DreaminaVideoResolutionType.RESOLUTION_720P)
                .ratio(DreaminaRatio.RATIO_16_9).durationSeconds(4).sessionId(session).pollSeconds(0).build()));
        submitAndComplete("image2video", 24, () -> cli.image2VideoSubmit(DreaminaImage2VideoRequest.builder()
                .imagePath(source).prompt("The blue square moves slowly")
                .modelVersion(DreaminaVideoModelVersion.SEEDANCE_2_0_MINI).videoResolution(DreaminaVideoResolutionType.RESOLUTION_720P)
                .ratio(DreaminaRatio.RATIO_16_9).durationSeconds(4).sessionId(session).pollSeconds(0).build()));
        submitAndComplete("frames2video", 24, () -> cli.frames2VideoSubmit(DreaminaFrames2VideoRequest.builder()
                .firstImagePath(source).lastImagePath(last).prompt("Smooth transition between the blue square images")
                .modelVersion(DreaminaVideoModelVersion.SEEDANCE_2_0_MINI).videoResolution(DreaminaVideoResolutionType.RESOLUTION_720P)
                .ratio(DreaminaRatio.RATIO_16_9).durationSeconds(4).sessionId(session).pollSeconds(0).build()));
        submitAndComplete("multimodal2video", 24, () -> cli.multimodal2VideoSubmit(DreaminaMultimodal2VideoRequest.builder()
                .image(source).prompt("The blue square moves slowly on a white background")
                .modelVersion(DreaminaVideoModelVersion.SEEDANCE_2_0_MINI).videoResolution(DreaminaVideoResolutionType.RESOLUTION_720P)
                .ratio(DreaminaRatio.RATIO_16_9).durationSeconds(4).sessionId(session).pollSeconds(0).build()));
    }


    @Test
    @EnabledIfSystemProperty(named = "dual.cli.live.approvedLegacyFinalTwo", matches = "true")
    void approvedMultiframeAndUpscaleUseOfficialWebPriceBasis() throws Exception {
        assertEquals("11", System.getProperty("dual.cli.live.legacyFinalTwoExpectedCredits"));
        String source = directory().resolve("sdk-test-pattern.png").toString();
        String last = directory().resolve("generated-image/dreamina-ef17e510-4435-4575-b469-fe007c7ee068.png").toString();
        long session = mapper.readTree(directory().resolve("legacy-sessions.json").toFile()).path("generationSession").asLong();
        assertTrue(session > 0);
        submitAndComplete("multiframe2video", 10, () -> cli.multiframe2VideoSubmit(DreaminaMultiframe2VideoRequest.builder()
                .image(source).image(last).prompt("Smooth transition between the blue square images").durationSeconds(5D)
                .videoResolution(DreaminaVideoResolutionType.RESOLUTION_720P).sessionId(session).pollSeconds(0).build()));
        submitAndComplete("image_upscale", 1, () -> cli.imageUpscaleSubmit(DreaminaImageUpscaleRequest.builder()
                .imagePath(source).resolutionType(DreaminaImageResolutionType.RESOLUTION_2K).sessionId(session).pollSeconds(0).build()));
    }

    private void submitAndComplete(String command, int expectedCredits,
            java.util.function.Supplier<DreaminaCliResponse<DreaminaGenerateSubmit>> submitAction) throws Exception {
        Path ledger = directory().resolve("legacy-" + command + ".json");
        assertFalse(Files.exists(ledger), "existing task must be queried, not submitted again: " + command);
        Map<String, Object> saved = new LinkedHashMap<>();
        saved.put("state", "submitting-once"); saved.put("expectedCredits", expectedCredits);
        saved.put("balanceBefore", cli.userCreditInfo().getBody().getTotalCredit());
        mapper.writeValue(ledger.toFile(), saved);
        DreaminaCliResponse<DreaminaGenerateSubmit> accepted = submitAction.get();
        String submit = Objects.requireNonNull(accepted.getBody().getSubmitId());
        saved.put("submitId", submit); saved.put("state", "accepted");
        saved.put("submitCreditCount", accepted.getBody().getCreditCount());
        mapper.writeValue(ledger.toFile(), saved);
        System.out.println("LEGACY_ACCEPTED " + command);
        DreaminaQueryResult result = null;
        for (int attempt = 0; attempt < 120; attempt++) {
            result = cli.queryResultInfo(submit).getBody();
            assertNotNull(result);
            if (result.isTerminal()) { break; }
            Thread.sleep(5000L);
        }
        assertNotNull(result);
        saved.put("genStatus", result.getGenStatus());
        saved.put("failReason", result.getFailReason());
        mapper.writeValue(ledger.toFile(), saved);
        assertTrue(result.isGenSuccess(), "accepted task must only be queried on failure: " + command);
        String url = Objects.nonNull(result.firstImageUrl()) ? result.firstImageUrl() : result.firstVideoUrl();
        assertNotNull(url);
        Path output = directory().resolve("legacy-" + command + "-output.bin");
        try (InputStream stream = new URL(url).openStream()) { Files.copy(stream, output); }
        assertTrue(Files.size(output) > 0);
        saved.put("queryCreditCount", result.getCreditCount());
        saved.put("balanceAfter", cli.userCreditInfo().getBody().getTotalCredit());
        saved.put("state", "complete");
        mapper.writeValue(ledger.toFile(), saved);
        record(command, submit); record("query_result", submit);
        assertEquals(Long.valueOf(expectedCredits), result.getCreditCount(), "fee differs; stop remaining paid tasks");
        System.out.println("LEGACY_COMPLETED " + command + " credits=" + result.getCreditCount());
    }
    private void record(String command, String submit) throws Exception {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("cli", "dreamina"); row.put("command", command); row.put("success", true); row.put("exitCode", 0);
        row.put("submitId", submit); row.put("phase", "completed-and-decoded");
        Files.write(directory().resolve("legacy-live.jsonl"), (mapper.writeValueAsString(row) + "\n").getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }
}
