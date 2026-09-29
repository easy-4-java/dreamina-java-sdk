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
public final class DreaminaCanvasOperationWaitResult {
    /**
     * 契约字段 {@code localStorageWarning}。
     */
    @JsonProperty("localStorageWarning")
    private DreaminaCanvasLocalStorageWarning localStorageWarning;
    /**
     * 契约字段 {@code operationRef}。
     */
    @JsonProperty("operationRef")
    private String operationRef;
    /**
     * 契约字段 {@code state}。
     */
    @JsonProperty("state")
    private String state;
    /**
     * 契约字段 {@code resources}。
     */
    @JsonProperty("resources")
    private java.util.List<DreaminaCanvasResource> resources;
    /**
     * 契约字段 {@code submission}。
     */
    @JsonProperty("submission")
    private DreaminaCanvasOperationStatusResultSubmission submission;
    /**
     * 契约字段 {@code submissionError}。
     */
    @JsonProperty("submissionError")
    private DreaminaCanvasSubmissionError submissionError;
    /**
     * 契约字段 {@code awaitingConfirmation}。
     */
    @JsonProperty("awaitingConfirmation")
    private DreaminaCanvasAwaitingConfirmation awaitingConfirmation;
}
