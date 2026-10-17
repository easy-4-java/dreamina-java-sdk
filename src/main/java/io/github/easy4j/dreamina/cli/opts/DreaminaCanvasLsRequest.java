package io.github.easy4j.dreamina.cli.opts;

import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;
import io.github.easy4j.dreamina.cli.model.DreaminaCanvasLsResult;
import lombok.Builder;
import lombok.Getter;

/**
 * 列举可访问的画布，只回定位摘要（projectId/name/createdAt/updatedAt/webUrl）。仅显式参数进入命令，不自动运行或批准积分。
 */
@Getter
@Builder
public final class DreaminaCanvasLsRequest extends DreaminaCanvasRequest<DreaminaCanvasLsResult> {
    /**
     * 单页条数，范围 1 到 100；--limit。
     */
    @DreaminaCanvasParameter(value = "limit", positional = false)
    private final Long limit;
    /**
     * 上一页返回的 nextCursor；首页省略。服务端不透明字符串，不要构造或解析；--cursor。
     */
    @DreaminaCanvasParameter(value = "cursor", positional = false)
    private final String cursor;
    /**
     * 显式启用确认策略，不替代积分凭证。
     */
    private final boolean yes;

    @Override
    public DreaminaCanvasCommand getCommand() {
        return DreaminaCanvasCommand.CANVAS_LS;
    }

    @Override
    public Class<DreaminaCanvasLsResult> getDataType() {
        return DreaminaCanvasLsResult.class;
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
