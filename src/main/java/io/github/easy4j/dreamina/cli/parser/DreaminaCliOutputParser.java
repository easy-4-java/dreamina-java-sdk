package io.github.easy4j.dreamina.cli.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Lightweight regex parsing of Dreamina CLI stdout / stderr.
 * <p>
 * Only extracts fields loosely coupled with downstream orchestration; when any match fails,
 * returns a {@link DreaminaParsedFields} with all fields defaulting to null,
 * and the caller must fall back to the raw strings.
 * </p>
 *
 * @see DreaminaParsedFields
 * @see DreaminaCliStructuredPayloadMapper
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 */
public final class DreaminaCliOutputParser {

    private static final Pattern SUBMIT_ID_PATTERN = Pattern.compile(
        "(?:--submit[_-]?id=|^\\s*submit[_-]?id\\s*[:=]\\s*)(\\S+)",
        Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

    private static final Pattern SUBMIT_ID_ALT = Pattern.compile(
        "\\bsubmit[_-]?id\\b\\s*[:=]\\s*[\"']?([A-Za-z0-9_-]+)", Pattern.CASE_INSENSITIVE);

    /**
     * The {@code "submit_id":"..."} in compact JSON from task submission/query command output (takes priority over loose text matching).
     */
    private static final Pattern SUBMIT_ID_JSON = Pattern.compile(
        "\"submit_id\"\\s*:\\s*\"([^\"]+)\"", Pattern.CASE_INSENSITIVE);

    private static final Pattern CREDIT_PATTERN = Pattern.compile(
        "(?:(?:user[_-]?)?credits?)\\s*[:=]\\s*(\\d+)", Pattern.CASE_INSENSITIVE);

    /**
     * The credit field in JSON returned by commands like {@code user_credit} (whitespace allowed between key and number).
     */
    private static final Pattern CREDIT_TOTAL_JSON = Pattern.compile(
        "\"total_credit\"\\s*:\\s*(\\d+)\\b", Pattern.CASE_INSENSITIVE);

    private static final Pattern POLL_HINT = Pattern.compile(
        "\\b(pending|queued|running)\\b", Pattern.CASE_INSENSITIVE);

    private DreaminaCliOutputParser() {
    }

    /**
     * Merges stdout/stderr and performs regex scanning to extract submitId, credit, and polling hints.
     *
     * @param stdout Standard output
     * @param stderr Standard error
     * @return A non-null snapshot; fields may be null if not recognized
     */
    public static DreaminaParsedFields parseBestEffort(String stdout, String stderr) {
        String a = stdout == null ? "" : stdout;
        String b = stderr == null ? "" : stderr;
        String combined = a + "\n" + b;

        // --- 结构化字段：顺序尝试多种 submit_id / credit 表达方式 ---
        String submitId = findSubmitId(combined);

        Long credit = parseCreditLong(combined);

        Boolean pollRecommended = null;
        if (POLL_HINT.matcher(combined).find()) {
            pollRecommended = true;
        }

        return DreaminaParsedFields.builder()
            .submitId(submitId)
            .credit(credit)
            .pollRecommended(pollRecommended)
            .build();
    }

    /**
     * Attempts to extract a submit ID across multiple patterns.
     */
    private static String findSubmitId(String combined) {
        Matcher json = SUBMIT_ID_JSON.matcher(combined);
        if (json.find()) {
            String id = json.group(1).trim();
            return id.isEmpty() ? null : id;
        }
        Matcher m = SUBMIT_ID_PATTERN.matcher(combined);
        if (m.find()) {
            return trimQuotes(m.group(1));
        }
        Matcher alt = SUBMIT_ID_ALT.matcher(combined);
        if (alt.find()) {
            return trimQuotes(alt.group(1));
        }
        return null;
    }

    /**
     * Parses the credit value from merged output: first matches JSON {@code total_credit}, then falls back to key-value / plain text.
     *
     * @param combined Merged stdout and stderr text
     * @return Non-negative integer on successful parse; {@code null} if unrecognizable
     */
    private static Long parseCreditLong(String combined) {
        Matcher json = CREDIT_TOTAL_JSON.matcher(combined);
        if (json.find()) {
            try {
                return Long.parseLong(json.group(1));
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        Matcher creditMatcher = CREDIT_PATTERN.matcher(combined);
        if (creditMatcher.find()) {
            try {
                return Long.parseLong(creditMatcher.group(1));
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    /**
     * Trims matching leading/trailing quotes to avoid residual JSON / shell-style wrapping.
     */
    private static String trimQuotes(String raw) {
        if (raw == null || raw.isEmpty()) {
            return null;
        }
        String t = raw.trim();
        if (t.length() >= 2 && ((t.startsWith("\"") && t.endsWith("\"")) || (t.startsWith("'") && t.endsWith("'")))) {
            return t.substring(1, t.length() - 1).trim();
        }
        return t.isEmpty() ? null : t;
    }
}
