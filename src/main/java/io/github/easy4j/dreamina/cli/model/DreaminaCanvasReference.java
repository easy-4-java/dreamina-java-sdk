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
public final class DreaminaCanvasReference {
    /**
     * 契约字段 {@code index}。
     */
    @JsonProperty("index")
    private Long index;
    /**
     * 契约字段 {@code type}。
     */
    @JsonProperty("type")
    private String type;
    /**
     * 契约字段 {@code id}。
     */
    @JsonProperty("id")
    private String id;
    /**
     * 契约字段 {@code resolvedFrom}。
     */
    @JsonProperty("resolvedFrom")
    private String resolvedFrom;
    /**
     * 契约字段 {@code role}。
     */
    @JsonProperty("role")
    private String role;
}
