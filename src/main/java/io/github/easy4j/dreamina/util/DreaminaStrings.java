package io.github.easy4j.dreamina.util;

/**
 * String utility class (Java 8 compatible), used as a substitute for {@link String#isBlank()} and other JDK 9+ APIs.
 *
 * @see String#isBlank()
 */
public final class DreaminaStrings {

    private DreaminaStrings() {
    }

    /**
     * Checks whether the value is null, empty, or contains only whitespace (semantically equivalent to {@code String.isBlank()}, compatible with Java 8).
     *
     * @param value The string to check; may be null
     * @return Returns true when there is no meaningful content
     */
    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * The inverse of {@link #isBlank(String)}.
     *
     * @param value 待检测字符串，可为 null
     * @return Returns true when the value contains non-whitespace characters
     */
    public static boolean isNotBlank(String value) {
        return !isBlank(value);
    }
}
