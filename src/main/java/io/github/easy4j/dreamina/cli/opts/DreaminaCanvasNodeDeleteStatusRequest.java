package io.github.easy4j.dreamina.cli.opts;

import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;
import io.github.easy4j.dreamina.cli.model.DreaminaCanvasNodeDeleteStatusResult;
import lombok.Builder;
import lombok.Getter;

/**
 * 只读查询该批次首次删除结果；不签发凭证、不删除节点。仅显式参数进入命令，不自动运行或批准积分。
 */
@Getter
@Builder
public final class DreaminaCanvasNodeDeleteStatusRequest extends DreaminaCanvasRequest<DreaminaCanvasNodeDeleteStatusResult> {
    /**
     * 项目 UUID；省略时使用当前画布；--project-id。
     */
    @DreaminaCanvasParameter(value = "project-id", positional = false)
    private final String projectId;
    /**
     * 指定 Node ID，可重复或逗号分隔，按顺序去重；不支持 group；--node-id。
     */
    @DreaminaCanvasParameter(value = "node-id", positional = false)
    @lombok.Singular("addNodeId")
    private final java.util.List<String> nodeId;
    /**
     * 摘要返回的稳定更新 UUID；必填，确认、执行与恢复须相同；--update-id。
     */
    @DreaminaCanvasParameter(value = "update-id", positional = false)
    private final String updateId;
    /**
     * 显式启用确认策略，不替代积分凭证。
     */
    private final boolean yes;

    @Override
    public DreaminaCanvasCommand getCommand() {
        return DreaminaCanvasCommand.NODE_DELETE_STATUS;
    }

    @Override
    public Class<DreaminaCanvasNodeDeleteStatusResult> getDataType() {
        return DreaminaCanvasNodeDeleteStatusResult.class;
    }

    @Override
    protected DreaminaCanvasArguments arguments() {
        DreaminaCanvasArguments args = new DreaminaCanvasArguments();
        args.flag("project-id", projectId);
        args.flag("node-id", nodeId);
        args.flag("update-id", updateId);
        args.yes(yes);
        return args;
    }
}
