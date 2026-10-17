package io.github.easy4j.dreamina.cli.opts;

import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;
import io.github.easy4j.dreamina.cli.model.DreaminaCanvasResourceGetResult;
import lombok.Builder;
import lombok.Getter;

/**
 * 查素材状态与稳定事实（running/success/failed/canceled 四态都可查）。仅显式参数进入命令，不自动运行或批准积分。
 */
@Getter
@Builder
public final class DreaminaCanvasResourceGetRequest extends DreaminaCanvasRequest<DreaminaCanvasResourceGetResult> {
    /**
     * 项目 UUID；省略时使用当前项目；--project-id。
     */
    @DreaminaCanvasParameter(value = "project-id", positional = false)
    private final String projectId;
    /**
     * resource-id；位置参数。
     */
    @DreaminaCanvasParameter(value = "resource-id", positional = true)
    private final String resourceId;
    /**
     * 显式启用确认策略，不替代积分凭证。
     */
    private final boolean yes;

    @Override
    public DreaminaCanvasCommand getCommand() {
        return DreaminaCanvasCommand.RESOURCE_GET;
    }

    @Override
    public Class<DreaminaCanvasResourceGetResult> getDataType() {
        return DreaminaCanvasResourceGetResult.class;
    }

    @Override
    protected DreaminaCanvasArguments arguments() {
        DreaminaCanvasArguments args = new DreaminaCanvasArguments();
        args.flag("project-id", projectId);
        args.position(resourceId);
        args.yes(yes);
        return args;
    }
}
