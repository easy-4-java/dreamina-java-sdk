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
public final class DreaminaCanvasPropertie {
    /**
     * 契约字段 {@code name}。
     */
    @JsonProperty("name")
    private String name;
    /**
     * 契约字段 {@code dataType}。
     */
    @JsonProperty("dataType")
    private String dataType;
    /**
     * 契约字段 {@code presence}。
     */
    @JsonProperty("presence")
    private String presence;
    /**
     * 契约字段 {@code rule}。
     */
    @JsonProperty("rule")
    private DreaminaCanvasParameterRule rule;
}
