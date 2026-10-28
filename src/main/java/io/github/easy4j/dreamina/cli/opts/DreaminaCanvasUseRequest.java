package io.github.easy4j.dreamina.cli.opts;

import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;
import io.github.easy4j.dreamina.cli.model.DreaminaCanvasUseResult;
import lombok.Builder;
import lombok.Getter;

/**
 * 核验权限并选择当前画布。仅显式参数进入命令，不自动运行或批准积分。
 */
@Getter
@Builder
public final class DreaminaCanvasUseRequest extends DreaminaCanvasRequest<DreaminaCanvasUseResult> {
    /**
     * project-id；位置参数。
     */
    @DreaminaCanvasParameter(value = "project-id", positional = true)
    private final String projectId;
    /**
     * 显式启用确认策略，不替代积分凭证。
     */
    private final boolean yes;

    @Override
    public DreaminaCanvasCommand getCommand() {
        return DreaminaCanvasCommand.CANVAS_USE;
    }

    @Override
    public Class<DreaminaCanvasUseResult> getDataType() {
        return DreaminaCanvasUseResult.class;
    }

    @Override
    protected DreaminaCanvasArguments arguments() {
        DreaminaCanvasArguments args = new DreaminaCanvasArguments();
        args.position(projectId);
        args.yes(yes);
        return args;
    }
}
