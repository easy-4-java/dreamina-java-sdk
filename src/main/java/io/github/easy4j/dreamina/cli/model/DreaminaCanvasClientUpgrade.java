package io.github.easy4j.dreamina.cli.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 画布 CLI 契约对象；由 scripts/generate_canvas_types.py 维护。
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public final class DreaminaCanvasClientUpgrade {
    /**
     * 契约字段 {@code installBaseUrl}。
     */
    @JsonProperty("installBaseUrl")
    private String installBaseUrl;
    /**
     * 契约字段 {@code currentVersion}。
     */
    @JsonProperty("currentVersion")
    private String currentVersion;
    /**
     * 契约字段 {@code minimumSupportedVersion}。
     */
    @JsonProperty("minimumSupportedVersion")
    private String minimumSupportedVersion;
    /**
     * 契约字段 {@code upgradeUrl}。
     */
    @JsonProperty("upgradeUrl")
    private String upgradeUrl;
    /**
     * 契约字段 {@code installCommands}。
     */
    @JsonProperty("installCommands")
    private java.util.List<DreaminaCanvasInstallCommand> installCommands;
}
