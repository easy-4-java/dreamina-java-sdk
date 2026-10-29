package io.github.easy4j.dreamina.cli.opts;

import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;
import io.github.easy4j.dreamina.cli.model.DreaminaCanvasSpaceUseResult;
import lombok.Builder;
import lombok.Getter;

/**
 * 核验并显式切换空间。仅显式参数进入命令，不自动运行或批准积分。
 */
@Getter
@Builder
public final class DreaminaCanvasSpaceUseRequest extends DreaminaCanvasRequest<DreaminaCanvasSpaceUseResult> {
    /**
     * kind；位置参数。
     */
    @DreaminaCanvasParameter(value = "kind", positional = true)
    private final String kind;
    /**
     * team-id；位置参数。
     */
    @DreaminaCanvasParameter(value = "team-id", positional = true)
    private final String teamId;
    /**
     * 显式启用确认策略，不替代积分凭证。
     */
    private final boolean yes;

    @Override
    public DreaminaCanvasCommand getCommand() {
        return DreaminaCanvasCommand.SPACE_USE;
    }

    @Override
    public Class<DreaminaCanvasSpaceUseResult> getDataType() {
        return DreaminaCanvasSpaceUseResult.class;
    }

    @Override
    protected DreaminaCanvasArguments arguments() {
        DreaminaCanvasArguments args = new DreaminaCanvasArguments();
        args.position(kind);
        args.position(teamId);
        args.yes(yes);
        return args;
    }
}
