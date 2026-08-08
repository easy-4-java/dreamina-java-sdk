package io.github.easy4j.dreamina.cli.model;

import lombok.Data;

/**
 * Account summary output when {@code dreamina login} reuses the local OAuth session (key-value text, not JSON).
 * <p>
 * 典型 CLI 片段：
 * </p>
 * <pre>
 * 已复用当前本地 OAuth 登录态。
 * 当前登录账户信息：
 * user_id: 1552973852847448
 * vip_level: maestro
 * total_credit: 4391
 * </pre>
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 */
@Data
public class DreaminaLoginAccount {

    /**
     * Numeric user ID of the currently logged-in user.
     */
    private Long userId;

    /**
     * VIP tier (e.g., {@code maestro}).
     */
    private String vipLevel;

    /**
     * Remaining credits.
     */
    private Long totalCredit;
}
