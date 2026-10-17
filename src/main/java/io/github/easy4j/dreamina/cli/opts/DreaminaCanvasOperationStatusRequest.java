package io.github.easy4j.dreamina.cli.opts;

import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;
import io.github.easy4j.dreamina.cli.model.DreaminaCanvasOperationStatusResult;
import lombok.Builder;
import lombok.Getter;

/**
 * 查询一次生成操作状态。仅显式参数进入命令，不自动运行或批准积分。
 */
@Getter
@Builder
public final class DreaminaCanvasOperationStatusRequest extends DreaminaCanvasRequest<DreaminaCanvasOperationStatusResult> {
    /**
     * 项目 UUID；仅在本地恢复记录丢失时需要，用于直接向服务端只读查询；--project-id。
     */
    @DreaminaCanvasParameter(value = "project-id", positional = false)
    private final String projectId;
    /**
     * operation-ref；位置参数。
     */
    @DreaminaCanvasParameter(value = "operation-ref", positional = true)
    private final String operationRef;
    /**
     * 显式启用确认策略，不替代积分凭证。
     */
    private final boolean yes;

    @Override
    public DreaminaCanvasCommand getCommand() {
        return DreaminaCanvasCommand.OPERATION_STATUS;
    }

    @Override
    public Class<DreaminaCanvasOperationStatusResult> getDataType() {
        return DreaminaCanvasOperationStatusResult.class;
    }

    @Override
    protected DreaminaCanvasArguments arguments() {
        DreaminaCanvasArguments args = new DreaminaCanvasArguments();
        args.flag("project-id", projectId);
        args.position(operationRef);
        args.yes(yes);
        return args;
    }
}
