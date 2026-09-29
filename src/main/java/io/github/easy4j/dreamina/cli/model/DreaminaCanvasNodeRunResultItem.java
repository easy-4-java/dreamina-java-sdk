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
public final class DreaminaCanvasNodeRunResultItem {
    /**
     * 契约字段 {@code nodeId}。
     */
    @JsonProperty("nodeId")
    private String nodeId;
    /**
     * 契约字段 {@code submitId}。
     */
    @JsonProperty("submitId")
    private String submitId;
    /**
     * 契约字段 {@code state}。
     */
    @JsonProperty("state")
    private String state;
    /**
     * 契约字段 {@code resources}。
     */
    @JsonProperty("resources")
    private java.util.List<DreaminaCanvasSubmissionResource> resources;
    /**
     * 契约字段 {@code error}。
     */
    @JsonProperty("error")
    private DreaminaCanvasServiceError error;
    /**
     * 契约字段 {@code generationDiff}。
     */
    @JsonProperty("generationDiff")
    private java.util.List<DreaminaCanvasGenerationDiff> generationDiff;
}
