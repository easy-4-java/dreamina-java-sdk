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
public final class DreaminaCanvasVideo {
    /**
     * 契约字段 {@code width}。
     */
    @JsonProperty("width")
    private Long width;
    /**
     * 契约字段 {@code height}。
     */
    @JsonProperty("height")
    private Long height;
    /**
     * 契约字段 {@code durationSeconds}。
     */
    @JsonProperty("durationSeconds")
    private java.math.BigDecimal durationSeconds;
    /**
     * 契约字段 {@code fps}。
     */
    @JsonProperty("fps")
    private java.math.BigDecimal fps;
    /**
     * 契约字段 {@code effectiveFps}。
     */
    @JsonProperty("effectiveFps")
    private java.math.BigDecimal effectiveFps;
    /**
     * 契约字段 {@code resolution}。
     */
    @JsonProperty("resolution")
    private String resolution;
}
