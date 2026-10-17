package io.github.easy4j.dreamina.cli.opts;

import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;
import io.github.easy4j.dreamina.cli.model.DreaminaCanvasNodeDeleteResult;
import lombok.Builder;
import lombok.Getter;

/**
 * 确认后批量删除节点；暂不支持群组。仅显式参数进入命令，不自动运行或批准积分。
 */
@Getter
@Builder
public final class DreaminaCanvasNodeDeleteRequest extends DreaminaCanvasRequest<DreaminaCanvasNodeDeleteResult> {
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
     * 稳定更新 UUID；省略时生成；确认和重试必须复用；--update-id。
     */
    @DreaminaCanvasParameter(value = "update-id", positional = false)
    private final String updateId;
    /**
     * node delete confirm 签发的短期凭证；必须复用同一 --update-id；--delete-token。
     */
    @DreaminaCanvasParameter(value = "delete-token", positional = false)
    private final String deleteToken;
    /**
     * 显式启用确认策略，不替代积分凭证。
     */
    private final boolean yes;

    @Override
    public DreaminaCanvasCommand getCommand() {
        return DreaminaCanvasCommand.NODE_DELETE;
    }

    @Override
    public Class<DreaminaCanvasNodeDeleteResult> getDataType() {
        return DreaminaCanvasNodeDeleteResult.class;
    }

    @Override
    protected DreaminaCanvasArguments arguments() {
        DreaminaCanvasArguments args = new DreaminaCanvasArguments();
        args.flag("project-id", projectId);
        args.flag("node-id", nodeId);
        args.flag("update-id", updateId);
        args.flag("delete-token", deleteToken);
        args.yes(yes);
        return args;
    }
}
