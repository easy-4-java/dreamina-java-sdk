package io.github.easy4j.dreamina.exception;

import io.github.easy4j.dreamina.cli.availability.DreaminaCliAvailabilityReport;
import lombok.Getter;

/**
 * 应用启动阶段 Dreamina CLI 不可用且配置为 fail-fast 时抛出。
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
@Getter
public class DreaminaCliStartupException extends DreaminaCliException {

    private final DreaminaCliAvailabilityReport availabilityReport;

    /**
     * @param message  诊断说明
     * @param report   探测报告
     */
    public DreaminaCliStartupException(String message, DreaminaCliAvailabilityReport report) {
        super(message, report != null ? report.getProbeResult() : null);
        this.availabilityReport = report;
    }
}
