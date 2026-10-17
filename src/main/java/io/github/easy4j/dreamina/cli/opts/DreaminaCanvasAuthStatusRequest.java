package io.github.easy4j.dreamina.cli.opts;

import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;
import io.github.easy4j.dreamina.cli.model.DreaminaCanvasAuthStatusResult;
import lombok.Builder;
import lombok.Getter;

/**
 * 读取本地登录状态。仅显式参数进入命令，不自动运行或批准积分。
 */
@Getter
@Builder
public final class DreaminaCanvasAuthStatusRequest extends DreaminaCanvasRequest<DreaminaCanvasAuthStatusResult> {
    /**
     * 显式启用确认策略，不替代积分凭证。
     */
    private final boolean yes;

    @Override
    public DreaminaCanvasCommand getCommand() {
        return DreaminaCanvasCommand.AUTH_STATUS;
    }

    @Override
    public Class<DreaminaCanvasAuthStatusResult> getDataType() {
        return DreaminaCanvasAuthStatusResult.class;
    }

    @Override
    protected DreaminaCanvasArguments arguments() {
        DreaminaCanvasArguments args = new DreaminaCanvasArguments();
        args.yes(yes);
        return args;
    }
}
