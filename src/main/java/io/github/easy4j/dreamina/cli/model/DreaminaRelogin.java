package io.github.easy4j.dreamina.cli.model;

import lombok.Builder;
import lombok.Getter;

/**
 * Parsed body for {@code dreamina relogin}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @see io.github.easy4j.dreamina.cli.DreaminaCliExecutor#relogin()
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
