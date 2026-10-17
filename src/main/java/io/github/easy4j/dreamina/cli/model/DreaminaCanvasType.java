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
public final class DreaminaCanvasType {
    /**
     * 契约字段 {@code mediaType}。
     */
    @JsonProperty("mediaType")
    private String mediaType;
    /**
     * 契约字段 {@code count}。
     */
    @JsonProperty("count")
    private DreaminaCanvasTotalCount count;
    /**
     * 契约字段 {@code limits}。
     */
    @JsonProperty("limits")
    private DreaminaCanvasLimit limits;
}
