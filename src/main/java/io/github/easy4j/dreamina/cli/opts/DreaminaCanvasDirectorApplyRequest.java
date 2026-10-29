package io.github.easy4j.dreamina.cli.opts;

import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;
import io.github.easy4j.dreamina.cli.model.DreaminaCanvasDirectorApplyResult;
import lombok.Builder;
import lombok.Getter;

/** 原子应用场景、镜头、姿势、动画或分镜命令；保留官方默认值，仅传显式参数。 */
@Getter
@Builder
public final class DreaminaCanvasDirectorApplyRequest extends DreaminaCanvasRequest<DreaminaCanvasDirectorApplyResult> {
    /** 本地导演台工作文件；--file。 */
    @DreaminaCanvasParameter(value = "file", positional = false)
    private final String file;
    /** 画布项目 UUID；默认当前项目；--project-id。 */
    @DreaminaCanvasParameter(value = "project-id", positional = false)
    private final String projectId;
    /** 直接操作画布导演台；与 file 互斥；--node-id。 */
    @DreaminaCanvasParameter(value = "node-id", positional = false)
    private final String nodeId;
    /** 有序类型化命令数组 JSON 文件；--input。 */
    @DreaminaCanvasParameter(value = "input", positional = false)
    private final String input;
    /** 显式确认策略，不替代任何服务端授权。 */
    private final boolean yes;
    @Override
    public DreaminaCanvasCommand getCommand() {
        return DreaminaCanvasCommand.DIRECTOR_APPLY;
    }
    @Override
    public Class<DreaminaCanvasDirectorApplyResult> getDataType() {
        return DreaminaCanvasDirectorApplyResult.class;
    }
    @Override
    protected DreaminaCanvasArguments arguments() {
        DreaminaCanvasArguments args = new DreaminaCanvasArguments();
        args.flag("file", file);
        args.flag("project-id", projectId);
        args.flag("node-id", nodeId);
        args.flag("input", input);
        args.yes(yes);
        return args;
    }
}
