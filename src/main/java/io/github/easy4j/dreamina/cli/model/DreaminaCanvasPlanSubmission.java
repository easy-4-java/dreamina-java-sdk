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
public final class DreaminaCanvasPlanSubmission {
    /**
     * 契约字段 {@code identity}。
     */
    @JsonProperty("identity")
    private DreaminaCanvasProject identity;
    /**
     * 契约字段 {@code usesCreditToken}。
     */
    @JsonProperty("usesCreditToken")
    private Boolean usesCreditToken;
    /**
     * 契约字段 {@code creditCeiling}。
     */
    @JsonProperty("creditCeiling")
    private Long creditCeiling;
    /**
     * 契约字段 {@code waitForTerminal}。
     */
    @JsonProperty("waitForTerminal")
    private Boolean waitForTerminal;
    /**
     * 契约字段 {@code timeout}。
     */
    @JsonProperty("timeout")
    private String timeout;
    /**
     * 契约字段 {@code interval}。
     */
    @JsonProperty("interval")
    private String interval;
}
