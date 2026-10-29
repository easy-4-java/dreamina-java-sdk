package io.github.easy4j.dreamina.cli.opts;

import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;
import io.github.easy4j.dreamina.cli.model.DreaminaCanvasDirectorPreviewResult;
import lombok.Builder;
import lombok.Getter;

/** 查看当前镜头或分镜帧的截图（需本机浏览器）；保留官方默认值，仅传显式参数。 */
@Getter
@Builder
public final class DreaminaCanvasDirectorPreviewRequest extends DreaminaCanvasRequest<DreaminaCanvasDirectorPreviewResult> {
    /** 本地导演台工作文件；--file。 */
    @DreaminaCanvasParameter(value = "file", positional = false)
    private final String file;
    /** 画布项目 UUID；默认当前项目；--project-id。 */
    @DreaminaCanvasParameter(value = "project-id", positional = false)
    private final String projectId;
    /** 直接操作画布导演台；与 file 互斥；--node-id。 */
    @DreaminaCanvasParameter(value = "node-id", positional = false)
    private final String nodeId;
    /** 新建产物路径（不覆盖）；--output。 */
    @DreaminaCanvasParameter(value = "output", positional = false)
    private final String output;
    /** staging 或 performance；仅选择渲染工作区；--workspace。 */
    @DreaminaCanvasParameter(value = "workspace", positional = false)
    private final String workspace;
    /** 镜头 ID；默认输出镜头；--camera-id。 */
    @DreaminaCanvasParameter(value = "camera-id", positional = false)
    private final String cameraId;
    /** 使用分镜序列；--storyboard。 */
    @DreaminaCanvasParameter(value = "storyboard", positional = false)
    private final Boolean storyboard;
    /** 截图帧号；--frame。 */
    @DreaminaCanvasParameter(value = "frame", positional = false)
    private final Long frame;
    /** 输出长边 1280 或 1920；--long-edge。 */
    @DreaminaCanvasParameter(value = "long-edge", positional = false)
    private final Long longEdge;
    /** 显式确认策略，不替代任何服务端授权。 */
    private final boolean yes;
    @Override
    public DreaminaCanvasCommand getCommand() {
        return DreaminaCanvasCommand.DIRECTOR_PREVIEW;
    }
    @Override
    public Class<DreaminaCanvasDirectorPreviewResult> getDataType() {
        return DreaminaCanvasDirectorPreviewResult.class;
    }
    @Override
    protected DreaminaCanvasArguments arguments() {
        DreaminaCanvasArguments args = new DreaminaCanvasArguments();
        args.flag("file", file);
        args.flag("project-id", projectId);
        args.flag("node-id", nodeId);
        args.flag("output", output);
        args.flag("workspace", workspace);
        args.flag("camera-id", cameraId);
        args.flag("storyboard", storyboard);
        args.flag("frame", frame);
        args.flag("long-edge", longEdge);
        args.yes(yes);
        return args;
    }
}
