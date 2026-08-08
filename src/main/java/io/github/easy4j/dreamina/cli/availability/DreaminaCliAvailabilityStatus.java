package io.github.easy4j.dreamina.cli.availability;

/**
 * Classification of Dreamina CLI availability probe conclusions.
 *
 * @see DreaminaCliAvailabilityChecker
 * @see DreaminaCliAvailabilityReport
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 */
public enum DreaminaCliAvailabilityStatus {

    /** The {@code dreamina version} probe succeeded; the CLI is available. */
    AVAILABLE,

    /** No executable path or command name is configured. */
    EXECUTABLE_NOT_CONFIGURED,

    /** The configured path does not exist in the file system. */
    EXECUTABLE_NOT_FOUND,

    /** The path exists but is not executable. */
    EXECUTABLE_NOT_EXECUTABLE,

    /** The process could not be started (not found in PATH, insufficient permissions, etc.). */
    SPAWN_FAILED,

    /** The process exited with a non-zero code. */
    NON_ZERO_EXIT,

    /** The probe timed out. */
    TIMEOUT,

    /** Other execution-layer failure. */
    FAILED
}
