package io.github.easy4j.dreamina.cli.canvas;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.easy4j.dreamina.DreaminaCanvasCliProperties;
import io.github.easy4j.dreamina.DreaminaCliProperties;
import io.github.easy4j.dreamina.cli.DreaminaCanvasCliExecutor;
import io.github.easy4j.dreamina.cli.DreaminaCanvasResponse;
import io.github.easy4j.dreamina.cli.DreaminaCliExecutor;
import io.github.easy4j.dreamina.cli.DreaminaCliResponse;
import io.github.easy4j.dreamina.cli.model.DreaminaDeviceLogin;
import io.github.easy4j.dreamina.cli.model.DreaminaRelogin;
import io.github.easy4j.dreamina.cli.opts.DreaminaCanvasAuthWaitRequest;
import io.github.easy4j.dreamina.cli.opts.DreaminaCanvasAuthLogoutRequest;
import io.github.easy4j.dreamina.cli.opts.DreaminaCanvasAuthLoginRequest;
import io.github.easy4j.dreamina.cli.opts.DreaminaCanvasAuthRefreshRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/** 人工批准后真实退登/重新授权；设备凭据只留在本次 JVM 内，不写验收文件。 */
class DreaminaDualCliAuthLiveTest {
    private final ObjectMapper mapper = new ObjectMapper();
    private Path directory() { return Paths.get(System.getProperty("dual.cli.evidenceDir")); }
    private void record(String cli, String command, boolean success, int exit) throws Exception {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("cli", cli); row.put("command", command); row.put("success", success); row.put("exitCode", exit);
        Files.write(directory().resolve(cli.equals("dreamina") ? "legacy-live.jsonl" : "canvas-live.jsonl"),
                (mapper.writeValueAsString(row) + "\n").getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        assertTrue(success, "auth command must succeed: " + command);
    }
    private void legacy(String command, DreaminaCliResponse<?> result) throws Exception {
        record("dreamina", command, result.isSuccess(), result.getExitCode());
    }
    private void canvas(String command, DreaminaCanvasResponse<?> result) throws Exception {
        record("dreamina-canvas", command, result.isSuccess(), result.getExitCode());
    }
    private void showAuthorization(String label, DreaminaDeviceLogin challenge) throws Exception {
        assertNotNull(challenge);
        Map<String, String> visible = new LinkedHashMap<>();
        visible.put("url", Objects.nonNull(challenge.getVerificationUriComplete()) ? challenge.getVerificationUriComplete() : challenge.getVerificationUri());
        visible.put("userCode", challenge.getUserCode());
        System.out.println(label + " " + mapper.writeValueAsString(visible));
        System.out.flush();
    }
    @Test
    @EnabledIfSystemProperty(named = "dual.cli.live.authLegacy", matches = "true")
    void legacyLogoutReloginAndCheckLoginRestoresAccount() throws Exception {
        Path marker = directory().resolve("legacy-auth-lifecycle.json");
        assertFalse(Files.exists(marker), "resume an existing login rather than clearing it again");
        DreaminaCliProperties props = new DreaminaCliProperties(); props.setCommandTimeoutMillis(300000L);
        DreaminaCliExecutor cli = new DreaminaCliExecutor(props);
        legacy("help", cli.helpInfo());
        legacy("login", cli.loginInfo(Arrays.asList("--headless")));
        mapper.writeValue(marker.toFile(), java.util.Collections.singletonMap("state", "logout-approved"));
        legacy("logout", cli.logoutInfo());
        DreaminaCliResponse<DreaminaRelogin> fresh = cli.reloginInfo(Arrays.asList("--headless"));
        legacy("relogin", fresh);
        assertNotNull(fresh.getBody());
        DreaminaDeviceLogin challenge = fresh.getBody().getDevice();
        showAuthorization("AUTH_LEGACY", challenge);
        legacy("login checklogin", cli.checkLoginInfo(challenge.getDeviceCode(), 180));
        legacy("user_credit", cli.userCreditInfo());
        mapper.writeValue(marker.toFile(), java.util.Collections.singletonMap("state", "logged-in-restored"));
    }
    @Test
    @EnabledIfSystemProperty(named = "dual.cli.live.authCanvas", matches = "true")
    void canvasLogoutLoginWaitAndRefreshRestoresAccount() throws Exception {
        Path marker = directory().resolve("canvas-auth-lifecycle.json");
        assertFalse(Files.exists(marker), "resume an existing login rather than clearing it again");
        DreaminaCanvasCliExecutor cli = new DreaminaCanvasCliExecutor(DreaminaCanvasCliProperties.builder().timeout(Duration.ofSeconds(300)).build());
        Long original = cli.authAccount().getData().getUserId();
        mapper.writeValue(marker.toFile(), java.util.Collections.singletonMap("state", "logout-approved"));
        canvas("auth logout", cli.authLogout(DreaminaCanvasAuthLogoutRequest.builder().build()));
        DreaminaCanvasResponse<io.github.easy4j.dreamina.cli.model.DreaminaCanvasAuthLoginResult> fresh = cli.authLogin(DreaminaCanvasAuthLoginRequest.builder().build());
        canvas("auth login", fresh);
        DreaminaDeviceLogin challenge = fresh.getData().getChallenge();
        showAuthorization("AUTH_CANVAS", challenge);
        canvas("auth wait", cli.authWait(DreaminaCanvasAuthWaitRequest.builder().deviceCode(challenge.getDeviceCode()).timeout(Duration.ofSeconds(180)).build()));
        canvas("auth refresh", cli.authRefresh(DreaminaCanvasAuthRefreshRequest.builder().build()));
        DreaminaCanvasResponse<io.github.easy4j.dreamina.cli.model.DreaminaLoginAccount> account = cli.authAccount();
        canvas("auth account", account);
        assertEquals(original, account.getData().getUserId(), "do not continue with a different account");
        canvas("auth status", cli.authStatus());
        mapper.writeValue(marker.toFile(), java.util.Collections.singletonMap("state", "logged-in-restored"));
    }
}
