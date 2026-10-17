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
public final class DreaminaCanvasModelResult {
    /**
     * 契约字段 {@code query}。
     */
    @JsonProperty("query")
    private String query;
    /**
     * 契约字段 {@code matchKind}。
     */
    @JsonProperty("matchKind")
    private String matchKind;
    /**
     * 契约字段 {@code matches}。
     */
    @JsonProperty("matches")
    private java.util.List<DreaminaCanvasMatche> matches;
}
