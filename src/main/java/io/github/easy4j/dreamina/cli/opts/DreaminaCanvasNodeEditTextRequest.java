package io.github.easy4j.dreamina.cli.opts;

import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;
import io.github.easy4j.dreamina.cli.model.DreaminaCanvasNodeEditTextResult;
import lombok.Builder;
import lombok.Getter;

/**
 * 编辑文本节点，不触发生成。仅显式参数进入命令，不自动运行或批准积分。
 */
@Getter
@Builder
public final class DreaminaCanvasNodeEditTextRequest extends DreaminaCanvasRequest<DreaminaCanvasNodeEditTextResult> {
    /**
     * 节点左上角 X；组内为父组相对坐标；与 --y 成组提供；--x。
     */
    @DreaminaCanvasParameter(value = "x", positional = false)
    private final java.math.BigDecimal x;
    /**
     * 节点左上角 Y；组内为父组相对坐标；与 --x 成组提供；--y。
     */
    @DreaminaCanvasParameter(value = "y", positional = false)
    private final java.math.BigDecimal y;
    /**
     * 项目 UUID；省略时使用当前项目；--project-id。
     */
    @DreaminaCanvasParameter(value = "project-id", positional = false)
    private final String projectId;
    /**
     * 要编辑的节点 node_id；必填；--node-id。
     */
    @DreaminaCanvasParameter(value = "node-id", positional = false)
    private final String nodeId;
    /**
     * 稳定更新 UUID；省略时生成；--update-id。
     */
    @DreaminaCanvasParameter(value = "update-id", positional = false)
    private final String updateId;
    /**
     * 只做本地校验并输出计划；不连接服务端、不写草稿、不报价或扣分；--dry-run。
     */
    @DreaminaCanvasParameter(value = "dry-run", positional = false)
    private final Boolean dryRun;
    /**
     * 文本节点标题；按 flag 出现更新；--title。
     */
    @DreaminaCanvasParameter(value = "title", positional = false)
    private final String title;
    /**
     * Markdown 正文；按 flag 出现更新；--text。
     */
    @DreaminaCanvasParameter(value = "text", positional = false)
    private final String text;
    /**
     * 显式启用确认策略，不替代积分凭证。
     */
    private final boolean yes;

    @Override
    public DreaminaCanvasCommand getCommand() {
        return DreaminaCanvasCommand.NODE_EDIT_TEXT;
    }

    @Override
    public Class<DreaminaCanvasNodeEditTextResult> getDataType() {
        return DreaminaCanvasNodeEditTextResult.class;
    }

    @Override
    protected DreaminaCanvasArguments arguments() {
        DreaminaCanvasArguments args = new DreaminaCanvasArguments();
        args.flag("x", x);
        args.flag("y", y);
        args.flag("project-id", projectId);
        args.flag("node-id", nodeId);
        args.flag("update-id", updateId);
        args.flag("dry-run", dryRun);
        args.flag("title", title);
        args.flag("text", text);
        args.yes(yes);
        return args;
    }
}
