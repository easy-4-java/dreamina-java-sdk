package io.github.easy4j.dreamina.exception;

import io.github.easy4j.dreamina.cli.availability.DreaminaCliAvailabilityReport;
import lombok.Getter;

/**
 * Thrown during application startup when the Dreamina CLI is unavailable and fail-fast is configured.
 *
 * @see io.github.easy4j.dreamina.cli.availability.DreaminaCliAvailabilityReport
 * @see DreaminaCliException
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 */
@Getter
public class DreaminaCliStartupException extends DreaminaCliException {

    private final DreaminaCliAvailabilityReport availabilityReport;

    /**
     * @param message  Diagnostic message
     * @param report   Probe report
     */
    public DreaminaCliStartupException(String message, DreaminaCliAvailabilityReport report) {
        super(message, report != null ? report.getProbeResult() : null);
        this.availabilityReport = report;
    }
}
