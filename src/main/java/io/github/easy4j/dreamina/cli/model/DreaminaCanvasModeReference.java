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
public final class DreaminaCanvasModeReference {
    /**
     * 契约字段 {@code min}。
     */
    @JsonProperty("min")
    private Long min;
    /**
     * 契约字段 {@code max}。
     */
    @JsonProperty("max")
    private Long max;
    /**
     * 契约字段 {@code types}。
     */
    @JsonProperty("types")
    private java.util.List<DreaminaCanvasModeReferenceType> types;
    /**
     * 契约字段 {@code requireAnyOfTypes}。
     */
    @JsonProperty("requireAnyOfTypes")
    private java.util.List<String> requireAnyOfTypes;
}
