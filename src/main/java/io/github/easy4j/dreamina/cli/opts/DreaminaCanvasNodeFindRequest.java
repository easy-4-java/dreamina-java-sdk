package io.github.easy4j.dreamina.cli.opts;

import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;
import io.github.easy4j.dreamina.cli.model.DreaminaCanvasNodeFindResult;
import lombok.Builder;
import lombok.Getter;

/**
 * 按条件查节点，只回定位摘要（nodeId/type/title/status/tags）。仅显式参数进入命令，不自动运行或批准积分。
 */
@Getter
@Builder
public final class DreaminaCanvasNodeFindRequest extends DreaminaCanvasRequest<DreaminaCanvasNodeFindResult> {
    /**
     * 项目 UUID；同时用作 draft-id；省略时使用当前项目；--project-id。
     */
    @DreaminaCanvasParameter(value = "project-id", positional = false)
    private final String projectId;
    /**
     * 文本匹配：标题、描述、文本正文与生成 prompt；--name。
     */
    @DreaminaCanvasParameter(value = "name", positional = false)
    private final String name;
    /**
     * 节点类型，可重复；同维度多值为 OR。取值：image、video、audio、element、timeline、text；--type。
     */
    @DreaminaCanvasParameter(value = "type", positional = false)
    @lombok.Singular("addType")
    private final java.util.List<String> type;
    /**
     * 运行状态，可重复；同维度多值为 OR。取值：empty、running、success、partial_success、failed、canceled、unknown；--status。
     */
    @DreaminaCanvasParameter(value = "status", positional = false)
    @lombok.Singular("addStatus")
    private final java.util.List<String> status;
    /**
     * 颜色标签 ID，可重复；同维度多值为 AND（须同时具备）。取值：default-tag-1、default-tag-2、default-tag-3、default-tag-4、default-tag-5；--tag。
     */
    @DreaminaCanvasParameter(value = "tag", positional = false)
    @lombok.Singular("addTag")
    private final java.util.List<String> tag;
    /**
     * 单页条数，范围 1 到 50；--limit。
     */
    @DreaminaCanvasParameter(value = "limit", positional = false)
    private final Long limit;
    /**
     * 过滤后的偏移量，范围 0 到 1000；分页不绑定同一草稿快照；--offset。
     */
    @DreaminaCanvasParameter(value = "offset", positional = false)
    private final Long offset;
    /**
     * 显式启用确认策略，不替代积分凭证。
     */
    private final boolean yes;

    @Override
    public DreaminaCanvasCommand getCommand() {
        return DreaminaCanvasCommand.NODE_FIND;
    }

    @Override
    public Class<DreaminaCanvasNodeFindResult> getDataType() {
        return DreaminaCanvasNodeFindResult.class;
    }

    @Override
    protected DreaminaCanvasArguments arguments() {
        DreaminaCanvasArguments args = new DreaminaCanvasArguments();
        args.flag("project-id", projectId);
        args.flag("name", name);
        args.flag("type", type);
        args.flag("status", status);
        args.flag("tag", tag);
        args.flag("limit", limit);
        args.flag("offset", offset);
        args.yes(yes);
        return args;
    }
}
