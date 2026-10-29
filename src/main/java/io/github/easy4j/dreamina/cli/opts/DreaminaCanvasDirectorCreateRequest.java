package io.github.easy4j.dreamina.cli.opts;

import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;
import io.github.easy4j.dreamina.cli.model.DreaminaCanvasDirectorCreateResult;
import lombok.Builder;
import lombok.Getter;

/** 在画布上创建导演台节点；保留官方默认值，仅传显式参数。 */
@Getter
@Builder
public final class DreaminaCanvasDirectorCreateRequest extends DreaminaCanvasRequest<DreaminaCanvasDirectorCreateResult> {
    /** 另存已绑定画布节点的新工作副本；--file。 */
    @DreaminaCanvasParameter(value = "file", positional = false)
    private final String file;
    /** 画布项目 UUID；默认当前项目；--project-id。 */
    @DreaminaCanvasParameter(value = "project-id", positional = false)
    private final String projectId;
    /** 导演台标题；--title。 */
    @DreaminaCanvasParameter(value = "title", positional = false)
    private final String title;
    /** 显式确认策略，不替代任何服务端授权。 */
    private final boolean yes;
    @Override
    public DreaminaCanvasCommand getCommand() {
        return DreaminaCanvasCommand.DIRECTOR_CREATE;
    }
    @Override
    public Class<DreaminaCanvasDirectorCreateResult> getDataType() {
        return DreaminaCanvasDirectorCreateResult.class;
    }
    @Override
    protected DreaminaCanvasArguments arguments() {
        DreaminaCanvasArguments args = new DreaminaCanvasArguments();
        args.flag("file", file);
        args.flag("project-id", projectId);
        args.flag("title", title);
        args.yes(yes);
        return args;
    }
}
