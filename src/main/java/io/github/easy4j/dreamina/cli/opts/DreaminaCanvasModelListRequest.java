package io.github.easy4j.dreamina.cli.opts;

import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;
import io.github.easy4j.dreamina.cli.model.DreaminaCanvasModelListResult;
import lombok.Builder;
import lombok.Getter;

/**
 * 列出生成模型及可填写 flag。仅显式参数进入命令，不自动运行或批准积分。
 */
@Getter
@Builder
public final class DreaminaCanvasModelListRequest extends DreaminaCanvasRequest<DreaminaCanvasModelListResult> {
    /**
     * 资源类型：image、video 或 audio；--type。
     */
    @DreaminaCanvasParameter(value = "type", positional = false)
    private final String type;
    /**
     * 显式启用确认策略，不替代积分凭证。
     */
    private final boolean yes;

    @Override
    public DreaminaCanvasCommand getCommand() {
        return DreaminaCanvasCommand.MODEL_LIST;
    }

    @Override
    public Class<DreaminaCanvasModelListResult> getDataType() {
        return DreaminaCanvasModelListResult.class;
    }

    @Override
    protected DreaminaCanvasArguments arguments() {
        DreaminaCanvasArguments args = new DreaminaCanvasArguments();
        args.flag("type", type);
        args.yes(yes);
        return args;
    }
}
