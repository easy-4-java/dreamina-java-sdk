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
public final class DreaminaCanvasParameterRule {
    /**
     * 契约字段 {@code kind}。
     */
    @JsonProperty("kind")
    private String kind;
    /**
     * 契约字段 {@code stringValues}。
     */
    @JsonProperty("stringValues")
    private java.util.List<String> stringValues;
    /**
     * 契约字段 {@code integerValues}。
     */
    @JsonProperty("integerValues")
    private java.util.List<Long> integerValues;
    /**
     * 契约字段 {@code numberValues}。
     */
    @JsonProperty("numberValues")
    private java.util.List<java.math.BigDecimal> numberValues;
    /**
     * 契约字段 {@code booleanValues}。
     */
    @JsonProperty("booleanValues")
    private java.util.List<Boolean> booleanValues;
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
    /**
     * 契约字段 {@code format}。
     */
    @JsonProperty("format")
    private String format;
    /**
     * 契约字段 {@code minItems}。
     */
    @JsonProperty("minItems")
    private Long minItems;
    /**
     * 契约字段 {@code maxItems}。
     */
    @JsonProperty("maxItems")
    private Long maxItems;
    /**
     * 契约字段 {@code unique}。
     */
    @JsonProperty("unique")
    private Boolean unique;
    /**
     * 契约字段 {@code items}。
     */
    @JsonProperty("items")
    private DreaminaCanvasItem items;
    /**
     * 契约字段 {@code properties}。
     */
    @JsonProperty("properties")
    private java.util.List<DreaminaCanvasPropertie> properties;
    /**
     * 契约字段 {@code additionalProperties}。
     */
    @JsonProperty("additionalProperties")
    private Boolean additionalProperties;
}
