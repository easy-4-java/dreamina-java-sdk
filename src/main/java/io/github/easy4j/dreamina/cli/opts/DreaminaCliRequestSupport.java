package io.github.easy4j.dreamina.cli.opts;

import io.github.easy4j.dreamina.util.DreaminaStrings;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Common validation and argument assembly support for Dreamina CLI request objects.
 *
 * @see DreaminaCliContractValidator
 * @see DreaminaCliArgumentProvider
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 */
public final class DreaminaCliRequestSupport {

    private DreaminaCliRequestSupport() {
    }

    /**
     * Asserts that a text parameter is not blank.
     *
     * @param value Parameter value
     * @param label Parameter name
     * @return Value with leading/trailing whitespace trimmed
     */
    public static String requireNonBlank(String value, String label) {
        if (DreaminaStrings.isBlank(value)) {
            throw new IllegalArgumentException(label + " must not be blank");
        }
        return value.trim();
    }

    /**
     * Validates a numeric range.
     *
     * @param value Current value
     * @param min   Minimum value
     * @param max   Maximum value
     * @param label 参数名
     */
    public static void requireRange(Integer value, int min, int max, String label) {
        if (value == null) {
            return;
        }
        if (value < min || value > max) {
            throw new IllegalArgumentException(label + " must be in range [" + min + ", " + max + "]");
        }
    }

    /**
     * Validates that a numeric value is non-negative.
     *
     * @param value 参数值
     * @param label 参数名
     */
    public static void requireNonNegative(Integer value, String label) {
        if (value == null) {
            return;
        }
        if (value < 0) {
            throw new IllegalArgumentException(label + " must be non-negative");
        }
    }

    /**
     * Validates a session ID.
     *
     * @param sessionId Session ID
     */
    public static void requireSessionId(Long sessionId) {
        if (sessionId == null) {
            return;
        }
        if (sessionId < 0) {
            throw new IllegalArgumentException("sessionId must be non-negative");
        }
    }

    /**
     * Validates that a local file exists and is readable.
     *
     * @param rawPath File path
     * @param label   参数名
     * @return Normalized path text
     */
    public static String requireReadableFile(String rawPath, String label) {
        String path = requireNonBlank(rawPath, label);
        Path resolved = Paths.get(path);
        if (!Files.exists(resolved)) {
            throw new IllegalArgumentException(label + " does not exist: " + path);
        }
        if (!Files.isRegularFile(resolved)) {
            throw new IllegalArgumentException(label + " is not a file: " + path);
        }
        if (!Files.isReadable(resolved)) {
            throw new IllegalArgumentException(label + " is not readable: " + path);
        }
        return path;
    }

    /**
     * Validates the count and readability of a file list.
     *
     * @param rawPaths File path list
     * @param label    参数名
     * @param minCount Minimum count
     * @param maxCount Maximum count
     * @return Cleaned path list
     */
    public static List<String> requireReadableFiles(List<String> rawPaths, String label, int minCount, int maxCount) {
        if (rawPaths == null || rawPaths.isEmpty()) {
            throw new IllegalArgumentException(label + " must not be empty");
        }
        if (rawPaths.size() < minCount || rawPaths.size() > maxCount) {
            throw new IllegalArgumentException(label + " size must be in range [" + minCount + ", " + maxCount + "]");
        }
        List<String> cleaned = new ArrayList<>(rawPaths.size());
        for (int i = 0; i < rawPaths.size(); i++) {
            cleaned.add(requireReadableFile(rawPaths.get(i), label + "[" + i + "]"));
        }
        return cleaned;
    }

    /**
     * Joins a file list with commas to match Dreamina CLI syntax like `--images=a,b,c`.
     *
     * @param paths Validated path list
     * @return CSV string
     */
    public static String csv(List<String> paths) {
        return String.join(",", paths);
    }


    /**
     * Validates a floating-point numeric range.
     */
    public static void requireDoubleRange(Double value, double min, double max, String label) {
        if (value == null) {
            return;
        }
        if (value < min || value > max) {
            throw new IllegalArgumentException(label + " must be in range [" + min + ", " + max + "]");
        }
    }

    /**
     * Validates video duration (seconds) by model version.
     */
    public static void requireVideoDuration(Integer durationSeconds, DreaminaVideoModelVersion modelVersion, String label) {
        if (durationSeconds == null) {
            return;
        }
        if (modelVersion == null) {
            requireRange(durationSeconds, 3, 15, label);
            return;
        }
        requireRange(durationSeconds, modelVersion.minDurationSeconds(), modelVersion.maxDurationSeconds(), label);
    }

    /**
     * Repeatedly appends the same flag (for CLI stringArray).
     */
    public static void addRepeatedFlag(List<String> args, String key, List<String> values) {
        if (values == null || values.isEmpty()) {
            return;
        }
        for (String value : values) {
            addFlag(args, key, value);
        }
    }

    /**
     * Appends a `--key=value` style parameter.
     *
     * @param args  Argument collection
     * @param key   Parameter name (including `--`)
     * @param value 参数值
     */
    public static void addFlag(List<String> args, String key, String value) {
        if (DreaminaStrings.isBlank(value)) {
            return;
        }
        args.add(key + "=" + value.trim());
    }

    /**
     * Appends an integer flag.
     *
     * @param args  参数集合
     * @param key   参数名
     * @param value 参数值
     */
    public static void addFlag(List<String> args, String key, Integer value) {
        if (value == null) {
            return;
        }
        args.add(key + "=" + value);
    }

    /**
     * Appends a long integer flag.
     *
     * @param args  参数集合
     * @param key   参数名
     * @param value 参数值
     */
    public static void addFlag(List<String> args, String key, Long value) {
        if (value == null) {
            return;
        }
        args.add(key + "=" + value);
    }

    /**
     * Appends caller-defined raw parameters.
     *
     * @param args             Target argument collection
     * @param additionalRawArgs Raw parameters
     */
    /**
     * Appends a double-precision flag.
     */
    public static void addFlag(List<String> args, String key, Double value) {
        if (value == null) {
            return;
        }
        args.add(key + "=" + value);
    }

    public static void addAdditionalArgs(List<String> args, List<String> additionalRawArgs) {
        if (additionalRawArgs == null || additionalRawArgs.isEmpty()) {
            return;
        }
        for (String arg : additionalRawArgs) {
            if (DreaminaStrings.isNotBlank(arg)) {
                args.add(arg.trim());
            }
        }
    }

    /**
     * Copies caller-defined parameters to prevent external list modifications from affecting the result.
     *
     * @param additionalRawArgs 原生参数
     * @return Immutable view
     */
    public static List<String> copyAdditionalArgs(List<String> additionalRawArgs) {
        if (additionalRawArgs == null || additionalRawArgs.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> copied = new ArrayList<>();
        addAdditionalArgs(copied, additionalRawArgs);
        return Collections.unmodifiableList(copied);
    }
}
