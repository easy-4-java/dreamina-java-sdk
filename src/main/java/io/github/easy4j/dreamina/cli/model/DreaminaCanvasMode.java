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
public final class DreaminaCanvasMode {
    /**
     * 契约字段 {@code name}。
     */
    @JsonProperty("name")
    private String name;
    /**
     * 契约字段 {@code flags}。
     */
    @JsonProperty("flags")
    private java.util.List<DreaminaCanvasFlag> flags;
    /**
     * 契约字段 {@code references}。
     */
    @JsonProperty("references")
    private DreaminaCanvasModeReference references;
    /**
     * 契约字段 {@code vipRequired}。
     */
    @JsonProperty("vipRequired")
    private Boolean vipRequired;
    /**
     * 契约字段 {@code vipResolutions}。
     */
    @JsonProperty("vipResolutions")
    private java.util.List<String> vipResolutions;
}
