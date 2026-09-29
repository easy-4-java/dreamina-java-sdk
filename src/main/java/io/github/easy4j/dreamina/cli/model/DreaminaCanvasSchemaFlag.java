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
public final class DreaminaCanvasSchemaFlag {
    /**
     * 契约字段 {@code name}。
     */
    @JsonProperty("name")
    private String name;
    /**
     * 契约字段 {@code type}。
     */
    @JsonProperty("type")
    private String type;
    /**
     * 契约字段 {@code required}。
     */
    @JsonProperty("required")
    private Boolean required;
    /**
     * 契约字段 {@code default}。
     */
    @JsonProperty("default")
    private String defaultValue;
    /**
     * 契约字段 {@code description}。
     */
    @JsonProperty("description")
    private String description;
    /**
     * 契约字段 {@code constraints}。
     */
    @JsonProperty("constraints")
    private String constraints;
}
