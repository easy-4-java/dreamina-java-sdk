package io.github.easy4j.dreamina.exception;

import io.github.easy4j.dreamina.cli.DreaminaCliResult;
/**
 * CLI 进程以非预期非零退出码结束时抛出（语义由上层解释）。
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
public class DreaminaCliNonZeroExitException extends DreaminaCliException {

    /**
     * @param message 说明性消息
     * @param result  已组装的完整快照（{@link DreaminaCliResult#success} 一般为 false）
     */
    public DreaminaCliNonZeroExitException(String message, DreaminaCliResult result) {
        super(message, result);
    }
}
