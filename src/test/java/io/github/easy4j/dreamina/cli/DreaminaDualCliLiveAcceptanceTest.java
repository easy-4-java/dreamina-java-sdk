package io.github.easy4j.dreamina.cli;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.easy4j.dreamina.DreaminaCliProperties;
import io.github.easy4j.dreamina.cli.model.DreaminaSessionMutation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/** 显式启用的真实 SDK 验收；日志只保留状态和哈希，不保存账户、提示词或凭据。 */
class DreaminaDualCliLiveAcceptanceTest {
    private final DreaminaCliExecutor legacy = new DreaminaCliExecutor(properties());
    private static DreaminaCliProperties properties() {
        DreaminaCliProperties props = new DreaminaCliProperties();
        props.setExecutable(System.getProperty("dreamina.cli.executable", "dreamina"));
        props.setCommandTimeoutMillis(120000L);
        return props;
    }
    private Path directory() throws Exception {
        Path dir = Paths.get(System.getProperty("dual.cli.evidenceDir", "target/dual-cli-live"));
        Files.createDirectories(dir);
        return dir;
    }
    private void record(String command, DreaminaCliResult result) throws Exception {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("cli", "dreamina");
        row.put("command", command);
        row.put("success", result.isSuccess());
        row.put("exitCode", result.getExitCode());
        byte[] digest = MessageDigest.getInstance("SHA-256").digest(result.getStdout().getBytes(StandardCharsets.UTF_8));
        StringBuilder hash = new StringBuilder();
        for (byte value : digest) {
            hash.append(String.format("%02x", value & 0xff));
        }
        row.put("stdoutSha256", hash.toString());
        Files.write(directory().resolve("legacy-live.jsonl"), (new ObjectMapper().writeValueAsString(row) + "\n").getBytes(StandardCharsets.UTF_8),
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        assertTrue(result.isSuccess(), command + " failed; see exit status in evidence");
    }
    private void record(String command, DreaminaCliResponse<?> result) throws Exception {
        record(command, DreaminaCliResult.builder().stdout(result.getStdout()).stderr(result.getStderr())
                .exitCode(result.getExitCode()).success(result.isSuccess()).build());
    }
    @Test
    @EnabledIfSystemProperty(named = "dual.cli.live.readOnly", matches = "true")
    void legacyReadOnlyCommandsThroughSdk() throws Exception {
        record("version", legacy.versionInfo());
        DreaminaCliResponse<?> account = legacy.userCreditInfo();
        record("user_credit", account);
        assertNotNull(account.getBody());
        record("session", legacy.session());
        record("list_task", legacy.listTaskInfo(Arrays.asList("--limit=1")));
    }
    @Test
    @EnabledIfSystemProperty(named = "dual.cli.live.readOnly", matches = "true")
    void legacySessionReadOnlyCommandsThroughSdk() throws Exception {
        try {
        DreaminaCliResponse<?> list = legacy.sessionListInfo(Arrays.asList("--max-count=1"));
        record("session list", list);
        assertNotNull(list.getBody());
        record("session search", legacy.sessionSearchInfo("SDK-E2E-CONTRACT"));
        } catch (io.github.easy4j.dreamina.exception.DreaminaCliException failure) {
            DreaminaCliResult raw = failure.getPartialResult();
            if (Objects.nonNull(raw) && (raw.getStdout() + raw.getStderr()).contains("ret=3018")) {
                Map<String, Object> blocked = new LinkedHashMap<>();
                blocked.put("cli", "dreamina");
                blocked.put("command", "session list");
                blocked.put("status", "blocked");
                blocked.put("reason", "service permission denied ret=3018");
                new ObjectMapper().writeValue(directory().resolve("legacy-session-blocked.json").toFile(), blocked);
                org.junit.jupiter.api.Assumptions.assumeTrue(false, "service permission denied ret=3018; live Session coverage remains blocked");
            }
            throw failure;
        }
    }
    @Test
    @EnabledIfSystemProperty(named = "dual.cli.live.sessionWrite", matches = "true")
    void isolatedSessionLifecycleThroughSdk() throws Exception {
        Path ledger = directory().resolve("legacy-sessions.json");
        String name;
        if (Files.exists(ledger)) {
            Map<String, String> old = new ObjectMapper().readValue(ledger.toFile(), new com.fasterxml.jackson.core.type.TypeReference<Map<String, String>>() {});
            assertTrue(Boolean.getBoolean("dual.cli.live.resumeSessionAfterAuth"), "existing Session ledger requires explicit resume");
            assertEquals("blocked-ret-3018", old.get("createState"), "only the pre-write permission rejection may be resumed after fresh authorization");
            name = old.get("name");
        } else {
            name = "SDK-E2E-CONTRACT-" + System.currentTimeMillis();
        }
        Map<String, String> sessions = new LinkedHashMap<>();
        sessions.put("name", name);
        sessions.put("createState", "pending");
        new ObjectMapper().writeValue(ledger.toFile(), sessions);
        DreaminaCliResponse<DreaminaSessionMutation> created;
        try {
            created = legacy.sessionCreateInfo(Arrays.asList(name));
        } catch (io.github.easy4j.dreamina.exception.DreaminaCliException failure) {
            DreaminaCliResult raw = failure.getPartialResult();
            if (Objects.nonNull(raw) && (raw.getStdout() + raw.getStderr()).contains("ret=3018")) {
                sessions.put("createState", "blocked-ret-3018");
                new ObjectMapper().writeValue(ledger.toFile(), sessions);
                org.junit.jupiter.api.Assumptions.assumeTrue(false, "service denied isolated Session creation ret=3018");
            }
            throw failure;
        }
        record("session create", created);
        assertNotNull(created.getBody());
        String id = Objects.requireNonNull(created.getBody().getSessionId(), "created session identity must be preserved");
        sessions.put("generationSession", id);
        sessions.put("createState", "created");
        sessions.put("name", name);
        new ObjectMapper().writeValue(ledger.toFile(), sessions);
        record("session rename", legacy.sessionRenameInfo(id, name + "-verified"));
        assertTrue(legacy.sessionSearch(name + "-verified").getStdout().contains(id));
        DreaminaCliResponse<DreaminaSessionMutation> disposable = legacy.sessionCreateInfo(Arrays.asList(name + "-delete-only"));
        record("session create", disposable);
        String disposableId = Objects.requireNonNull(disposable.getBody().getSessionId());
        assertNotEquals(id, disposableId);
        sessions.put("disposableSession", disposableId);
        new ObjectMapper().writeValue(ledger.toFile(), sessions);
        record("session delete", legacy.sessionDelete(disposableId));
        // 原生 search 无匹配时退出 1；删除验收以列表不再包含专用 ID 为准。
        assertFalse(legacy.sessionList(Arrays.asList("--max-count=100")).getStdout().contains(disposableId));
        sessions.put("disposableDeleted", "true");
        new ObjectMapper().writeValue(ledger.toFile(), sessions);
    }
    @Test
    @EnabledIfSystemProperty(named = "dual.cli.live.verifySessionAfterDelete", matches = "true")
    void verifyPreviouslyCreatedAndDeletedSessionsWithoutRepeatingWrites() throws Exception {
        Path ledger = directory().resolve("legacy-sessions.json");
        Map<String, String> sessions = new ObjectMapper().readValue(ledger.toFile(), new com.fasterxml.jackson.core.type.TypeReference<Map<String, String>>() {});
        String id = Objects.requireNonNull(sessions.get("generationSession"));
        String deleted = Objects.requireNonNull(sessions.get("disposableSession"));
        DreaminaCliResponse<?> found = legacy.sessionSearchInfo(sessions.get("name") + "-verified");
        record("session search", found);
        assertTrue(found.getStdout().contains(id));
        DreaminaCliResponse<?> listed = legacy.sessionListInfo(Arrays.asList("--max-count=100"));
        record("session list", listed);
        assertFalse(listed.getStdout().contains(deleted));
        sessions.put("disposableDeleted", "true");
        new ObjectMapper().writeValue(ledger.toFile(), sessions);
    }

}
