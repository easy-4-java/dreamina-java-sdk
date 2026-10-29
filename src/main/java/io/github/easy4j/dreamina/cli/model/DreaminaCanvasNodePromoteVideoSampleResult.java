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
public final class DreaminaCanvasNodePromoteVideoSampleResult {
    /**
     * 契约字段 {@code sourceNodeId}。
     */
    @JsonProperty("sourceNodeId")
    private String sourceNodeId;
    /**
     * 契约字段 {@code sourceResourceId}。
     */
    @JsonProperty("sourceResourceId")
    private String sourceResourceId;
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
     * 契约字段 {@code errorCode}。
     */
    @JsonProperty("errorCode")
    private Long errorCode;
    /**
     * 契约字段 {@code authorizedCredits}。
     */
    @JsonProperty("authorizedCredits")
    private Long authorizedCredits;
    /**
     * 契约字段 {@code totalMaxCredits}。
     */
    @JsonProperty("totalMaxCredits")
    private Long totalMaxCredits;
    /**
     * 契约字段 {@code creditsApproved}。
     */
    @JsonProperty("creditsApproved")
    private Long creditsApproved;
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
     * 契约字段 {@code localStorageWarning}。
     */
    @JsonProperty("localStorageWarning")
    private DreaminaCanvasLocalStorageWarning localStorageWarning;
    /**
     * 契约字段 {@code dryRun}。
     */
    @JsonProperty("dryRun")
    private Boolean dryRun;
    /**
     * 契约字段 {@code validationScope}。
     */
    @JsonProperty("validationScope")
    private String validationScope;
    /**
     * 契约字段 {@code project}。
     */
    @JsonProperty("project")
    private DreaminaCanvasProject project;
    /**
     * 契约字段 {@code submission}。
     */
    @JsonProperty("submission")
    private DreaminaCanvasProject submission;
    /**
     * 契约字段 {@code resolution}。
     */
    @JsonProperty("resolution")
    private String resolution;
    /**
     * 契约字段 {@code creditCeiling}。
     */
    @JsonProperty("creditCeiling")
    private Long creditCeiling;
    /**
     * 契约字段 {@code usesCreditToken}。
     */
    @JsonProperty("usesCreditToken")
    private Boolean usesCreditToken;
    /**
     * 契约字段 {@code sideEffects}。
     */
    @JsonProperty("sideEffects")
    private java.util.List<String> sideEffects;
    /**
     * 契约字段 {@code unverified}。
     */
    @JsonProperty("unverified")
    private java.util.List<String> unverified;
}
