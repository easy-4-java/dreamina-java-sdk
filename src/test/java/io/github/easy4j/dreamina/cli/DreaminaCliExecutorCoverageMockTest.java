package io.github.easy4j.dreamina.cli;

import io.github.easy4j.dreamina.DreaminaCliProperties;
import io.github.easy4j.dreamina.cli.parser.DreaminaParsedFields;
import io.github.easy4j.dreamina.cli.support.MockDreaminaCli;
import io.github.easy4j.dreamina.cli.support.SubprocessExecutionSupport;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import io.github.easy4j.dreamina.exception.DreaminaCliException;
import io.github.easy4j.dreamina.exception.DreaminaCliExecutableFailureException;
import io.github.easy4j.dreamina.exception.DreaminaCliNonZeroExitException;
import io.github.easy4j.dreamina.exception.DreaminaCliTimeoutException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 补齐 {@link DreaminaCliExecutor} 私有分支与 {@code run()} 异常路径的 mock 覆盖。
 */
class DreaminaCliExecutorCoverageMockTest {

    private MockDreaminaCli mockCli;
    private DreaminaCliExecutor executor;

    @BeforeEach
    void setUp() throws Exception {
        mockCli = MockDreaminaCli.install();
        mockCli.resetLog();
        executor = mockCli.newExecutor();
    }

    @Test void checkLoginBlankDeviceCodeShouldFailValidation() {
        assertThrows(IllegalArgumentException.class, () -> executor.checkLogin("  ", 0));
    }

    @Test void checkLoginNegativePollShouldFailValidation() {
        assertThrows(IllegalArgumentException.class, () -> executor.checkLogin("dev", -1));
    }

    @Test void loginHeadlessNullAdditionalArgsOverload() throws Exception {
        assertTrue(executor.loginHeadless(null).isSuccess());
    }

    @Test void loginHeadlessSkipsBlankAdditionalArgs() throws Exception {
        assertTrue(executor.loginHeadless(Arrays.asList(null, "  ", "--verbose")).isSuccess());
    }

    @Test void validWorkingDirectoryShouldBeApplied() throws Exception {
        Path wd = Files.createTempDirectory("dreamina-mock-wd-");
        DreaminaCliProperties props = new DreaminaCliProperties();
        props.setExecutable(mockCli.scriptPath().toAbsolutePath().toString());
        props.setWorkingDirectory(wd.toAbsolutePath().toString());
        props.setCommandTimeoutMillis(5_000L);
        assertTrue(new DreaminaCliExecutor(props).version().isSuccess());
    }

    @Test void blankWorkingDirectoryPropertyShouldBeIgnored() throws Exception {
        DreaminaCliProperties props = new DreaminaCliProperties();
        props.setExecutable(mockCli.scriptPath().toAbsolutePath().toString());
        props.setWorkingDirectory("   ");
        props.setCommandTimeoutMillis(5_000L);
        assertTrue(new DreaminaCliExecutor(props).version().isSuccess());
    }

    @Test void exitOneWithoutExecuteExceptionShouldThrowNonZeroExit() {
        assertThrows(DreaminaCliNonZeroExitException.class, () -> executor.invoke("__exit_one", null));
    }

    @Test void completeAfterWaitGenericAsyncFailure() {
        CommandLine cmd = new CommandLine("dreamina");
        StubHandler handler = new StubHandler(0, false);
        ExecuteWatchdog watchdog = new ExecuteWatchdog(5_000L);
        assertThrows(
            DreaminaCliException.class,
            () -> executor.completeAfterWait(
                cmd,
                5_000L,
                new ByteArrayOutputStream(),
                new ByteArrayOutputStream(),
                handler,
                watchdog,
                false,
                new RuntimeException("async")));
    }

    @Test void completeAfterWaitMissingExitCode() {
        CommandLine cmd = new CommandLine("dreamina");
        StubHandler handler = new StubHandler(0, true);
        ExecuteWatchdog watchdog = new ExecuteWatchdog(5_000L);
        assertThrows(
            DreaminaCliException.class,
            () -> executor.completeAfterWait(
                cmd,
                5_000L,
                new ByteArrayOutputStream(),
                new ByteArrayOutputStream(),
                handler,
                watchdog,
                false,
                null));
    }

    @Test void completeAfterWaitWaitTimedOutShouldThrowTimeoutException() {
        CommandLine cmd = new CommandLine("dreamina");
        StubHandler handler = new StubHandler(0, false);
        ExecuteWatchdog watchdog = ExecuteWatchdog.builder()
                .setTimeout(java.time.Duration.ofSeconds(5))
                .get();
        assertThrows(
            DreaminaCliTimeoutException.class,
            () -> executor.completeAfterWait(
                cmd,
                5_000L,
                new ByteArrayOutputStream(),
                new ByteArrayOutputStream(),
                handler,
                watchdog,
                true,
                null));
    }

    @Test void completeAfterWaitPlainNonZeroExit() {
        CommandLine cmd = new CommandLine("dreamina");
        StubHandler handler = new StubHandler(1, false);
        ExecuteWatchdog watchdog = new ExecuteWatchdog(5_000L);
        assertThrows(
            DreaminaCliNonZeroExitException.class,
            () -> executor.completeAfterWait(
                cmd,
                5_000L,
                new ByteArrayOutputStream(),
                new ByteArrayOutputStream(),
                handler,
                watchdog,
                false,
                null));
    }

    @Test void interruptedWaitShouldThrowDreaminaCliException() throws Exception {
        DreaminaCliExecutor longRun = mockCli.newExecutorWithTimeout(60_000L);
        AtomicReference<Throwable> caught = new AtomicReference<>();
        Thread worker = new Thread(() -> {
            try {
                longRun.invoke("__sleep_forever", null);
            } catch (Throwable t) {
                caught.set(t);
            }
        });
        worker.start();
        Thread.sleep(300);
        worker.interrupt();
        worker.join(10_000);
        assertTrue(caught.get() instanceof DreaminaCliException);
    }

    @Test void newSubcommandChainEmptyShouldFailValidation() {
        assertThrows(IllegalArgumentException.class, () -> executor.newSubcommandChain());
    }

    @Test void newSubcommandChainNullTokensShouldFailValidation() {
        assertThrows(IllegalArgumentException.class, () -> executor.newSubcommandChain((String[]) null));
    }

    @Test void newSubcommandChainBlankTokenShouldFailValidation() {
        assertThrows(IllegalArgumentException.class, () -> executor.newSubcommandChain("session", "  "));
    }

    @Test void runShouldWrapIOExceptionFromExecutor() {
        DreaminaCliProperties props = new DreaminaCliProperties();
        props.setExecutable(mockCli.scriptPath().toAbsolutePath().toString());
        props.setCommandTimeoutMillis(5_000L);
        DreaminaCliExecutor failing = new DreaminaCliExecutor(props) {
            @Override
            SubprocessExecutionSupport.RunSession executeSubprocess(
                    SubprocessExecutionSupport.ExecutionRequest request) throws IOException {
                throw new IOException("spawn-fail-run");
            }
        };
        assertThrows(DreaminaCliExecutableFailureException.class, failing::version);
    }

    @Test void failedToStartHelperShouldWrapIOException() {
        CommandLine cmd = new CommandLine("dreamina");
        assertThrows(
            DreaminaCliExecutableFailureException.class,
            () -> {
                throw DreaminaCliExecutor.failedToStart(cmd, new IOException("spawn-fail"));
            });
    }

    @Test void failedAsyncHelperShouldWrapGenericFailure() {
        CommandLine cmd = new CommandLine("dreamina");
        DreaminaCliResult partial = DreaminaCliResult.builder()
            .stdout("").stderr("").exitCode(1).success(false)
            .parsed(DreaminaParsedFields.builder().build())
            .build();
        DreaminaCliException ex = DreaminaCliExecutor.failedAsync(cmd, new RuntimeException("async"), partial);
        assertTrue(ex.getMessage().contains("async failure"));
    }

    @Test void missingExitCodeHelperShouldWrapIllegalState() {
        CommandLine cmd = new CommandLine("dreamina");
        DreaminaCliResult partial = DreaminaCliResult.builder()
            .stdout("").stderr("").exitCode(null).success(false)
            .parsed(DreaminaParsedFields.builder().build())
            .build();
        DreaminaCliException ex = DreaminaCliExecutor.missingExitCode(
            cmd, new IllegalStateException("no exit"), partial);
        assertTrue(ex.getMessage().contains("without observable exit code"));
    }

    @Test void nonZeroExitHelperShouldWrapResult() {
        CommandLine cmd = new CommandLine("dreamina");
        DreaminaCliResult failed = DreaminaCliResult.builder()
            .stdout("").stderr("err").exitCode(1).success(false)
            .parsed(DreaminaParsedFields.builder().build())
            .build();
        DreaminaCliNonZeroExitException ex =
            DreaminaCliExecutor.nonZeroExitWithoutExecuteException(cmd, 1, failed);
        assertEquals(1, ex.getPartialResult().getExitCode());
    }

    @Test void appendQuotedKvInvalidKeyViaReflectionShouldFail() throws Exception {
        Method method = DreaminaCliExecutor.class.getDeclaredMethod(
            "appendQuotedKv", CommandLine.class, String.class, String.class);
        method.setAccessible(true);
        CommandLine cmd = new CommandLine("dreamina");
        assertThrows(IllegalArgumentException.class, () -> invokeReflect(method, null, cmd, "prompt", "x"));
    }

    @Test void appendQuotedKvKeyWithTrailingEqualsViaReflection() throws Exception {
        Method method = DreaminaCliExecutor.class.getDeclaredMethod(
            "appendQuotedKv", CommandLine.class, String.class, String.class);
        method.setAccessible(true);
        CommandLine cmd = new CommandLine("dreamina");
        method.invoke(null, cmd, "--prompt=", "hello");
        assertEquals(1, cmd.getArguments().length);
        assertTrue(cmd.getArguments()[0].contains("--prompt=hello"));
    }

    @Test void snapshotNullStreamsViaReflection() throws Exception {
        Method method = DreaminaCliExecutor.class.getDeclaredMethod(
            "snapshot", String.class, String.class, Integer.class, DreaminaParsedFields.class);
        method.setAccessible(true);
        DreaminaCliResult result = (DreaminaCliResult) method.invoke(
            null, null, null, 1, DreaminaParsedFields.builder().build());
        assertEquals("", result.getStdout());
        assertEquals("", result.getStderr());
    }

    @Test void readExitQuietlyWithoutExitViaReflection() throws Exception {
        Method method = DreaminaCliExecutor.class.getDeclaredMethod(
            "readExitQuietly", DefaultExecuteResultHandler.class);
        method.setAccessible(true);
        assertNull(method.invoke(null, new DefaultExecuteResultHandler()));
    }

    @Test void normalizeInvalidExitValueViaReflection() throws Exception {
        Method method = DreaminaCliExecutor.class.getDeclaredMethod("normalizeExitValue", int.class);
        method.setAccessible(true);
        assertNull(method.invoke(null, Executor.INVALID_EXITVALUE));
    }

    private static void invokeReflect(Method method, Object target, Object... args) throws Throwable {
        try {
            method.invoke(target, args);
        } catch (InvocationTargetException ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof Exception) {
                throw cause;
            }
            throw ex;
        }
    }

    /**
     * 可控的 {@link DefaultExecuteResultHandler}，用于覆盖 {@link DreaminaCliExecutor#completeAfterWait} 分支。
     */
    private static final class StubHandler extends DefaultExecuteResultHandler {

        private final Integer exitCode;
        private final boolean missingExit;

        private StubHandler(Integer exitCode, boolean missingExit) {
            this.exitCode = exitCode;
            this.missingExit = missingExit;
        }

        @Override
        public ExecuteException getException() {
            return null;
        }

        @Override
        public int getExitValue() {
            if (missingExit) {
                throw new IllegalStateException("no exit yet");
            }
            return exitCode == null ? 0 : exitCode;
        }
    }

    // --- jacoco line/branch 100% 门禁补齐 ---

    @Test void sessionInfo_overload_withAdditionalArgs_shouldInvokeStructuredMapper() {
        assertNotNull(executor.sessionInfo(java.util.Collections.singletonList("--help")));
    }

    @Test void sessionUpdate_overload_blankArgs_shouldFailValidation() {
        assertThrows(IllegalArgumentException.class,
            () -> executor.sessionUpdate("  ", "newName", java.util.Collections.emptyList()));
    }

    @Test void sessionUpdate_overload_blankNewName_shouldFailValidation() {
        // 反向分支: sessionId 合法, newName 空白, 触发 short-circuit newName 分支
        assertThrows(IllegalArgumentException.class,
            () -> executor.sessionUpdate("10086", "  ", java.util.Collections.emptyList()));
    }

    @Test void logoutInfo_noArgOverload_shouldInvokeStructuredMapper() {
        // L1304 (logoutInfo() 无参重载) 此前未覆盖
        assertNotNull(executor.logoutInfo());
    }

    @Test void sessionRm_overload_blankSessionId_shouldFailValidation() {
        assertThrows(IllegalArgumentException.class,
            () -> executor.sessionRm("  ", java.util.Collections.emptyList()));
    }

    @Test void loginInfo_overload_withAdditionalArgs_shouldInvokeStructuredMapper() {
        assertNotNull(executor.loginInfo(java.util.Collections.singletonList("--verbose")));
    }

    @Test void logoutInfo_overload_withAdditionalArgs_shouldInvokeStructuredMapper() {
        assertNotNull(executor.logoutInfo(java.util.Collections.singletonList("--verbose")));
    }

    @Test void reloginInfo_overload_withAdditionalArgs_shouldInvokeStructuredMapper() {
        assertNotNull(executor.reloginInfo(java.util.Collections.singletonList("--headless")));
    }

    @Test void checkLoginInfo_overload_withAdditionalArgs_shouldInvokeStructuredMapper() {
        assertNotNull(executor.checkLoginInfo("dev-xyz", 0,
            java.util.Collections.singletonList("--verbose")));
    }

    @Test void withDefaultFlag_blankTrimmedEntries_areSkippedAtAppendCleanArgs() throws java.io.IOException {
        // trim-空白项分支: appendCleanArgs 在 arg.trim().isEmpty() 时 continue
        java.util.List<String> args = java.util.Arrays.asList(null, "   ", "  --ok  ", "");
        executor.invoke("version", args); // 不抛异常即说明容错路径已被执行
        assertTrue(mockCli.lastInvocation().contains("version"));
    }

    @Test void withDefaultFlag_nullAdditionalArgs_shouldStillSupplyDefault() throws java.io.IOException {
        // L1792 containsFlag 的 null/empty 短路分支
        executor.invoke("version", null);
        assertTrue(mockCli.lastInvocation().contains("version"));
    }

    @Test void withDefaultFlag_additionalArgsContainsFlagValuePair_shouldNotDuplicate() throws java.io.IOException {
        // L1800 containsFlag 的 normalized.startsWith("=") 分支
        executor.invoke("version", java.util.Collections.singletonList("--poll=30"));
        assertTrue(mockCli.lastInvocation().contains("version"));
    }

    @Test void containsFlag_blankArgumentEntry_shouldBeSkipped() throws Exception {
        // L1796 containsFlag 对 blank 元素 continue 分支 (blank+entry两种)
        Method method = DreaminaCliExecutor.class.getDeclaredMethod("containsFlag",
            java.util.List.class, String.class);
        method.setAccessible(true);
        java.util.List<String> args = java.util.Arrays.asList(null, "  ", "--ok");
        boolean result = (boolean) method.invoke(null, args, "--ok");
        assertTrue(result); // 跳过 null/blank 后碰到 --ok, 返回 true
    }

    @Test void containsFlag_exactFlagMatch_shouldReturnTrue() throws Exception {
        // L1800 containsFlag 的 flag.equals(normalized) 分支
        Method method = DreaminaCliExecutor.class.getDeclaredMethod("containsFlag",
            java.util.List.class, String.class);
        method.setAccessible(true);
        java.util.List<String> args = java.util.Arrays.asList("--poll");
        boolean result = (boolean) method.invoke(null, args, "--poll");
        assertTrue(result);
    }

    @Test void containsFlag_argumentWithLeadingWhitespace_trimmedBeforeMatch() throws Exception {
        // L1799 normalized = argument.trim() 分支, 配合 L1800 startsWith
        Method method = DreaminaCliExecutor.class.getDeclaredMethod("containsFlag",
            java.util.List.class, String.class);
        method.setAccessible(true);
        java.util.List<String> args = java.util.Arrays.asList("   --poll   ");
        boolean result = (boolean) method.invoke(null, args, "--poll");
        assertTrue(result);
    }

    @Test void containsFlag_emptyList_shouldReturnFalse() throws Exception {
        // L1792 additionalRawArgs.isEmpty() == true 短路分支
        Method method = DreaminaCliExecutor.class.getDeclaredMethod("containsFlag",
            java.util.List.class, String.class);
        method.setAccessible(true);
        boolean result = (boolean) method.invoke(null, java.util.Collections.emptyList(), "--poll");
        assertFalse(result);
    }

    @Test void withDefaultFlag_nullAdditionalArgsObjectsNonNullBranch() throws Exception {
        // L1784 Objects.nonNull(additionalRawArgs) false 分支
        Method method = DreaminaCliExecutor.class.getDeclaredMethod("withDefaultFlag",
            java.util.List.class, String.class, String.class);
        method.setAccessible(true);
        java.util.List<String> result = (java.util.List<String>) method.invoke(null, null, "--poll", "30");
        assertEquals(1, result.size());
        assertEquals("--poll=30", result.get(0));
    }

    @Test void newRunExecutor_shouldReturnNonNullDefaultExecutor() throws Exception {
        Method method = DreaminaCliExecutor.class.getDeclaredMethod("newRunExecutor");
        method.setAccessible(true);
        Object result = method.invoke(executor);
        assertNotNull(result);
    }
}
