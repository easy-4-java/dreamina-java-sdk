package io.github.easy4j.dreamina.cli.opts;

import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;
import io.github.easy4j.dreamina.cli.model.DreaminaCanvasCurrentResult;
import lombok.Builder;
import lombok.Getter;

/**
 * 查询当前画布和实时权限。仅显式参数进入命令，不自动运行或批准积分。
 */
@Getter
@Builder
public final class DreaminaCanvasCurrentRequest extends DreaminaCanvasRequest<DreaminaCanvasCurrentResult> {
    /**
     * 显式启用确认策略，不替代积分凭证。
     */
    private final boolean yes;

    @Override
    public DreaminaCanvasCommand getCommand() {
        return DreaminaCanvasCommand.CANVAS_CURRENT;
    }

    @Override
    public Class<DreaminaCanvasCurrentResult> getDataType() {
        return DreaminaCanvasCurrentResult.class;
    }

    @Override
    protected DreaminaCanvasArguments arguments() {
        DreaminaCanvasArguments args = new DreaminaCanvasArguments();
        args.yes(yes);
        return args;
    }
}
