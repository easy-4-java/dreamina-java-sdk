package io.github.easy4j.dreamina.cli.opts;

import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;
import io.github.easy4j.dreamina.cli.model.DreaminaCanvasSchema;
import lombok.Builder;
import lombok.Getter;

/**
 * 返回当前制品的导演台独立契约。仅显式参数进入命令，不自动运行或批准积分。
 */
@Getter
@Builder
public final class DreaminaCanvasSchemaDirectorRequest extends DreaminaCanvasRequest<DreaminaCanvasSchema> {
    /**
     * 显式启用确认策略，不替代积分凭证。
     */
    private final boolean yes;

    @Override
    public DreaminaCanvasCommand getCommand() {
        return DreaminaCanvasCommand.SCHEMA_DIRECTOR;
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
