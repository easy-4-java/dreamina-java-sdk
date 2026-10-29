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
public final class DreaminaCanvasValidationPostEditConstraint {
    /**
     * 契约字段 {@code maxWidth}。
     */
    @JsonProperty("maxWidth")
    private Long maxWidth;
    /**
     * 契约字段 {@code maxHeight}。
     */
    @JsonProperty("maxHeight")
    private Long maxHeight;
    /**
     * 契约字段 {@code maxDurationSeconds}。
     */
    @JsonProperty("maxDurationSeconds")
    private java.math.BigDecimal maxDurationSeconds;
    /**
     * 契约字段 {@code maxFrameCount}。
     */
    @JsonProperty("maxFrameCount")
    private java.math.BigDecimal maxFrameCount;
    /**
     * 契约字段 {@code targetFpsMustExceed}。
     */
    @JsonProperty("targetFpsMustExceed")
    private java.math.BigDecimal targetFpsMustExceed;
    /**
     * 契约字段 {@code targetLongEdgeMustExceed}。
     */
    @JsonProperty("targetLongEdgeMustExceed")
    private Long targetLongEdgeMustExceed;
}
