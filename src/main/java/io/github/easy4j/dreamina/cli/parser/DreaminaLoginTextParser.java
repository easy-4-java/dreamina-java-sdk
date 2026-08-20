package io.github.easy4j.dreamina.cli.parser;

import io.github.easy4j.dreamina.cli.model.DreaminaDeviceLogin;
import io.github.easy4j.dreamina.cli.model.DreaminaLoginAccount;
import io.github.easy4j.dreamina.util.DreaminaStrings;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses the plain-text output of {@code dreamina login} / {@code relogin} / {@code logout}.
 *
 * @see DreaminaCliStructuredPayloadMapper#mapLogin(io.github.easy4j.dreamina.cli.DreaminaCliResult)
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 */
public final class DreaminaLoginTextParser {

    private static final Pattern KV_LINE = Pattern.compile(
        "^(?<key>[a-z_][a-z0-9_]*)\\s*:\\s*(?<value>.+)$",
        Pattern.CASE_INSENSITIVE);

    private DreaminaLoginTextParser() {
    }

    /**
     * Detects whether the text contains local OAuth session reuse semantics (Chinese/English).
     *
     * @param combined Combined stdout/stderr text
     * @return Returns true if reuse semantics are detected
     */
    public static boolean detectsOAuthReuse(String combined) {
        if (DreaminaStrings.isBlank(combined)) {
            return false;
        }
        if (combined.contains("复用") && combined.contains("登录")) {
            return true;
        }
        String lower = combined.toLowerCase(Locale.ROOT);
        return (lower.contains("reuse") && lower.contains("oauth"))
            || lower.contains("already logged")
            || lower.contains("still valid");
    }

    /**
     * Detects whether the text contains local login state cleared semantics.
     *
     * @param combined Combined text
     * @return Returns true if a successful logout message is detected
     */
    public static boolean detectsLogoutCleared(String combined) {
        if (DreaminaStrings.isBlank(combined)) {
            return false;
        }
        return combined.contains("已清除") && combined.contains("登录态");
    }

    /**
     * Detects whether the text indicates a browser-based Device Flow is required (e.g., first line of {@code relogin}).
     *
     * @param combined 合并文本
     * @return Returns true if browser-based OAuth is required
     */
    public static boolean detectsDeviceFlowBrowserPrompt(String combined) {
        if (DreaminaStrings.isBlank(combined)) {
            return false;
        }
        return combined.contains("OAuth Device Flow")
            || combined.contains("请使用浏览器");
    }

    /**
     * Parses the account summary from the key-value paragraph about current login account info.
     *
     * @param combined CLI combined text
     * @return Returns the object if at least one field was parsed, otherwise null
     */
    public static DreaminaLoginAccount parseReusedAccount(String combined) {
        if (DreaminaStrings.isBlank(combined)) {
            return null;
        }
        DreaminaLoginAccount payload = new DreaminaLoginAccount();
        boolean any = false;
        for (String line : combined.split("\\R")) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            Matcher m = KV_LINE.matcher(trimmed);
            if (!m.matches()) {
                continue;
            }
            String key = m.group("key").toLowerCase(Locale.ROOT);
            String value = m.group("value").trim();
            switch (key) {
                case "user_id": {
                    Long uid = parseLong(value);
                    if (uid != null) {
                        payload.setUserId(uid);
                        any = true;
                    }
                    break;
                }
                case "vip_level":
                    payload.setVipLevel(value);
                    any = true;
                    break;
                case "total_credit": {
                    Long credit = parseLong(value);
                    if (credit != null) {
                        payload.setTotalCredit(credit);
                        any = true;
                    }
                    break;
                }
                default:
                    // 账户段仅识别上述键
                    break;
            }
        }
        return any ? payload : null;
    }

    /**
     * Parses Device Flow material from key-value text ({@code relogin} / some {@code --headless} scenarios).
     *
     * @param combined CLI 合并文本
     * @return Returns the object if it contains at least one of device_code / verification_uri / user_code, otherwise null
     */
    public static DreaminaDeviceLogin parseDeviceFlow(String combined) {
        if (DreaminaStrings.isBlank(combined)) {
            return null;
        }
        DreaminaDeviceLogin payload = new DreaminaDeviceLogin();
        boolean any = false;
        for (String line : combined.split("\\R")) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            Matcher m = KV_LINE.matcher(trimmed);
            if (!m.matches()) {
                continue;
            }
            String key = m.group("key").toLowerCase(Locale.ROOT);
            String value = m.group("value").trim();
            switch (key) {
                case "device_code":
                    payload.setDeviceCode(value);
                    any = true;
                    break;
                case "verification_uri":
                    payload.setVerificationUri(value);
                    any = true;
                    break;
                case "user_code":
                    payload.setUserCode(value);
                    any = true;
                    break;
                case "poll_interval":
                    payload.setPollInterval(value);
                    any = true;
                    break;
                case "expires_at":
                    payload.setExpiresAt(value);
                    any = true;
                    break;
                default:
                    // 忽略无关键（如 user_id 由 parseReusedAccount 处理）
                    break;
            }
        }
        return any ? payload : null;
    }

    /**
     * Whether the Device Flow payload contains core fields.
     *
     * @param payload Payload; may be null
     * @return Returns true if the material is present
     */
    public static boolean hasDeviceFlowMaterial(DreaminaDeviceLogin payload) {
        if (payload == null) {
            return false;
        }
        return DreaminaStrings.isNotBlank(payload.getDeviceCode())
            || DreaminaStrings.isNotBlank(payload.getVerificationUri())
            || DreaminaStrings.isNotBlank(payload.getUserCode());
    }

    private static Long parseLong(String value) {
        if (DreaminaStrings.isBlank(value)) {
            return null;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException ignored) {
            return null;
        }
    }
}
