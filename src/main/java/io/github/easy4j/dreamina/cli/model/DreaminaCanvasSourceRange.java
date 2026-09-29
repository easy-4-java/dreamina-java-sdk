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
public final class DreaminaCanvasSourceRange {
    /**
     * 契约字段 {@code startTick}。
     */
    @JsonProperty("startTick")
    private Long startTick;
    /**
     * 契约字段 {@code durationTick}。
     */
    @JsonProperty("durationTick")
    private Long durationTick;
}
