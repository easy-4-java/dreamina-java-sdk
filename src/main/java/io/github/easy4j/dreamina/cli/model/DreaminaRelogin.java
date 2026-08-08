package io.github.easy4j.dreamina.cli.model;

import lombok.Builder;
import lombok.Getter;

/**
 * Parsed body for {@code dreamina relogin}.
 *
 * @see io.github.easy4j.dreamina.cli.DreaminaCliExecutor#relogin()
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 */
@Getter
@Builder
public class DreaminaRelogin {

    private final Boolean requiresBrowserOAuth;
    private final DreaminaDeviceLogin device;

    /**
     * @return Whether {@code checklogin} should follow.
     */
    public boolean needsCheckLogin() {
        return device != null && device.isMaterialPresent();
    }
}
