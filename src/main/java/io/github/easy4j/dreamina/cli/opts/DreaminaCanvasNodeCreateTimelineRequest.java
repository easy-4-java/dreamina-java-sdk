package io.github.easy4j.dreamina.cli.opts;

import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;
import io.github.easy4j.dreamina.cli.model.DreaminaCanvasNodeCreateTimelineResult;
import lombok.Builder;
import lombok.Getter;

/**
 * 创建时间轴，把多个素材按顺序拼接，不触发生成。仅显式参数进入命令，不自动运行或批准积分。
 */
@Getter
@Builder
public final class DreaminaCanvasNodeCreateTimelineRequest extends DreaminaCanvasRequest<DreaminaCanvasNodeCreateTimelineResult> {
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
     * 时间轴标题；必填；--title。
     */
    @DreaminaCanvasParameter(value = "title", positional = false)
    private final String title;
    /**
     * 一个视觉片段，按出现顺序拼接；sourceId 只接受画布节点 ID 或 Resource ID；完整格式与选项见 --help；画布已有来源节点时优先使用 Node ID，保留上游连线；Resource ID 用于资产库等无来源节点的素材或用户明确要求冻结，不自动从节点产物提取；--clip。
     */
    @DreaminaCanvasParameter(value = "clip", positional = false)
    @lombok.Singular("addClip")
    private final java.util.List<String> clip;
    /**
     * 一个音轨片段，按出现顺序排列；来源身份与 --clip 相同，但可用选项不同，详见 --help；画布已有来源节点时优先使用 Node ID，保留上游连线；Resource ID 用于资产库等无来源节点的素材或用户明确要求冻结，不自动从节点产物提取；--audio-clip。
     */
    @DreaminaCanvasParameter(value = "audio-clip", positional = false)
    @lombok.Singular("addAudioClip")
    private final java.util.List<String> audioClip;
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
        return DreaminaCanvasCommand.NODE_CREATE_TIMELINE;
    }

    @Override
    public Class<DreaminaCanvasNodeCreateTimelineResult> getDataType() {
        return DreaminaCanvasNodeCreateTimelineResult.class;
    }

    @Override
    protected DreaminaCanvasArguments arguments() {
        DreaminaCanvasArguments args = new DreaminaCanvasArguments();
        args.flag("project-id", projectId);
        args.flag("node-id", nodeId);
        args.flag("update-id", updateId);
        args.flag("dry-run", dryRun);
        args.flag("title", title);
        args.flag("clip", clip);
        args.flag("audio-clip", audioClip);
        args.flag("import-kind", importKind);
        args.flag("x", x);
        args.flag("y", y);
        args.yes(yes);
        return args;
    }
}
