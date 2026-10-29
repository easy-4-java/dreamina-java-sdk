package io.github.easy4j.dreamina.cli.opts;

import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;
import io.github.easy4j.dreamina.cli.model.DreaminaCanvasDirectorPushResult;
import lombok.Builder;
import lombok.Getter;

/** 保存正文并原子更新画布版本；保留官方默认值，仅传显式参数。 */
@Getter
@Builder
public final class DreaminaCanvasDirectorPushRequest extends DreaminaCanvasRequest<DreaminaCanvasDirectorPushResult> {
    /** 本地导演台工作文件；--file。 */
    @DreaminaCanvasParameter(value = "file", positional = false)
    private final String file;
    /** 画布项目 UUID；默认当前项目；--project-id。 */
    @DreaminaCanvasParameter(value = "project-id", positional = false)
    private final String projectId;
    /** 画布已有他人保存的较新版本时仍用本文件覆盖；仅在用户明确要求时使用；--force。 */
    @DreaminaCanvasParameter(value = "force", positional = false)
    private final Boolean force;
    /** 显式确认策略，不替代任何服务端授权。 */
    private final boolean yes;
    @Override
    public DreaminaCanvasCommand getCommand() {
        return DreaminaCanvasCommand.DIRECTOR_PUSH;
    }
    @Override
    public Class<DreaminaCanvasDirectorPushResult> getDataType() {
        return DreaminaCanvasDirectorPushResult.class;
    }
    @Override
    protected DreaminaCanvasArguments arguments() {
        DreaminaCanvasArguments args = new DreaminaCanvasArguments();
        args.flag("file", file);
        args.flag("project-id", projectId);
        args.flag("force", force);
        args.yes(yes);
        return args;
    }
}
