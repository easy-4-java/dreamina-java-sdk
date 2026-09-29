package io.github.easy4j.dreamina.cli.opts;

import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;
import io.github.easy4j.dreamina.cli.model.DreaminaLoginAccount;
import lombok.Builder;
import lombok.Getter;

/**
 * 读取服务端认证的当前账号。仅显式参数进入命令，不自动运行或批准积分。
 */
@Getter
@Builder
public final class DreaminaCanvasAuthAccountRequest extends DreaminaCanvasRequest<DreaminaLoginAccount> {
    /**
     * 显式启用确认策略，不替代积分凭证。
     */
    private final boolean yes;

    @Override
    public DreaminaCanvasCommand getCommand() {
        return DreaminaCanvasCommand.AUTH_ACCOUNT;
    }

    @Override
    public Class<DreaminaLoginAccount> getDataType() {
        return DreaminaLoginAccount.class;
    }

    @Override
    protected DreaminaCanvasArguments arguments() {
        DreaminaCanvasArguments args = new DreaminaCanvasArguments();
        args.yes(yes);
        return args;
    }
}
