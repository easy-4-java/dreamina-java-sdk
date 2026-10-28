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
public final class DreaminaCanvasTeamSpaceDiscoveryError {
    /**
     * 契约字段 {@code projectId}。
     */
    @JsonProperty("projectId")
    private String projectId;
    /**
     * 契约字段 {@code localStorage}。
     */
    @JsonProperty("localStorage")
    private DreaminaCanvasLocalStorageWarning localStorage;
    /**
     * 契约字段 {@code code}。
     */
    @JsonProperty("code")
    private String code;
    /**
     * 契约字段 {@code serviceCode}。
     */
    @JsonProperty("serviceCode")
    private Long serviceCode;
    /**
     * 契约字段 {@code class}。
     */
    @JsonProperty("class")
    private String errorClass;
    /**
     * 契约字段 {@code message}。
     */
    @JsonProperty("message")
    private String message;
    /**
     * 契约字段 {@code reason}。
     */
    @JsonProperty("reason")
    private String reason;
    /**
     * 契约字段 {@code retryable}。
     */
    @JsonProperty("retryable")
    private Boolean retryable;
    /**
     * 契约字段 {@code requiredAction}。
     */
    @JsonProperty("requiredAction")
    private String requiredAction;
    /**
     * 契约字段 {@code requestId}。
     */
    @JsonProperty("requestId")
    private String requestId;
    /**
     * 契约字段 {@code traceId}。
     */
    @JsonProperty("traceId")
    private String traceId;
    /**
     * 契约字段 {@code operationRef}。
     */
    @JsonProperty("operationRef")
    private String operationRef;
    /**
     * 契约字段 {@code validation}。
     */
    @JsonProperty("validation")
    private DreaminaCanvasValidation validation;
}
