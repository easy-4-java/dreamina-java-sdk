package io.github.easy4j.dreamina.cli.opts;

import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;
import io.github.easy4j.dreamina.cli.model.DreaminaCanvasDirectorCatalogResult;
import lombok.Builder;
import lombok.Getter;

/** 发现内置几何体、预设、人物或动作；保留官方默认值，仅传显式参数。 */
@Getter
@Builder
public final class DreaminaCanvasDirectorCatalogRequest extends DreaminaCanvasRequest<DreaminaCanvasDirectorCatalogResult> {
    /** builtin、character 或 motion；--kind。 */
    @DreaminaCanvasParameter(value = "kind", positional = false)
    private final String kind;
    /** 分页偏移；--offset。 */
    @DreaminaCanvasParameter(value = "offset", positional = false)
    private final Long offset;
    /** 每页条数（1..100）；--count。 */
    @DreaminaCanvasParameter(value = "count", positional = false)
    private final Long count;
    /** 显式确认策略，不替代任何服务端授权。 */
    private final boolean yes;
    @Override
    public DreaminaCanvasCommand getCommand() {
        return DreaminaCanvasCommand.DIRECTOR_CATALOG;
    }
    @Override
    public Class<DreaminaCanvasDirectorCatalogResult> getDataType() {
        return DreaminaCanvasDirectorCatalogResult.class;
    }
    @Override
    protected DreaminaCanvasArguments arguments() {
        DreaminaCanvasArguments args = new DreaminaCanvasArguments();
        args.flag("kind", kind);
        args.flag("offset", offset);
        args.flag("count", count);
        args.yes(yes);
        return args;
    }
}
