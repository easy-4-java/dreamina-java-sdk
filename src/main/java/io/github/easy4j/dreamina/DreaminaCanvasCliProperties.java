package io.github.easy4j.dreamina;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.time.Duration;
import java.util.Objects;

/**
 * Canvas 专用配置，复用原有进程路径、工作目录、超时和并发字段。
 */
@Getter
@Setter
public final class DreaminaCanvasCliProperties extends DreaminaCliProperties {

    private String profile = "default";
    private String region = "cn";
    private int maxOutputBytes = 16 * 1024 * 1024;

    /**
     * 创建 Canvas 默认配置。
     */
    public DreaminaCanvasCliProperties() {
        setExecutable("dreamina-canvas");
        setCommandTimeoutMillis(660000L);
        setMaxConcurrentExecutions(4);
    }

    /**
     * Builder 沿用常用配置表达方式；底层存储复用原配置字段。
     */
    @Builder
    public DreaminaCanvasCliProperties(String executable, String profile, String region, Duration timeout,
                                       Integer maxConcurrentExecutions, Integer maxOutputBytes, String workingDirectory) {
        this();
        if (Objects.nonNull(executable)) {
            setExecutable(executable);
        }
        if (Objects.nonNull(profile)) {
            this.profile = profile;
        }
        if (Objects.nonNull(region)) {
            this.region = region;
        }
        if (Objects.nonNull(timeout)) {
            if (timeout.compareTo(Duration.ofDays(7)) > 0 || timeout.isNegative()) {
                throw new IllegalArgumentException("timeout must be positive and <= 7 days");
            }
            setCommandTimeoutMillis(timeout.toMillis());
        }
        if (Objects.nonNull(maxConcurrentExecutions)) {
            setMaxConcurrentExecutions(maxConcurrentExecutions);
        }
        if (Objects.nonNull(maxOutputBytes)) {
            this.maxOutputBytes = maxOutputBytes;
        }
        setWorkingDirectory(workingDirectory);
        validate();
    }

    /**
     * 校验配置并复制，避免运行中的执行器受调用者后续 setter 影响。
     */
    public static DreaminaCanvasCliProperties copyOf(DreaminaCanvasCliProperties source) {
        Objects.requireNonNull(source, "properties");
        source.validate();
        DreaminaCanvasCliProperties copy = DreaminaCanvasCliProperties.builder()
                .executable(source.getExecutable()).workingDirectory(source.getWorkingDirectory())
                .profile(source.getProfile()).region(source.getRegion()).timeout(source.getTimeout())
                .maxConcurrentExecutions(source.getMaxConcurrentExecutions()).maxOutputBytes(source.getMaxOutputBytes()).build();
        copy.setStartupProbeTimeoutMillis(source.getStartupProbeTimeoutMillis());
        copy.setDefaultPollIntervalSeconds(source.getDefaultPollIntervalSeconds());
        return copy;
    }

    /**
     * 返回执行预算；与原配置 commandTimeoutMillis 为同一份数据。
     */
    public Duration getTimeout() {
        return Duration.ofMillis(getCommandTimeoutMillis());
    }

    private void validate() {
        if (StringUtils.isBlank(getExecutable()) || StringUtils.isBlank(profile) || !"cn".equals(region)) {
            throw new IllegalArgumentException("executable/profile must be nonblank; region must be cn");
        }
        if (getCommandTimeoutMillis() <= 0 || getCommandTimeoutMillis() > Duration.ofDays(7).toMillis()
                || getMaxConcurrentExecutions() < 1 || maxOutputBytes < 1) {
            throw new IllegalArgumentException("timeout must be 1ms..7days; concurrency/output limit must be positive");
        }
    }
}
