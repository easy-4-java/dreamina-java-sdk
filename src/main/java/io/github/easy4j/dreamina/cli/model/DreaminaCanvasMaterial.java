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
public final class DreaminaCanvasMaterial {
    /**
     * 契约字段 {@code totalCount}。
     */
    @JsonProperty("totalCount")
    private DreaminaCanvasTotalCount totalCount;
    /**
     * 契约字段 {@code types}。
     */
    @JsonProperty("types")
    private java.util.List<DreaminaCanvasType> types;
    /**
     * 契约字段 {@code referenceRules}。
     */
    @JsonProperty("referenceRules")
    private DreaminaCanvasReferenceRule referenceRules;
    /**
     * 契约字段 {@code maxTotalVideoDurationMs}。
     */
    @JsonProperty("maxTotalVideoDurationMs")
    private Long maxTotalVideoDurationMs;
    /**
     * 契约字段 {@code maxTotalAudioDurationMs}。
     */
    @JsonProperty("maxTotalAudioDurationMs")
    private Long maxTotalAudioDurationMs;
}
