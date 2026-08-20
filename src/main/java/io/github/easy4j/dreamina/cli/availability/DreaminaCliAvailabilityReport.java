package io.github.easy4j.dreamina.cli.availability;

import io.github.easy4j.dreamina.cli.DreaminaCliResult;
import lombok.Builder;
import lombok.Getter;

/**
 * Dreamina CLI startup/readiness probe result (pure SDK, no Spring dependency).
 *
 * @see DreaminaCliAvailabilityChecker
 * @see DreaminaCliAvailabilityStatus
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 */
@Getter
@Builder
public class DreaminaCliAvailabilityReport {

    private final DreaminaCliAvailabilityStatus status;
    private final boolean available;
    private final String configuredExecutable;
    private final String resolvedExecutablePath;
    private final String message;
    private final DreaminaCliResult probeResult;

    /**
     * @return Whether it is safe to invoke {@code dreamina} subcommands.
     */
    public boolean isAvailable() {
        return available;
    }

    /**
     * Constructs a diagnostic message for logging or exceptions.
     *
     * @return 单行或多行说明
     */
    public String toDiagnosticMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("Dreamina CLI ");
        sb.append(available ? "ready" : "unavailable");
        sb.append(" [").append(status).append(']');
        if (configuredExecutable != null) {
            sb.append(" executable=").append(configuredExecutable);
        }
        if (resolvedExecutablePath != null) {
            sb.append(" resolved=").append(resolvedExecutablePath);
        }
        if (message != null && !message.isEmpty()) {
            sb.append(" — ").append(message);
        }
        return sb.toString();
    }
}
