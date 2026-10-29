package io.github.easy4j.dreamina.cli.opts;

import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;
import io.github.easy4j.dreamina.cli.model.DreaminaCanvasSpaceCurrentResult;
import lombok.Builder;
import lombok.Getter;

/**
 * 查询当前空间和已选画布的实时权限。仅显式参数进入命令，不自动运行或批准积分。
 */
@Getter
@Builder
public final class DreaminaCanvasSpaceCurrentRequest extends DreaminaCanvasRequest<DreaminaCanvasSpaceCurrentResult> {
    /**
     * 显式启用确认策略，不替代积分凭证。
     */
    private final boolean yes;

    @Override
    public DreaminaCanvasCommand getCommand() {
        return DreaminaCanvasCommand.SPACE_CURRENT;
    }

    @Override
    public Class<DreaminaCanvasSpaceCurrentResult> getDataType() {
        return DreaminaCanvasSpaceCurrentResult.class;
    }

    @Override
    protected DreaminaCanvasArguments arguments() {
        DreaminaCanvasArguments args = new DreaminaCanvasArguments();
        args.yes(yes);
        return args;
    }
}
