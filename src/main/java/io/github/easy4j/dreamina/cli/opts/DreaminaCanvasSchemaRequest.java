package io.github.easy4j.dreamina.cli.opts;

import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;
import io.github.easy4j.dreamina.cli.model.DreaminaCanvasSchema;
import lombok.Builder;
import lombok.Getter;

/**
 * 返回当前制品的命令契约。仅显式参数进入命令，不自动运行或批准积分。
 */
@Getter
@Builder
public final class DreaminaCanvasSchemaRequest extends DreaminaCanvasRequest<DreaminaCanvasSchema> {
    /**
     * 显式启用确认策略，不替代积分凭证。
     */
    private final boolean yes;

    @Override
    public DreaminaCanvasCommand getCommand() {
        return DreaminaCanvasCommand.SCHEMA;
    }

    @Override
    public Class<DreaminaCanvasSchema> getDataType() {
        return DreaminaCanvasSchema.class;
    }

    @Override
    protected DreaminaCanvasArguments arguments() {
        DreaminaCanvasArguments args = new DreaminaCanvasArguments();
        args.yes(yes);
        return args;
    }
}
