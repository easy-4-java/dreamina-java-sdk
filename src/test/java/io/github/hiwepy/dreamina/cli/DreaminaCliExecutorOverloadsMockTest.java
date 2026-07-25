package io.github.hiwepy.dreamina.cli;

import io.github.hiwepy.dreamina.cli.opts.DreaminaImageModelVersion;
import io.github.hiwepy.dreamina.cli.opts.DreaminaImageResolutionType;
import io.github.hiwepy.dreamina.cli.opts.DreaminaRatio;
import io.github.hiwepy.dreamina.cli.opts.DreaminaVideoModelVersion;
import io.github.hiwepy.dreamina.cli.opts.DreaminaVideoResolutionType;

import io.github.hiwepy.dreamina.cli.model.DreaminaDeviceLogin;
import io.github.hiwepy.dreamina.cli.support.MockDreaminaCli;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 覆盖 {@link DreaminaCliExecutor} 无参 overload、additionalRawArgs 与 *Info/*Submit 快捷方法。
 */
class DreaminaCliExecutorOverloadsMockTest {

    private MockDreaminaCli mockCli;
    private DreaminaCliExecutor executor;
    private Path tinyPng;

    @BeforeEach
    void setUp() throws Exception {
        mockCli = MockDreaminaCli.install();
        mockCli.resetLog();
        executor = mockCli.newExecutor();
        tinyPng = mockCli.newTinyPng("tiny.png");
    }

    @Test void sessionNoArgsOverload() throws Exception {
        assertTrue(executor.session().isSuccess());
    }

    @Test void sessionCreateNoArgsOverload() throws Exception {
        assertTrue(executor.sessionCreate().isSuccess());
        assertNotNull(executor.sessionCreateInfo().getBody().getSessionId());
    }

    @Test void sessionListNoArgsOverload() throws Exception {
        assertTrue(executor.sessionList().isSuccess());
        assertTrue(executor.sessionListInfo().getBody().getRows().size() >= 1);
    }

    @Test void listTaskNoArgsOverload() throws Exception {
        assertTrue(executor.listTask().isSuccess());
    }

    @Test void logoutNoArgsOverload() throws Exception {
        assertTrue(executor.logout().isSuccess());
    }

    @Test void reloginNoArgsOverload() throws Exception {
        assertTrue(executor.relogin().isSuccess());
    }

    @Test void loginWithAdditionalArgsOverload() throws Exception {
        assertTrue(executor.login(Arrays.asList("--verbose")).isSuccess());
    }

    @Test void loginHeadlessWithAdditionalArgsOverload() throws Exception {
        assertTrue(executor.loginHeadless(Arrays.asList("--headless")).isSuccess());
        assertNotNull(executor.loginHeadlessInfo(Arrays.asList("--headless")).getBody());
    }

    @Test void checkLoginWithAdditionalArgsOverload() throws Exception {
        assertTrue(executor.checkLogin("dev-mock", 30, Arrays.asList("--verbose")).isSuccess());
    }

    @Test void logoutWithAdditionalArgsOverload() throws Exception {
        assertTrue(executor.relogin(Arrays.asList("--force")).isSuccess());
    }

    @Test void sessionSearchSingleArgOverload() throws Exception {
        assertEquals(1, executor.sessionSearchInfo("mock").getBody().safeRows().size());
    }

    @Test void sessionSearchInfoWithAdditionalArgsOverload() throws Exception {
        assertTrue(executor.sessionSearchInfo("mock", Arrays.asList("-n=5")).getBody().safeRows().size() >= 1);
    }

    @Test void sessionRenameWithAdditionalArgsOverload() throws Exception {
        assertTrue(executor.sessionRename("10001", "x", Arrays.asList("--verbose")).isSuccess());
        assertNotNull(executor.sessionRenameInfo("10001", "x", Arrays.asList("--verbose")).getBody());
    }

    @Test void sessionDeleteWithAdditionalArgsOverload() throws Exception {
        assertTrue(executor.sessionDelete("10001", Arrays.asList("--force")).isSuccess());
    }

    @Test void queryResultWithAdditionalArgsOverload() throws Exception {
        assertTrue(executor.queryResult("mock-submit-1", Arrays.asList("--download_dir=./dl")).isSuccess());
        assertNotNull(executor.queryResultInfo("mock-submit-1", Arrays.asList("--download_dir=./dl")).getBody());
    }

    @Test void invokeWithNullAdditionalArgsOverload() throws Exception {
        assertTrue(executor.invoke(DreaminaCliSubcommands.Builtin.HELP, null).isSuccess());
    }

    @Test void deviceLoginMaterialOverload() throws Exception {
        DreaminaCliResult raw = executor.loginHeadless(Arrays.asList("--mock-device-flow"));
        DreaminaCliResponse<DreaminaDeviceLogin> typed = executor.deviceLoginMaterial(raw);
        assertNotNull(typed.getBody().getDeviceCode());
        assertNotNull(typed.getBody().getVerificationUri());
    }

    @Test void text2ImagePromptOnlyOverload() throws Exception {
        assertTrue(executor.text2Image("solo-prompt").isSuccess());
    }

    @Test void text2videoPromptOnlyOverload() throws Exception {
        assertTrue(executor.text2video("solo-video").isSuccess());
    }

    @Test void imageUpscaleNoArgsOverload() throws Exception {
        assertTrue(executor.imageUpscale().isSuccess());
    }

    @Test void frames2videoNoArgsOverload() throws Exception {
        assertTrue(executor.frames2video().isSuccess());
    }

    @Test void multiframe2videoNoArgsOverload() throws Exception {
        assertTrue(executor.multiframe2video().isSuccess());
    }

    @Test void multimodal2videoNoArgsOverload() throws Exception {
        assertTrue(executor.multimodal2video().isSuccess());
    }

    @Test void image2videoWithoutPromptOverload() throws Exception {
        assertThrows(IllegalArgumentException.class,
            () -> executor.image2video(tinyPng.toString(), Arrays.asList("--poll=0")));
    }

    @Test void text2ImageSubmitRawArgsOverload() throws Exception {
        assertNotNull(executor.text2ImageSubmit("cat", Arrays.asList("--poll=0")).getBody().getSubmitId());
    }

    @Test void image2ImageSubmitRawArgsOverload() throws Exception {
        assertNotNull(executor.image2ImageSubmit(
            tinyPng.toString(), "style", Arrays.asList("--poll=0")).getBody().getSubmitId());
    }

    @Test void imageUpscaleSubmitRawArgsOverload() throws Exception {
        assertNotNull(executor.imageUpscaleSubmit(Arrays.asList("--image=" + tinyPng, "--poll=0"))
            .getBody().getSubmitId());
    }

    @Test void text2VideoSubmitRawArgsOverload() throws Exception {
        assertNotNull(executor.text2VideoSubmit("run", Arrays.asList("--poll=0")).getBody().getSubmitId());
    }

    @Test void image2VideoSubmitRawArgsOverload() throws Exception {
        assertNotNull(executor.image2VideoSubmit(
            tinyPng.toString(), "zoom", Arrays.asList("--poll=0")).getBody().getSubmitId());
    }

    @Test void frames2VideoSubmitRawArgsOverload() throws Exception {
        assertNotNull(executor.frames2VideoSubmit(Arrays.asList(
            "--first=" + tinyPng, "--last=" + tinyPng, "--poll=0")).getBody().getSubmitId());
    }

    @Test void multiframe2VideoSubmitRawArgsOverload() throws Exception {
        Path second = mockCli.newTinyPng("tiny3.png");
        assertNotNull(executor.multiframe2VideoSubmit(Arrays.asList(
            "--images=" + tinyPng + "," + second, "--poll=0")).getBody().getSubmitId());
    }

    @Test void multimodal2VideoSubmitRawArgsOverload() throws Exception {
        assertNotNull(executor.multimodal2VideoSubmit(Arrays.asList(
            "--image=" + tinyPng, "--prompt=p", "--poll=0")).getBody().getSubmitId());
    }

    @Test void appendCleanArgsSkipsBlankAndNullElements() throws Exception {
        assertTrue(executor.login(Arrays.asList(null, "  ", "--verbose")).isSuccess());
    }

    @Test void listTaskInfoWithAdditionalArgsOverload() throws Exception {
        assertNotNull(executor.listTaskInfo(Arrays.asList("--gen_status=success")).getBody());
    }

    @Test void helpInfoSubcommandOnlyOverload() throws Exception {
        assertNotNull(executor.helpInfo("text2image").getBody());
    }
}
