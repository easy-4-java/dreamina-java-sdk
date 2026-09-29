package io.github.easy4j.dreamina.exception;


import io.github.easy4j.dreamina.cli.DreaminaCliResult;
import lombok.Getter;

/**
 * 执行或协议故障；消息不回显参数和凭据。发生超时后需用原 submitId 查询，不自动重试。
 */
@Getter
public final class DreaminaCanvasCliException extends DreaminaCliException {
    private static final long serialVersionUID = 1L;
    private final Reason reason;
    private final String command;
    /**
     * 构造异常快照；原始输出可能含敏感字段，仅通过显式 getter 访问。
     */
    public DreaminaCanvasCliException(Reason reason, String command, Integer exitCode, String stdout, String stderr) {
        super("Canvas CLI " + command + ": " + reason.name(), DreaminaCliResult.builder()
                .exitCode(exitCode).stdout(stdout).stderr(stderr).success(false).build());
        this.reason = reason;
        this.command = command;
    }

    /**
     * 复用基类持有的进程结果。
     */
    public Integer getExitCode() {
        return getPartialResult().getExitCode();
    }

    /**
     * 显式读取原始标准输出。
     */
    public String getStdout() {
        return getPartialResult().getStdout();
    }

    /**
     * 显式读取原始标准错误。
     */
    public String getStderr() {
        return getPartialResult().getStderr();
    }

    /**
     * 可区分的执行失败原因。
     */
    public enum Reason {START_FAILED, TIMEOUT, INTERRUPTED, OUTPUT_LIMIT, INVALID_RESPONSE, IO_FAILED}
}
