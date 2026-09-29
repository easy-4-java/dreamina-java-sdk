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
public final class DreaminaCanvasField {
    /**
     * 契约字段 {@code title}。
     */
    @JsonProperty("title")
    private String title;
    /**
     * 契约字段 {@code description}。
     */
    @JsonProperty("description")
    private String description;
    /**
     * 契约字段 {@code tags}。
     */
    @JsonProperty("tags")
    private java.util.List<String> tags;
    /**
     * 契约字段 {@code text}。
     */
    @JsonProperty("text")
    private String text;
    /**
     * 契约字段 {@code clearGeneration}。
     */
    @JsonProperty("clearGeneration")
    private Boolean clearGeneration;
    /**
     * 契约字段 {@code clearDescription}。
     */
    @JsonProperty("clearDescription")
    private Boolean clearDescription;
    /**
     * 契约字段 {@code clearMain}。
     */
    @JsonProperty("clearMain")
    private Boolean clearMain;
    /**
     * 契约字段 {@code clearVoice}。
     */
    @JsonProperty("clearVoice")
    private Boolean clearVoice;
    /**
     * 契约字段 {@code clearAuxiliary}。
     */
    @JsonProperty("clearAuxiliary")
    private Boolean clearAuxiliary;
}
