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
public final class DreaminaCanvasClip {
    /**
     * 契约字段 {@code clipId}。
     */
    @JsonProperty("clipId")
    private String clipId;
    /**
     * 契约字段 {@code kind}。
     */
    @JsonProperty("kind")
    private String kind;
    /**
     * 契约字段 {@code source}。
     */
    @JsonProperty("source")
    private DreaminaCanvasNodeGenerationReference source;
}
