package io.github.easy4j.dreamina.cli.opts;

import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;
import io.github.easy4j.dreamina.cli.model.DreaminaCanvasNodeQuoteResult;
import lombok.Builder;
import lombok.Getter;

/**
 * 对给定节点做运行前积分预估。仅显式参数进入命令，不自动运行或批准积分。
 */
@Getter
@Builder
public final class DreaminaCanvasNodeQuoteRequest extends DreaminaCanvasRequest<DreaminaCanvasNodeQuoteResult> {
    /**
     * 项目 UUID；同时用作 draft-id；省略时使用当前项目；--project-id。
     */
    @DreaminaCanvasParameter(value = "project-id", positional = false)
    private final String projectId;
    /**
     * 需要预估的节点 node_id，可重复传入；--node-id。
     */
    @DreaminaCanvasParameter(value = "node-id", positional = false)
    @lombok.Singular("addNodeId")
    private final java.util.List<String> nodeId;
    /**
     * 显式启用确认策略，不替代积分凭证。
     */
    private final boolean yes;

    @Override
    public DreaminaCanvasCommand getCommand() {
        return DreaminaCanvasCommand.NODE_QUOTE;
    }

    @Override
    public Class<DreaminaCanvasNodeQuoteResult> getDataType() {
        return DreaminaCanvasNodeQuoteResult.class;
    }

    @Override
    protected DreaminaCanvasArguments arguments() {
        DreaminaCanvasArguments args = new DreaminaCanvasArguments();
        args.flag("project-id", projectId);
        args.flag("node-id", nodeId);
        args.yes(yes);
        return args;
    }
}
