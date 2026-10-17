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
public final class DreaminaCanvasMutation {
    /**
     * 契约字段 {@code action}。
     */
    @JsonProperty("action")
    private String action;
    /**
     * 契约字段 {@code savedDraftVersion}。
     */
    @JsonProperty("savedDraftVersion")
    private Long savedDraftVersion;
    /**
     * 契约字段 {@code effectiveGeneration}。
     */
    @JsonProperty("effectiveGeneration")
    private DreaminaCanvasEffectiveGeneration effectiveGeneration;
    /**
     * 契约字段 {@code generationDiff}。
     */
    @JsonProperty("generationDiff")
    private java.util.List<DreaminaCanvasGenerationDiff> generationDiff;
}
