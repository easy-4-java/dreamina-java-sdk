package io.github.easy4j.dreamina.cli.model;

import lombok.Builder;
import lombok.Getter;

/**
 * Parsed body for {@code dreamina login} / {@code login --headless}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @see io.github.easy4j.dreamina.cli.DreaminaCliExecutor#login()
 * @since 3.0.0
 * @deprecated 旧文本解析 oauthSessionReused/account/device 聚合；替代：AUTH_LOGIN envelope，challenge 可复用 DreaminaDeviceLogin。
 */
@Getter
@Builder
@Deprecated
public class DreaminaLogin {

    private final Boolean oauthSessionReused;
    private final DreaminaLoginAccount account;
    private final DreaminaDeviceLogin device;

    /**
     * @return Whether the local OAuth session was reused and account info was parsed.
     */
    public boolean hasAccount() {
        return account != null;
    }

    /**
     * @return Whether this is only a single-line OAuth-reuse hint (no JSON, no account line).
     */
    public boolean isOAuthReuseOnly() {
        return Boolean.TRUE.equals(oauthSessionReused)
                && account == null
                && (device == null || !device.isMaterialPresent());
    }
}
