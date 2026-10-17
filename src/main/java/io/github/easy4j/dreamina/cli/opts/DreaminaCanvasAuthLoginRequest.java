package io.github.easy4j.dreamina.cli.opts;

import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;
import io.github.easy4j.dreamina.cli.model.DreaminaCanvasAuthLoginResult;
import lombok.Builder;
import lombok.Getter;

/**
 * 启动设备授权登录。仅显式参数进入命令，不自动运行或批准积分。
 */
@Getter
@Builder
public final class DreaminaCanvasAuthLoginRequest extends DreaminaCanvasRequest<DreaminaCanvasAuthLoginResult> {
    /**
     * 清除当前 profile 登录态后重新授权；--force。
     */
    @DreaminaCanvasParameter(value = "force", positional = false)
    private final Boolean force;
    /**
     * 显式启用确认策略，不替代积分凭证。
     */
    private final boolean yes;

    @Override
    public DreaminaCanvasCommand getCommand() {
        return DreaminaCanvasCommand.AUTH_LOGIN;
    }

    @Override
    public Class<DreaminaCanvasAuthLoginResult> getDataType() {
        return DreaminaCanvasAuthLoginResult.class;
    }

    @Override
    protected DreaminaCanvasArguments arguments() {
        DreaminaCanvasArguments args = new DreaminaCanvasArguments();
        args.flag("force", force);
        args.yes(yes);
        return args;
    }
}
