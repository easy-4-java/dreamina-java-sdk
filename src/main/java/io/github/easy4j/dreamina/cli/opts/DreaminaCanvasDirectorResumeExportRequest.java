package io.github.easy4j.dreamina.cli.opts;

import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;
import io.github.easy4j.dreamina.cli.model.DreaminaCanvasDirectorResumeExportResult;
import lombok.Builder;
import lombok.Getter;

/** 恢复中断的画布导出；保留官方默认值，仅传显式参数。 */
@Getter
@Builder
public final class DreaminaCanvasDirectorResumeExportRequest extends DreaminaCanvasRequest<DreaminaCanvasDirectorResumeExportResult> {
    /** 导出恢复记录路径；--operation-ref。 */
    @DreaminaCanvasParameter(value = "operation-ref", positional = false)
    private final String operationRef;
    /** 显式确认策略，不替代任何服务端授权。 */
    private final boolean yes;
    @Override
    public DreaminaCanvasCommand getCommand() {
        return DreaminaCanvasCommand.DIRECTOR_RESUME_EXPORT;
    }
    @Override
    public Class<DreaminaCanvasDirectorResumeExportResult> getDataType() {
        return DreaminaCanvasDirectorResumeExportResult.class;
    }
    @Override
    protected DreaminaCanvasArguments arguments() {
        DreaminaCanvasArguments args = new DreaminaCanvasArguments();
        args.flag("operation-ref", operationRef);
        args.yes(yes);
        return args;
    }
}
