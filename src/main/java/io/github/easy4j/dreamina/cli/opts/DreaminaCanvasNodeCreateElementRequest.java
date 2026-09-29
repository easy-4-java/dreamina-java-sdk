package io.github.easy4j.dreamina.cli.opts;

import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;
import io.github.easy4j.dreamina.cli.model.DreaminaCanvasNodeCreateElementResult;
import lombok.Builder;
import lombok.Getter;

/**
 * 创建Element 节点，不触发生成。仅显式参数进入命令，不自动运行或批准积分。
 */
@Getter
@Builder
public final class DreaminaCanvasNodeCreateElementRequest extends DreaminaCanvasRequest<DreaminaCanvasNodeCreateElementResult> {
    /**
     * 项目 UUID；省略时使用当前项目；--project-id。
     */
    @DreaminaCanvasParameter(value = "project-id", positional = false)
    private final String projectId;
    /**
     * 稳定 node_id；省略时生成；--node-id。
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
     * Element 标题；未给出则保持；--title。
     */
    @DreaminaCanvasParameter(value = "title", positional = false)
    private final String title;
    /**
     * Element 描述；--description。
     */
    @DreaminaCanvasParameter(value = "description", positional = false)
    private final String description;
    /**
     * 颜色标签 ID：default-tag-1..default-tag-5；仅用户明确指定时传入；--tag。
     */
    @DreaminaCanvasParameter(value = "tag", positional = false)
    @lombok.Singular("addTag")
    private final java.util.List<String> tag;
    /**
     * 清空全部颜色标签；--clear-tags。
     */
    @DreaminaCanvasParameter(value = "clear-tags", positional = false)
    private final Boolean clearTags;
    /**
     * 主素材：已产出可用素材的画布节点 ID（node_ 前缀，跟随）或可用资源 UUID（冻结）；画布已有来源节点时优先使用 Node ID，保留上游连线；Resource ID 用于资产库等无来源节点的素材或用户明确要求冻结，不自动从节点产物提取；--main。
     */
    @DreaminaCanvasParameter(value = "main", positional = false)
    private final String main;
    /**
     * 音色素材：已产出可用 audio 的节点 ID 或可用 audio 资源 UUID；画布已有来源节点时优先使用 Node ID，保留上游连线；Resource ID 用于资产库等无来源节点的素材或用户明确要求冻结，不自动从节点产物提取；--voice。
     */
    @DreaminaCanvasParameter(value = "voice", positional = false)
    private final String voice;
    /**
     * 辅助素材，可重复传入：已产出可用 image 的节点 ID 或可用 image 资源 UUID；画布已有来源节点时优先使用 Node ID，保留上游连线；Resource ID 用于资产库等无来源节点的素材或用户明确要求冻结，不自动从节点产物提取；--auxiliary。
     */
    @DreaminaCanvasParameter(value = "auxiliary", positional = false)
    @lombok.Singular("addAuxiliary")
    private final java.util.List<String> auxiliary;
    /**
     * 节点导入类别：local_upload（挂本地上传登记的资源）或 external_generated（外部平台生成产物导入）；与 resource upload 的登记意图同一套词汇；--import-kind。
     */
    @DreaminaCanvasParameter(value = "import-kind", positional = false)
    private final String importKind;
    /**
     * external_generated 导入节点在根画布中的 X 坐标；必须与 --y 成组提供；--x。
     */
    @DreaminaCanvasParameter(value = "x", positional = false)
    private final java.math.BigDecimal x;
    /**
     * external_generated 导入节点在根画布中的 Y 坐标；必须与 --x 成组提供；--y。
     */
    @DreaminaCanvasParameter(value = "y", positional = false)
    private final java.math.BigDecimal y;
    /**
     * 显式启用确认策略，不替代积分凭证。
     */
    private final boolean yes;

    @Override
    public DreaminaCanvasCommand getCommand() {
        return DreaminaCanvasCommand.NODE_CREATE_ELEMENT;
    }

    @Override
    public Class<DreaminaCanvasNodeCreateElementResult> getDataType() {
        return DreaminaCanvasNodeCreateElementResult.class;
    }

    @Override
    protected DreaminaCanvasArguments arguments() {
        DreaminaCanvasArguments args = new DreaminaCanvasArguments();
        args.flag("project-id", projectId);
        args.flag("node-id", nodeId);
        args.flag("update-id", updateId);
        args.flag("dry-run", dryRun);
        args.flag("title", title);
        args.flag("description", description);
        args.flag("tag", tag);
        args.flag("clear-tags", clearTags);
        args.flag("main", main);
        args.flag("voice", voice);
        args.flag("auxiliary", auxiliary);
        args.flag("import-kind", importKind);
        args.flag("x", x);
        args.flag("y", y);
        args.yes(yes);
        return args;
    }
}
