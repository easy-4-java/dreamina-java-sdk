package io.github.easy4j.dreamina.cli.opts;

import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;
import io.github.easy4j.dreamina.cli.model.DreaminaCanvasCreateResult;
import lombok.Builder;
import lombok.Getter;

/**
 * 创建画布。仅显式参数进入命令，不自动运行或批准积分。
 */
@Getter
@Builder
public final class DreaminaCanvasCreateRequest extends DreaminaCanvasRequest<DreaminaCanvasCreateResult> {
    /**
     * 客户端生成的画布 UUID；重试时复用同一值；--project-id。
     */
    @DreaminaCanvasParameter(value = "project-id", positional = false)
    private final String projectId;
    /**
     * 创建后设为当前画布；--use。
     */
    @DreaminaCanvasParameter(value = "use", positional = false)
    private final Boolean use;
    /**
     * name；位置参数。
     */
    @DreaminaCanvasParameter(value = "name", positional = true)
    private final String name;
    /**
     * 显式启用确认策略，不替代积分凭证。
     */
    private final boolean yes;

    @Override
    public DreaminaCanvasCommand getCommand() {
        return DreaminaCanvasCommand.CANVAS_CREATE;
    }

    @Override
    public Class<DreaminaCanvasCreateResult> getDataType() {
        return DreaminaCanvasCreateResult.class;
    }

    @Override
    protected DreaminaCanvasArguments arguments() {
        DreaminaCanvasArguments args = new DreaminaCanvasArguments();
        args.flag("project-id", projectId);
        args.flag("use", use);
        args.position(name);
        args.yes(yes);
        return args;
    }
}
