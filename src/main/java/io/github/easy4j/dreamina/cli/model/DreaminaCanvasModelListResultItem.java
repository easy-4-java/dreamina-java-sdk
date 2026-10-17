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
public final class DreaminaCanvasModelListResultItem {
    /**
     * 契约字段 {@code type}。
     */
    @JsonProperty("type")
    private String type;
    /**
     * 契约字段 {@code model}。
     */
    @JsonProperty("model")
    private String model;
    /**
     * 契约字段 {@code aliases}。
     */
    @JsonProperty("aliases")
    private java.util.List<String> aliases;
    /**
     * 契约字段 {@code modes}。
     */
    @JsonProperty("modes")
    private java.util.List<DreaminaCanvasMode> modes;
}
