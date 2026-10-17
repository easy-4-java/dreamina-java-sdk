package io.github.easy4j.dreamina.cli.opts;

import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;
import io.github.easy4j.dreamina.cli.model.DreaminaCanvasNodeEditTimelineResult;
import lombok.Builder;
import lombok.Getter;

/**
 * 编辑时间轴，把多个素材按顺序拼接，不触发生成。仅显式参数进入命令，不自动运行或批准积分。
 */
@Getter
@Builder
public final class DreaminaCanvasNodeEditTimelineRequest extends DreaminaCanvasRequest<DreaminaCanvasNodeEditTimelineResult> {
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
     * 跨项目资源来源：<资源 UUID>@<来源项目 UUID>；可重复，仅声明本次保存使用的资源；--resource-source。
     */
    @DreaminaCanvasParameter(value = "resource-source", positional = false)
    @lombok.Singular("addResourceSource")
    private final java.util.List<String> resourceSource;
    /**
     * 时间轴标题；按 flag 出现更新；--title。
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
     * 清空全部片段；保留协议要求的空视觉轨；--clear-tracks。
     */
    @DreaminaCanvasParameter(value = "clear-tracks", positional = false)
    private final Boolean clearTracks;
    /**
     * 显式启用确认策略，不替代积分凭证。
     */
    private final boolean yes;

    @Override
    public DreaminaCanvasCommand getCommand() {
        return DreaminaCanvasCommand.NODE_EDIT_TIMELINE;
    }

    @Override
    public Class<DreaminaCanvasNodeEditTimelineResult> getDataType() {
        return DreaminaCanvasNodeEditTimelineResult.class;
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
        args.flag("resource-source", resourceSource);
        args.flag("title", title);
        args.flag("clip", clip);
        args.flag("audio-clip", audioClip);
        args.flag("clear-tracks", clearTracks);
        args.yes(yes);
        return args;
    }
}
