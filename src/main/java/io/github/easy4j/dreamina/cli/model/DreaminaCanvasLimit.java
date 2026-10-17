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
public final class DreaminaCanvasLimit {
    /**
     * 契约字段 {@code minDurationMs}。
     */
    @JsonProperty("minDurationMs")
    private Long minDurationMs;
    /**
     * 契约字段 {@code maxDurationMs}。
     */
    @JsonProperty("maxDurationMs")
    private Long maxDurationMs;
    /**
     * 契约字段 {@code maxFileSizeBytes}。
     */
    @JsonProperty("maxFileSizeBytes")
    private Long maxFileSizeBytes;
    /**
     * 契约字段 {@code minWidthPx}。
     */
    @JsonProperty("minWidthPx")
    private Long minWidthPx;
    /**
     * 契约字段 {@code maxWidthPx}。
     */
    @JsonProperty("maxWidthPx")
    private Long maxWidthPx;
    /**
     * 契约字段 {@code minHeightPx}。
     */
    @JsonProperty("minHeightPx")
    private Long minHeightPx;
    /**
     * 契约字段 {@code maxHeightPx}。
     */
    @JsonProperty("maxHeightPx")
    private Long maxHeightPx;
}
