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
public final class DreaminaCanvasValidationPostEdit {
    /**
     * 契约字段 {@code status}。
     */
    @JsonProperty("status")
    private String status;
    /**
     * 契约字段 {@code sourceResourceId}。
     */
    @JsonProperty("sourceResourceId")
    private String sourceResourceId;
    /**
     * 契约字段 {@code observedAtMs}。
     */
    @JsonProperty("observedAtMs")
    private Long observedAtMs;
    /**
     * 契约字段 {@code actual}。
     */
    @JsonProperty("actual")
    private DreaminaCanvasActual actual;
    /**
     * 契约字段 {@code requested}。
     */
    @JsonProperty("requested")
    private DreaminaCanvasNodeUpscaleVideoResultParam requested;
    /**
     * 契约字段 {@code constraints}。
     */
    @JsonProperty("constraints")
    private DreaminaCanvasValidationPostEditConstraint constraints;
    /**
     * 契约字段 {@code eligibleResolutions}。
     */
    @JsonProperty("eligibleResolutions")
    private java.util.List<String> eligibleResolutions;
    /**
     * 契约字段 {@code eligibleFps}。
     */
    @JsonProperty("eligibleFps")
    private java.util.List<Long> eligibleFps;
    /**
     * 契约字段 {@code missingFields}。
     */
    @JsonProperty("missingFields")
    private java.util.List<String> missingFields;
    /**
     * 契约字段 {@code requiredAction}。
     */
    @JsonProperty("requiredAction")
    private String requiredAction;
    /**
     * 契约字段 {@code retryable}。
     */
    @JsonProperty("retryable")
    private Boolean retryable;
}
