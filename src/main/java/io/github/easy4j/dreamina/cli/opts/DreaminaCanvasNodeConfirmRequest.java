package io.github.easy4j.dreamina.cli.opts;

import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;
import io.github.easy4j.dreamina.cli.model.DreaminaCanvasNodeConfirmResult;
import lombok.Builder;
import lombok.Getter;

/**
 * 批准给定节点的积分消费上限并取回短期凭证。仅显式参数进入命令，不自动运行或批准积分。
 */
@Getter
@Builder
public final class DreaminaCanvasNodeConfirmRequest extends DreaminaCanvasRequest<DreaminaCanvasNodeConfirmResult> {
    /**
     * 项目 UUID；同时用作 draft-id；省略时使用当前项目；--project-id。
     */
    @DreaminaCanvasParameter(value = "project-id", positional = false)
    private final String projectId;
    /**
     * 需要批准的节点 node_id，可重复传入；--node-id。
     */
    @DreaminaCanvasParameter(value = "node-id", positional = false)
    @lombok.Singular("addNodeId")
    private final java.util.List<String> nodeId;
    /**
     * 批准的积分消费上限；低于服务端最新报价总额时不签发；--credit-ceiling。
     */
    @DreaminaCanvasParameter(value = "credit-ceiling", positional = false)
    private final Long creditCeiling;
    /**
     * 显式启用确认策略，不替代积分凭证。
     */
    private final boolean yes;

    @Override
    public DreaminaCanvasCommand getCommand() {
        return DreaminaCanvasCommand.NODE_CONFIRM;
    }

    @Override
    public Class<DreaminaCanvasNodeConfirmResult> getDataType() {
        return DreaminaCanvasNodeConfirmResult.class;
    }

    @Override
    protected DreaminaCanvasArguments arguments() {
        DreaminaCanvasArguments args = new DreaminaCanvasArguments();
        args.flag("project-id", projectId);
        args.flag("node-id", nodeId);
        args.flag("credit-ceiling", creditCeiling);
        args.yes(yes);
        return args;
    }
}
