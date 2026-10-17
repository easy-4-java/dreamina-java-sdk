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
public final class DreaminaCanvasNodeUpscaleImageResultItem {
    /**
     * 契约字段 {@code sourceNodeId}。
     */
    @JsonProperty("sourceNodeId")
    private String sourceNodeId;
    /**
     * 契约字段 {@code submitId}。
     */
    @JsonProperty("submitId")
    private String submitId;
    /**
     * 契约字段 {@code nodeId}。
     */
    @JsonProperty("nodeId")
    private String nodeId;
    /**
     * 契约字段 {@code resourceIds}。
     */
    @JsonProperty("resourceIds")
    private java.util.List<String> resourceIds;
    /**
     * 契约字段 {@code state}。
     */
    @JsonProperty("state")
    private String state;
    /**
     * 契约字段 {@code error}。
     */
    @JsonProperty("error")
    private DreaminaCanvasNodeUpscaleImageResultItemError error;
    /**
     * 契约字段 {@code authorizedCredits}。
     */
    @JsonProperty("authorizedCredits")
    private Long authorizedCredits;
    /**
     * 契约字段 {@code generationState}。
     */
    @JsonProperty("generationState")
    private String generationState;
    /**
     * 契约字段 {@code resources}。
     */
    @JsonProperty("resources")
    private java.util.List<DreaminaCanvasResource> resources;
    /**
     * 契约字段 {@code submission}。
     */
    @JsonProperty("submission")
    private DreaminaCanvasProject submission;
    /**
     * 契约字段 {@code outputTitle}。
     */
    @JsonProperty("outputTitle")
    private String outputTitle;
}
