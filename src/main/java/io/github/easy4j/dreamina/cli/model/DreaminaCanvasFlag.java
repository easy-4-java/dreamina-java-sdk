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
public final class DreaminaCanvasFlag {
    /**
     * 契约字段 {@code flag}。
     */
    @JsonProperty("flag")
    private String flag;
    /**
     * 契约字段 {@code required}。
     */
    @JsonProperty("required")
    private Boolean required;
    /**
     * 契约字段 {@code values}。
     */
    @JsonProperty("values")
    private java.util.List<String> values;
    /**
     * 契约字段 {@code min}。
     */
    @JsonProperty("min")
    private java.math.BigDecimal min;
    /**
     * 契约字段 {@code max}。
     */
    @JsonProperty("max")
    private java.math.BigDecimal max;
    /**
     * 契约字段 {@code step}。
     */
    @JsonProperty("step")
    private java.math.BigDecimal step;
    /**
     * 契约字段 {@code minLength}。
     */
    @JsonProperty("minLength")
    private Long minLength;
    /**
     * 契约字段 {@code maxLength}。
     */
    @JsonProperty("maxLength")
    private Long maxLength;
}
