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
public final class DreaminaCanvasNodeGenerationReference {
    /**
     * 契约字段 {@code kind}。
     */
    @JsonProperty("kind")
    private String kind;
    /**
     * 契约字段 {@code id}。
     */
    @JsonProperty("id")
    private String id;
    /**
     * 契约字段 {@code type}。
     */
    @JsonProperty("type")
    private String type;
    /**
     * 契约字段 {@code title}。
     */
    @JsonProperty("title")
    private String title;
    /**
     * 契约字段 {@code writebackSupported}。
     */
    @JsonProperty("writebackSupported")
    private Boolean writebackSupported;
    /**
     * 契约字段 {@code unavailableCode}。
     */
    @JsonProperty("unavailableCode")
    private String unavailableCode;
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
     * 契约字段 {@code resource}。
     */
    @JsonProperty("resource")
    private DreaminaCanvasNodeResource resource;
}
