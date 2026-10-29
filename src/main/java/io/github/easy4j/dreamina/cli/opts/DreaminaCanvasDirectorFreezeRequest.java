package io.github.easy4j.dreamina.cli.opts;

import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;
import io.github.easy4j.dreamina.cli.model.DreaminaCanvasDirectorFreezeResult;
import lombok.Builder;
import lombok.Getter;

/** 把动画帧定格到摆拍（需本机浏览器）；保留官方默认值，仅传显式参数。 */
@Getter
@Builder
public final class DreaminaCanvasDirectorFreezeRequest extends DreaminaCanvasRequest<DreaminaCanvasDirectorFreezeResult> {
    /** 本地导演台工作文件；--file。 */
    @DreaminaCanvasParameter(value = "file", positional = false)
    private final String file;
    /** 定格帧号；--frame。 */
    @DreaminaCanvasParameter(value = "frame", positional = false)
    private final Long frame;
    /** 显式确认策略，不替代任何服务端授权。 */
    private final boolean yes;
    @Override
    public DreaminaCanvasCommand getCommand() {
        return DreaminaCanvasCommand.DIRECTOR_FREEZE;
    }
    @Override
    public Class<DreaminaCanvasDirectorFreezeResult> getDataType() {
        return DreaminaCanvasDirectorFreezeResult.class;
    }
    @Override
    protected DreaminaCanvasArguments arguments() {
        DreaminaCanvasArguments args = new DreaminaCanvasArguments();
        args.flag("file", file);
        args.flag("frame", frame);
        args.yes(yes);
        return args;
    }
}
