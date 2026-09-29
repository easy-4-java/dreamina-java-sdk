package io.github.easy4j.dreamina.cli.opts;

import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;
import io.github.easy4j.dreamina.cli.model.DreaminaCanvasModelResult;
import lombok.Builder;
import lombok.Getter;

/**
 * 查询生成模型及其可调用规格。仅显式参数进入命令，不自动运行或批准积分。
 */
@Getter
@Builder
public final class DreaminaCanvasModelRequest extends DreaminaCanvasRequest<DreaminaCanvasModelResult> {
    /**
     * 资源类型：image、video 或 audio；省略时搜索全部生成类型；--type。
     */
    @DreaminaCanvasParameter(value = "type", positional = false)
    private final String type;
    /**
     * name；位置参数。
     */
    @DreaminaCanvasParameter(value = "name", positional = true)
    @lombok.Singular("addName")
    private final java.util.List<String> name;
    /**
     * 显式启用确认策略，不替代积分凭证。
     */
    private final boolean yes;

    @Override
    public DreaminaCanvasCommand getCommand() {
        return DreaminaCanvasCommand.MODEL;
    }

    @Override
    public Class<DreaminaCanvasModelResult> getDataType() {
        return DreaminaCanvasModelResult.class;
    }

    @Override
    protected DreaminaCanvasArguments arguments() {
        DreaminaCanvasArguments args = new DreaminaCanvasArguments();
        args.flag("type", type);
        args.position(name);
        args.yes(yes);
        return args;
    }
}
