package io.github.easy4j.dreamina.cli.parser;

/**
 * Extracts usable JSON fragments (object or array) from Dreamina CLI output mixed with log lines.
 * <p>
 * Typical scenario: stderr prints an initialization warning while stdout is still compact JSON;
 * some commands may also embed JSON after multi-line logs. This utility uses bracket-pair scanning
 * to avoid accidentally consuming brackets inside string literals.
 * </p>
 *
 * @see DreaminaCliStructuredPayloadMapper
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 */
final class DreaminaCliJsonExtract {

    private DreaminaCliJsonExtract() {
    }

    /**
     * Finds the first syntactically balanced JSON segment ({@code {...}} or {@code [...]}) in the combined text.
     *
     * @param combined Combined stdout/stderr text; may be null
     * @return Candidate JSON substring; returns {@code null} if not found
     */
    static String extractFirstBalancedJson(String combined) {
        if (combined == null || combined.isEmpty()) {
            return null;
        }
        int obj = combined.indexOf('{');
        int arr = combined.indexOf('[');
        int start = -1;
        char open = 0;
        if (obj < 0 && arr < 0) {
            return null;
        }
        if (obj < 0) {
            start = arr;
            open = '[';
        } else if (arr < 0) {
            start = obj;
            open = '{';
        } else {
            start = Math.min(obj, arr);
            open = combined.charAt(start);
        }
        char close = open == '{' ? '}' : ']';
        int end = findClosingIndex(combined, start, open, close);
        if (end <= start) {
            return null;
        }
        return combined.substring(start, end + 1).trim();
    }

    /**
     * Scans from the opening bracket at {@code start} to find the matching closing bracket inclusive index.
     *
     * @param text      The full text
     * @param start     Index of {@code '{'} or {@code '['}
     * @param openChar  Opening bracket character
     * @param closeChar Closing bracket character
     * @return Closing bracket index; returns {@code -1} on failure
     */
    private static int findClosingIndex(String text, int start, char openChar, char closeChar) {
        int depth = 0;
        boolean inString = false;
        boolean escape = false;
        for (int i = start; i < text.length(); i++) {
            char c = text.charAt(i);
            if (inString) {
                if (escape) {
                    escape = false;
                } else if (c == '\\') {
                    escape = true;
                } else if (c == '"') {
                    inString = false;
                }
                continue;
            }
            if (c == '"') {
                inString = true;
                continue;
            }
            if (c == openChar) {
                depth++;
            } else if (c == closeChar) {
                depth--;
                if (depth == 0) {
                    return i;
                }
            }
        }
        return -1;
    }
}
