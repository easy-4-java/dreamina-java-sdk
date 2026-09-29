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
public final class DreaminaCanvasParam {
    /**
     * 契约字段 {@code mode}。
     */
    @JsonProperty("mode")
    private String mode;
    /**
     * 契约字段 {@code resolution}。
     */
    @JsonProperty("resolution")
    private String resolution;
    /**
     * 契约字段 {@code detail}。
     */
    @JsonProperty("detail")
    private Long detail;
}
