package io.github.easy4j.dreamina.cli;

import io.github.easy4j.dreamina.exception.DreaminaCliExecutableFailureException;
import org.apache.commons.exec.CommandLine;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

/** 进程启动错误保留类型与原因，但公共消息不能复制完整 argv。 */
class DreaminaCliSpawnPrivacyTest {
    @Test void spawnFailureMessageDoesNotRevealPromptOrToken() {
        CommandLine command = new CommandLine("dreamina");
        command.addArgument("text2image");
        command.addArgument("--prompt=private-prompt", false);
        command.addArgument("--token=private-token", false);
        IOException cause = new IOException("system launch failed");
        DreaminaCliExecutableFailureException failure = DreaminaCliExecutor.failedToStart(command, cause);
        assertSame(cause, failure.getCause());
        assertFalse(failure.getMessage().contains("private-prompt"));
        assertFalse(failure.getMessage().contains("private-token"));
    }
    @Test void otherExecutionFailuresPreserveSnapshotWithoutArgv() {
        CommandLine command = new CommandLine("dreamina");
        command.addArgument("--prompt=private-prompt", false);
        DreaminaCliResult snapshot = DreaminaCliResult.builder().stdout("original submit_id").stderr("business error").exitCode(20).success(false).build();
        for (io.github.easy4j.dreamina.exception.DreaminaCliException failure : java.util.Arrays.asList(
                DreaminaCliExecutor.nonZeroExitWithoutExecuteException(command, 20, snapshot),
                DreaminaCliExecutor.failedAsync(command, new IOException("private-prompt"), snapshot),
                DreaminaCliExecutor.missingExitCode(command, new IllegalStateException("unknown"), snapshot))) {
            assertSame(snapshot, failure.getPartialResult());
            assertFalse(failure.getMessage().contains("private-prompt"));
        }
    }
}
