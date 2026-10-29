package io.github.easy4j.dreamina.cli.opts;

import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;
import io.github.easy4j.dreamina.cli.model.DreaminaCanvasDirectorPullResult;
import lombok.Builder;
import lombok.Getter;

/** 读取画布导演台到新文件；保留官方默认值，仅传显式参数。 */
@Getter
@Builder
public final class DreaminaCanvasDirectorPullRequest extends DreaminaCanvasRequest<DreaminaCanvasDirectorPullResult> {
    /** 本地导演台工作文件；--file。 */
    @DreaminaCanvasParameter(value = "file", positional = false)
    private final String file;
    /** 画布项目 UUID；默认当前项目；--project-id。 */
    @DreaminaCanvasParameter(value = "project-id", positional = false)
    private final String projectId;
    /** 导演台节点 ID；--node-id。 */
    @DreaminaCanvasParameter(value = "node-id", positional = false)
    private final String nodeId;
    /** 正文版本；默认当前版本；--version-id。 */
    @DreaminaCanvasParameter(value = "version-id", positional = false)
    private final String versionId;
    /** 显式确认策略，不替代任何服务端授权。 */
    private final boolean yes;
    @Override
    public DreaminaCanvasCommand getCommand() {
        return DreaminaCanvasCommand.DIRECTOR_PULL;
    }
    @Override
    public Class<DreaminaCanvasDirectorPullResult> getDataType() {
        return DreaminaCanvasDirectorPullResult.class;
    }
    @Override
    protected DreaminaCanvasArguments arguments() {
        DreaminaCanvasArguments args = new DreaminaCanvasArguments();
        args.flag("file", file);
        args.flag("project-id", projectId);
        args.flag("node-id", nodeId);
        args.flag("version-id", versionId);
        args.yes(yes);
        return args;
    }
}
