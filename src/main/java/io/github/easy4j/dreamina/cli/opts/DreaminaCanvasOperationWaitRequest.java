package io.github.easy4j.dreamina.cli.opts;

import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;
import io.github.easy4j.dreamina.cli.model.DreaminaCanvasOperationWaitResult;
import lombok.Builder;
import lombok.Getter;

/**
 * 等待生成操作进入终态。仅显式参数进入命令，不自动运行或批准积分。
 */
@Getter
@Builder
public final class DreaminaCanvasOperationWaitRequest extends DreaminaCanvasRequest<DreaminaCanvasOperationWaitResult> {
    /**
     * 项目 UUID；仅在本地恢复记录丢失时需要，用于直接向服务端只读查询；--project-id。
     */
    @DreaminaCanvasParameter(value = "project-id", positional = false)
    private final String projectId;
    /**
     * 最长等待时间；0 表示只查询一次；--timeout。
     */
    @DreaminaCanvasParameter(value = "timeout", positional = false)
    private final java.time.Duration timeout;
    /**
     * 轮询间隔；--interval。
     */
    @DreaminaCanvasParameter(value = "interval", positional = false)
    private final java.time.Duration interval;
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
        return DreaminaCanvasCommand.OPERATION_WAIT;
    }

    @Override
    public Class<DreaminaCanvasOperationWaitResult> getDataType() {
        return DreaminaCanvasOperationWaitResult.class;
    }

    @Override
    protected DreaminaCanvasArguments arguments() {
        DreaminaCanvasArguments args = new DreaminaCanvasArguments();
        args.flag("project-id", projectId);
        args.flag("timeout", timeout);
        args.flag("interval", interval);
        args.position(operationRef);
        args.yes(yes);
        return args;
    }
}
