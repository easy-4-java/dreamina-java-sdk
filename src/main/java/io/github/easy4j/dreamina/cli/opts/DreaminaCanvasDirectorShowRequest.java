package io.github.easy4j.dreamina.cli.opts;

import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;
import io.github.easy4j.dreamina.cli.model.DreaminaCanvasDirectorShowResult;
import lombok.Builder;
import lombok.Getter;

/** 查看并校验画布或本地场景；保留官方默认值，仅传显式参数。 */
@Getter
@Builder
public final class DreaminaCanvasDirectorShowRequest extends DreaminaCanvasRequest<DreaminaCanvasDirectorShowResult> {
    /** 本地导演台工作文件；--file。 */
    @DreaminaCanvasParameter(value = "file", positional = false)
    private final String file;
    /** 画布项目 UUID；默认当前项目；--project-id。 */
    @DreaminaCanvasParameter(value = "project-id", positional = false)
    private final String projectId;
    /** 直接操作画布导演台；与 file 互斥；--node-id。 */
    @DreaminaCanvasParameter(value = "node-id", positional = false)
    private final String nodeId;
    /** 显式确认策略，不替代任何服务端授权。 */
    private final boolean yes;
    @Override
    public DreaminaCanvasCommand getCommand() {
        return DreaminaCanvasCommand.DIRECTOR_SHOW;
    }
    @Override
    public Class<DreaminaCanvasDirectorShowResult> getDataType() {
        return DreaminaCanvasDirectorShowResult.class;
    }
    @Override
    protected DreaminaCanvasArguments arguments() {
        DreaminaCanvasArguments args = new DreaminaCanvasArguments();
        args.flag("file", file);
        args.flag("project-id", projectId);
        args.flag("node-id", nodeId);
        args.yes(yes);
        return args;
    }
}
