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
public final class DreaminaCanvasLayout {
    /**
     * 契约字段 {@code x}。
     */
    @JsonProperty("x")
    private java.math.BigDecimal x;
    /**
     * 契约字段 {@code y}。
     */
    @JsonProperty("y")
    private java.math.BigDecimal y;
    /**
     * 契约字段 {@code width}。
     */
    @JsonProperty("width")
    private java.math.BigDecimal width;
    /**
     * 契约字段 {@code height}。
     */
    @JsonProperty("height")
    private java.math.BigDecimal height;
    /**
     * 契约字段 {@code zIndex}。
     */
    @JsonProperty("zIndex")
    private java.math.BigDecimal zIndex;
}
