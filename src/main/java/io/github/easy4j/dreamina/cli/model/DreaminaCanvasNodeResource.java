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
public final class DreaminaCanvasNodeResource {
    /**
     * 契约字段 {@code resourceId}。
     */
    @JsonProperty("resourceId")
    private String resourceId;
    /**
     * 契约字段 {@code type}。
     */
    @JsonProperty("type")
    private String type;
    /**
     * 契约字段 {@code status}。
     */
    @JsonProperty("status")
    private String status;
    /**
     * 契约字段 {@code name}。
     */
    @JsonProperty("name")
    private String name;
    /**
     * 契约字段 {@code errorCode}。
     */
    @JsonProperty("errorCode")
    private Long errorCode;
    /**
     * 契约字段 {@code submitId}。
     */
    @JsonProperty("submitId")
    private String submitId;
    /**
     * 契约字段 {@code createdAt}。
     */
    @JsonProperty("createdAt")
    private Long createdAt;
    /**
     * 契约字段 {@code canRegenerate}。
     */
    @JsonProperty("canRegenerate")
    private Boolean canRegenerate;
    /**
     * 契约字段 {@code source}。
     */
    @JsonProperty("source")
    private String source;
    /**
     * 契约字段 {@code creditsAmount}。
     */
    @JsonProperty("creditsAmount")
    private Long creditsAmount;
}
