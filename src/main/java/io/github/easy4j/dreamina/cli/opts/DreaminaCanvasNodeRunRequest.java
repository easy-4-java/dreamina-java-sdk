package io.github.easy4j.dreamina.cli.opts;

import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;
import io.github.easy4j.dreamina.cli.model.DreaminaCanvasNodeRunResult;
import lombok.Builder;
import lombok.Getter;

/**
 * 触发给定节点的运行。仅显式参数进入命令，不自动运行或批准积分。
 */
@Getter
@Builder
public final class DreaminaCanvasNodeRunRequest extends DreaminaCanvasRequest<DreaminaCanvasNodeRunResult> {
    /**
     * 项目 UUID；同时用作 draft-id；省略时使用当前项目；--project-id。
     */
    @DreaminaCanvasParameter(value = "project-id", positional = false)
    private final String projectId;
    /**
     * 需要运行的节点 node_id，可重复传入；--node-id。
     */
    @DreaminaCanvasParameter(value = "node-id", positional = false)
    @lombok.Singular("addNodeId")
    private final java.util.List<String> nodeId;
    /**
     * 逐节点稳定提交 UUID，可重复传入并与 --node-id 按顺序对应；全部省略时自动生成；--submit-id。
     */
    @DreaminaCanvasParameter(value = "submit-id", positional = false)
    @lombok.Singular("addSubmitId")
    private final java.util.List<String> submitId;
    /**
     * 确认扣费后返回的 creditConfirmationToken；--credit-token。
     */
    @DreaminaCanvasParameter(value = "credit-token", positional = false)
    private final String creditToken;
    /**
     * 显式启用确认策略，不替代积分凭证。
     */
    private final boolean yes;

    @Override
    public DreaminaCanvasCommand getCommand() {
        return DreaminaCanvasCommand.NODE_RUN;
    }

    @Override
    public Class<DreaminaCanvasNodeRunResult> getDataType() {
        return DreaminaCanvasNodeRunResult.class;
    }

    @Override
    protected DreaminaCanvasArguments arguments() {
        DreaminaCanvasArguments args = new DreaminaCanvasArguments();
        args.flag("project-id", projectId);
        args.flag("node-id", nodeId);
        args.flag("submit-id", submitId);
        args.flag("credit-token", creditToken);
        args.yes(yes);
        return args;
    }
}
