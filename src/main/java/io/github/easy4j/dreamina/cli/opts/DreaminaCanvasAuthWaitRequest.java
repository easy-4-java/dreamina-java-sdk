package io.github.easy4j.dreamina.cli.opts;

import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;
import io.github.easy4j.dreamina.cli.model.DreaminaCanvasAuthWaitResult;
import lombok.Builder;
import lombok.Getter;

/**
 * 等待已启动的设备授权完成。仅显式参数进入命令，不自动运行或批准积分。
 */
@Getter
@Builder
public final class DreaminaCanvasAuthWaitRequest extends DreaminaCanvasRequest<DreaminaCanvasAuthWaitResult> {
    /**
     * auth login 返回的设备授权引用；--device-code。
     */
    @DreaminaCanvasParameter(value = "device-code", positional = false)
    private final String deviceCode;
    /**
     * 本次等待登录的最长时间；--timeout。
     */
    @DreaminaCanvasParameter(value = "timeout", positional = false)
    private final java.time.Duration timeout;
    /**
     * 显式启用确认策略，不替代积分凭证。
     */
    private final boolean yes;

    @Override
    public DreaminaCanvasCommand getCommand() {
        return DreaminaCanvasCommand.AUTH_WAIT;
    }

    @Override
    public Class<DreaminaCanvasAuthWaitResult> getDataType() {
        return DreaminaCanvasAuthWaitResult.class;
    }

    @Override
    protected DreaminaCanvasArguments arguments() {
        DreaminaCanvasArguments args = new DreaminaCanvasArguments();
        args.flag("device-code", deviceCode);
        args.flag("timeout", timeout);
        args.yes(yes);
        return args;
    }
}
