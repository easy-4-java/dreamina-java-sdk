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
public final class DreaminaCanvasTimelineTrackValueClip {
    /**
     * 契约字段 {@code sourceId}。
     */
    @JsonProperty("sourceId")
    private String sourceId;
    /**
     * 契约字段 {@code sourceKind}。
     */
    @JsonProperty("sourceKind")
    private String sourceKind;
    /**
     * 契约字段 {@code durationTick}。
     */
    @JsonProperty("durationTick")
    private Long durationTick;
    /**
     * 契约字段 {@code sourceRange}。
     */
    @JsonProperty("sourceRange")
    private DreaminaCanvasSourceRange sourceRange;
    /**
     * 契约字段 {@code startTick}。
     */
    @JsonProperty("startTick")
    private Long startTick;
    /**
     * 契约字段 {@code volume}。
     */
    @JsonProperty("volume")
    private java.math.BigDecimal volume;
    /**
     * 契约字段 {@code muted}。
     */
    @JsonProperty("muted")
    private Boolean muted;
    /**
     * 契约字段 {@code speed}。
     */
    @JsonProperty("speed")
    private java.math.BigDecimal speed;
}
