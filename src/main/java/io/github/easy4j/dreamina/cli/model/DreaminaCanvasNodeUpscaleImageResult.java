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
public final class DreaminaCanvasNodeUpscaleImageResult {
    /**
     * 契约字段 {@code localStorageWarning}。
     */
    @JsonProperty("localStorageWarning")
    private DreaminaCanvasLocalStorageWarning localStorageWarning;
    /**
     * 契约字段 {@code items}。
     */
    @JsonProperty("items")
    private java.util.List<DreaminaCanvasNodeUpscaleImageResultItem> items;
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
     * 契约字段 {@code params}。
     */
    @JsonProperty("params")
    private DreaminaCanvasParam params;
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
     * 契约字段 {@code waitForTerminal}。
     */
    @JsonProperty("waitForTerminal")
    private Boolean waitForTerminal;
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
