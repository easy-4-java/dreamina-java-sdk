package io.github.easy4j.dreamina.cli.opts;

import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;
import io.github.easy4j.dreamina.cli.model.DreaminaCanvasTeamLsResult;
import lombok.Builder;
import lombok.Getter;

/**
 * 查看团队及订阅积分信息。仅显式参数进入命令，不自动运行或批准积分。
 */
@Getter
@Builder
public final class DreaminaCanvasTeamLsRequest extends DreaminaCanvasRequest<DreaminaCanvasTeamLsResult> {
    /**
     * 单页条数，范围 1 到 100；--limit。
     */
    @DreaminaCanvasParameter(value = "limit", positional = false)
    private final Long limit;
    /**
     * 上一页返回的不透明 nextCursor；--cursor。
     */
    @DreaminaCanvasParameter(value = "cursor", positional = false)
    private final String cursor;
    /**
     * 显式启用确认策略，不替代积分凭证。
     */
    private final boolean yes;

    @Override
    public DreaminaCanvasCommand getCommand() {
        return DreaminaCanvasCommand.TEAM_LS;
    }

    @Override
    public Class<DreaminaCanvasTeamLsResult> getDataType() {
        return DreaminaCanvasTeamLsResult.class;
    }

    @Override
    protected DreaminaCanvasArguments arguments() {
        DreaminaCanvasArguments args = new DreaminaCanvasArguments();
        args.flag("limit", limit);
        args.flag("cursor", cursor);
        args.yes(yes);
        return args;
    }
}
