package io.github.easy4j.dreamina.cli.opts;

import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;
import io.github.easy4j.dreamina.cli.model.DreaminaCanvasAuthRefreshResult;
import lombok.Builder;
import lombok.Getter;

/**
 * 刷新当前登录凭据。仅显式参数进入命令，不自动运行或批准积分。
 */
@Getter
@Builder
public final class DreaminaCanvasAuthRefreshRequest extends DreaminaCanvasRequest<DreaminaCanvasAuthRefreshResult> {
    /**
     * 显式启用确认策略，不替代积分凭证。
     */
    private final boolean yes;

    @Override
    public DreaminaCanvasCommand getCommand() {
        return DreaminaCanvasCommand.AUTH_REFRESH;
    }

    @Override
    public Class<DreaminaCanvasAuthRefreshResult> getDataType() {
        return DreaminaCanvasAuthRefreshResult.class;
    }

    @Override
    protected DreaminaCanvasArguments arguments() {
        DreaminaCanvasArguments args = new DreaminaCanvasArguments();
        args.yes(yes);
        return args;
    }
}
