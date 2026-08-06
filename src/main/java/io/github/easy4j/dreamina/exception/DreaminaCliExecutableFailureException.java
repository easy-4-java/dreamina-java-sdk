package io.github.easy4j.dreamina.exception;

import io.github.easy4j.dreamina.cli.DreaminaCliResult;
/**
 * 无法在操作系统层面启动 CLI（例如命令不存在或路径非法）时抛出。
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
public class DreaminaCliExecutableFailureException extends DreaminaCliException {

    /**
     * @param message 面向日志的失败说明
     * @param cause   通常为 {@link java.io.IOException}
     */
    public DreaminaCliExecutableFailureException(String message, Throwable cause) {
        super(message, cause, null);
    }
}
