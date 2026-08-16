package io.github.easy4j.dreamina.cli.model;

import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DreaminaModelClassesTest {

    private final ObjectMapper om = JsonMapper.builder().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).build();

    // --- DreaminaVersion ---
    @Test
    void shouldDeserializeVersion() throws Exception {
        String json = "{\"version\":\"1.4.14\",\"commit\":\"abc123\",\"build_time\":\"2026-07-21\"}";
        DreaminaVersion v = om.readValue(json, DreaminaVersion.class);
        assertEquals("1.4.14", v.getVersion());
        assertEquals("abc123", v.getCommit());
        assertEquals("2026-07-21", v.getBuildTime());
    }

    // --- DreaminaUserCredit ---
    @Test
    void shouldDeserializeUserCredit() throws Exception {
        String json = "{\"total_credit\":4391,\"user_id\":1552973852847448,\"user_name\":\"test\",\"vip_level\":\"maestro\"}";
        DreaminaUserCredit c = om.readValue(json, DreaminaUserCredit.class);
        assertEquals(4391L, c.getTotalCredit());
        assertEquals(1552973852847448L, c.getUserId());
        assertEquals("test", c.getUserName());
        assertEquals("maestro", c.getVipLevel());
    }

    // --- DreaminaCheckLogin ---
    @Test
    void shouldDeserializeCheckLogin() throws Exception {
        String json = "{\"gen_status\":\"success\",\"message\":\"ok\"}";
        DreaminaCheckLogin cl = om.readValue(json, DreaminaCheckLogin.class);
        assertEquals("success", cl.getGenStatus());
        assertEquals("ok", cl.getMessage());
    }

    // --- DreaminaGenerateSubmit ---
    @Test
    void shouldDeserializeGenerateSubmit() throws Exception {
        String json = "{\"submit_id\":\"abc-123\",\"gen_status\":\"querying\",\"fail_reason\":null,\"logid\":\"log1\",\"credit_count\":10}";
        DreaminaGenerateSubmit gs = om.readValue(json, DreaminaGenerateSubmit.class);
        assertEquals("abc-123", gs.getSubmitId());
        assertEquals("querying", gs.getGenStatus());
        assertEquals("log1", gs.getLogid());
        assertEquals(10L, gs.getCreditCount());
        assertTrue(gs.isGenQuerying());
        assertFalse(gs.isGenSuccess());
        assertFalse(gs.isGenFailed());
        assertFalse(gs.isTerminal());
    }

    @Test
    void shouldReturnCorrectTerminalStateForSubmit() throws Exception {
        DreaminaGenerateSubmit gs = new DreaminaGenerateSubmit();
        gs.setGenStatus("success");
        assertTrue(gs.isGenSuccess());
        assertTrue(gs.isTerminal());

        gs.setGenStatus("fail");
        assertTrue(gs.isGenFailed());
        assertTrue(gs.isTerminal());
    }

    @Test
    void shouldReturnUnknownStatusForNullGenStatus() {
        DreaminaGenerateSubmit gs = new DreaminaGenerateSubmit();
        assertEquals(DreaminaGenerationStatus.UNKNOWN, gs.generationStatus());
    }

    // --- DreaminaQueryResult ---
    @Test
    void shouldDeserializeQueryResult() throws Exception {
        String json = "{\"submit_id\":\"s1\",\"gen_status\":\"success\",\"prompt\":\"a cat\",\"logid\":\"l1\",\"credit_count\":5}";
        DreaminaQueryResult qr = om.readValue(json, DreaminaQueryResult.class);
        assertEquals("s1", qr.getSubmitId());
        assertEquals("a cat", qr.getPrompt());
        assertTrue(qr.isGenSuccess());
        assertTrue(qr.isTerminal());
    }

    @Test
    void shouldReturnEmptyListsForNullResultJson() {
        DreaminaQueryResult qr = new DreaminaQueryResult();
        assertTrue(qr.images().isEmpty());
        assertTrue(qr.videos().isEmpty());
        assertNull(qr.firstImageUrl());
        assertNull(qr.firstVideoUrl());
    }

    @Test
    void shouldReturnFirstImageUrl() {
        DreaminaQueryResult qr = new DreaminaQueryResult();
        DreaminaResultJson rj = new DreaminaResultJson();
        DreaminaQueryImage img = new DreaminaQueryImage();
        img.setImageUrl("https://example.com/img.png");
        rj.setImages(List.of(img));
        qr.setResultJson(rj);
        assertEquals("https://example.com/img.png", qr.firstImageUrl());
    }

    @Test
    void shouldReturnFirstVideoUrl() {
        DreaminaQueryResult qr = new DreaminaQueryResult();
        DreaminaResultJson rj = new DreaminaResultJson();
        DreaminaQueryVideo vid = new DreaminaQueryVideo();
        vid.setVideoUrl("https://example.com/vid.mp4");
        rj.setVideos(List.of(vid));
        qr.setResultJson(rj);
        assertEquals("https://example.com/vid.mp4", qr.firstVideoUrl());
    }

    @Test
    void shouldDetectQueueFinished() {
        DreaminaQueryResult qr = new DreaminaQueryResult();
        assertFalse(qr.isQueueFinished());
        DreaminaQueryQueueInfo qi = new DreaminaQueryQueueInfo();
        qr.setQueueInfo(qi);
        assertFalse(qr.isQueueFinished());
        qi.setQueueStatus("Finish");
        assertTrue(qr.isQueueFinished());
    }

    // --- DreaminaResultJson ---
    @Test
    void shouldReturnSafeEmptyLists() {
        DreaminaResultJson rj = new DreaminaResultJson();
        assertTrue(rj.safeImages().isEmpty());
        assertTrue(rj.safeVideos().isEmpty());
    }

    // --- DreaminaCommerceInfo ---
    @Test
    void shouldReturnSafeEmptyTriplets() {
        DreaminaCommerceInfo ci = new DreaminaCommerceInfo();
        assertTrue(ci.safeTriplets().isEmpty());
    }

    @Test
    void shouldDeserializeCommerceInfo() throws Exception {
        String json = "{\"credit_count\":100,\"triplets\":[{\"resource_type\":\"rt\",\"resource_id\":\"ri\",\"benefit_type\":\"bt\"}]}";
        DreaminaCommerceInfo ci = om.readValue(json, DreaminaCommerceInfo.class);
        assertEquals(100L, ci.getCreditCount());
        assertEquals(1, ci.safeTriplets().size());
    }

    // --- DreaminaTaskItem ---
    @Test
    void shouldResolveCreditFromCommerceInfo() {
        DreaminaTaskItem item = new DreaminaTaskItem();
        assertNull(item.resolveCreditCount());

        item.setCreditCount(50L);
        assertEquals(50L, item.resolveCreditCount());

        DreaminaCommerceInfo ci = new DreaminaCommerceInfo();
        ci.setCreditCount(100L);
        item.setCommerceInfo(ci);
        assertEquals(100L, item.resolveCreditCount());
    }

    @Test
    void shouldDeserializeTaskItem() throws Exception {
        String json = "{\"submit_id\":\"s1\",\"gen_status\":\"success\",\"gen_task_type\":\"text2image\",\"prompt\":\"cat\"}";
        DreaminaTaskItem item = om.readValue(json, DreaminaTaskItem.class);
        assertEquals("s1", item.getSubmitId());
        assertTrue(item.isGenSuccess());
        assertTrue(item.isTerminal());
    }

    // --- DreaminaQueryImage ---
    @Test
    void shouldDeserializeQueryImage() throws Exception {
        String json = "{\"image_url\":\"https://img\",\"width\":512,\"height\":512}";
        DreaminaQueryImage img = om.readValue(json, DreaminaQueryImage.class);
        assertEquals("https://img", img.getImageUrl());
        assertEquals(512, img.getWidth());
        assertEquals(512, img.getHeight());
    }

    // --- DreaminaQueryVideo ---
    @Test
    void shouldDeserializeQueryVideo() throws Exception {
        String json = "{\"video_url\":\"https://vid\",\"cover_url\":\"https://cover\",\"width\":1280,\"height\":720,\"fps\":24,\"format\":\"mp4\",\"duration\":3.208}";
        DreaminaQueryVideo vid = om.readValue(json, DreaminaQueryVideo.class);
        assertEquals("https://vid", vid.getVideoUrl());
        assertEquals("https://cover", vid.getCoverUrl());
        assertEquals(1280, vid.getWidth());
        assertEquals(720, vid.getHeight());
        assertEquals(24, vid.getFps());
        assertEquals("mp4", vid.getFormat());
        assertEquals(3.208, vid.getDuration(), 0.001);
    }

    // --- DreaminaQueryQueueInfo ---
    @Test
    void shouldDeserializeQueueInfo() throws Exception {
        String json = "{\"queue_idx\":1,\"priority\":5,\"queue_status\":\"Waiting\",\"queue_length\":10,\"debug_info\":\"{}\"}";
        DreaminaQueryQueueInfo qi = om.readValue(json, DreaminaQueryQueueInfo.class);
        assertEquals(1, qi.getQueueIdx());
        assertEquals(5, qi.getPriority());
        assertEquals("Waiting", qi.getQueueStatus());
        assertEquals(10, qi.getQueueLength());
    }

    // --- DreaminaQueryQueueDebugInfo ---
    @Test
    void shouldDeserializeQueueDebugInfo() throws Exception {
        String json = "{\"have_no_dreamina_queue_name\":false,\"dreamina_matrix_queue_name\":\"q1\",\"queue_name\":\"q2\"}";
        DreaminaQueryQueueDebugInfo di = om.readValue(json, DreaminaQueryQueueDebugInfo.class);
        assertFalse(di.getHaveNoDreaminaQueueName());
        assertEquals("q1", di.getDreaminaMatrixQueueName());
        assertEquals("q2", di.getQueueName());
    }

    // --- DreaminaQueueInfoSupport ---
    @Test
    void shouldEnrichParsedDebugInfo() {
        DreaminaQueryQueueInfo qi = new DreaminaQueryQueueInfo();
        qi.setDebugInfo("{\"queue_name\":\"test-q\"}");
        DreaminaQueueInfoSupport.enrichParsedDebugInfo(om, qi);
        assertNotNull(qi.getParsedDebugInfo());
        assertEquals("test-q", qi.getParsedDebugInfo().getQueueName());
    }

    @Test
    void shouldNotFailOnInvalidDebugInfo() {
        DreaminaQueryQueueInfo qi = new DreaminaQueryQueueInfo();
        qi.setDebugInfo("not-json");
        DreaminaQueueInfoSupport.enrichParsedDebugInfo(om, qi);
        assertNull(qi.getParsedDebugInfo());
    }

    @Test
    void shouldHandleNullQueueInfo() {
        DreaminaQueueInfoSupport.enrichParsedDebugInfo(om, null);
        // No exception expected
    }

    // --- DreaminaHelp ---
    @Test
    void shouldBuildHelp() {
        DreaminaHelp h = DreaminaHelp.builder().topic("login").build();
        assertEquals("login", h.getTopic());
    }

    // --- DreaminaLogin ---
    @Test
    void shouldDetectOAuthReuseOnly() {
        DreaminaLogin login = DreaminaLogin.builder()
            .oauthSessionReused(true)
            .build();
        assertTrue(login.isOAuthReuseOnly());
        assertFalse(login.hasAccount());
    }

    @Test
    void shouldDetectHasAccount() {
        DreaminaLoginAccount acc = new DreaminaLoginAccount();
        DreaminaLogin login = DreaminaLogin.builder().account(acc).build();
        assertTrue(login.hasAccount());
        assertFalse(login.isOAuthReuseOnly());
    }

    // --- DreaminaLoginAccount ---
    @Test
    void shouldSetAndGetAccountFields() {
        DreaminaLoginAccount acc = new DreaminaLoginAccount();
        acc.setUserId(12345L);
        acc.setVipLevel("maestro");
        acc.setTotalCredit(1000L);
        assertEquals(12345L, acc.getUserId());
        assertEquals("maestro", acc.getVipLevel());
        assertEquals(1000L, acc.getTotalCredit());
    }

    // --- DreaminaLogout ---
    @Test
    void shouldBuildLogout() {
        DreaminaLogout lo = DreaminaLogout.builder().localSessionCleared(true).build();
        assertTrue(lo.getLocalSessionCleared());
    }

    // --- DreaminaRelogin ---
    @Test
    void shouldDetectNeedsCheckLogin() {
        DreaminaDeviceLogin device = new DreaminaDeviceLogin();
        device.setDeviceCode("abc");
        DreaminaRelogin rl = DreaminaRelogin.builder().device(device).build();
        assertTrue(rl.needsCheckLogin());
    }

    @Test
    void shouldNotNeedCheckLoginWithoutDevice() {
        DreaminaRelogin rl = DreaminaRelogin.builder().build();
        assertFalse(rl.needsCheckLogin());
    }

    // --- DreaminaDeviceLogin ---
    @Test
    void shouldDetectMaterialPresent() {
        DreaminaDeviceLogin dl = new DreaminaDeviceLogin();
        assertFalse(dl.isMaterialPresent());
        dl.setDeviceCode("abc");
        assertTrue(dl.isMaterialPresent());
    }

    @Test
    void shouldDeserializeDeviceLogin() throws Exception {
        String json = "{\"device_code\":\"dc1\",\"verification_uri\":\"https://verify\",\"user_code\":\"UC123\",\"poll_interval\":\"5s\",\"expires_at\":\"2026-08-09T00:00:00Z\"}";
        DreaminaDeviceLogin dl = om.readValue(json, DreaminaDeviceLogin.class);
        assertEquals("dc1", dl.getDeviceCode());
        assertEquals("https://verify", dl.getVerificationUri());
        assertEquals("UC123", dl.getUserCode());
        assertEquals("5s", dl.getPollInterval());
    }

    // --- DreaminaSessionList / DreaminaSessionRow / DreaminaSessionSearch / DreaminaSessionMutation / DreaminaSessionDelete ---
    @Test
    void shouldReturnSafeRowsForNullList() {
        DreaminaSessionList sl = DreaminaSessionList.builder().build();
        assertTrue(sl.safeRows().isEmpty());
    }

    @Test
    void shouldBuildSessionRow() {
        DreaminaSessionRow row = DreaminaSessionRow.builder()
            .id("100").name("Test").pinned("No").updatedAt("2026-08-08 10:00").build();
        assertEquals("100", row.getId());
        assertEquals("Test", row.getName());
    }

    @Test
    void shouldReturnSafeRowsForNullSearch() {
        DreaminaSessionSearch ss = DreaminaSessionSearch.builder().queryTerm("q").build();
        assertTrue(ss.safeRows().isEmpty());
    }

    @Test
    void shouldBuildSessionMutation() {
        DreaminaSessionMutation mut = DreaminaSessionMutation.builder()
            .kind(DreaminaSessionMutation.Kind.CREATE).sessionId("100").sessionName("Test").build();
        assertEquals(DreaminaSessionMutation.Kind.CREATE, mut.getKind());
        assertEquals("100", mut.getSessionId());
    }

    @Test
    void shouldBuildSessionDelete() {
        DreaminaSessionDelete sd = DreaminaSessionDelete.builder().deleted(true).build();
        assertTrue(sd.isDeleted());
    }

    // --- DreaminaCommerceTriplet ---
    @Test
    void shouldDeserializeCommerceTriplet() throws Exception {
        String json = "{\"resource_type\":\"rt\",\"resource_id\":\"ri\",\"benefit_type\":\"bt\"}";
        DreaminaCommerceTriplet t = om.readValue(json, DreaminaCommerceTriplet.class);
        assertEquals("rt", t.getResourceType());
        assertEquals("ri", t.getResourceId());
        assertEquals("bt", t.getBenefitType());
    }
}
