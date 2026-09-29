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
public final class DreaminaCanvasLocalStorageWarning {
    /**
     * 契约字段 {@code store}。
     */
    @JsonProperty("store")
    private String store;
    /**
     * 契约字段 {@code stage}。
     */
    @JsonProperty("stage")
    private String stage;
    /**
     * 契约字段 {@code reason}。
     */
    @JsonProperty("reason")
    private String reason;
    /**
     * 契约字段 {@code directory}。
     */
    @JsonProperty("directory")
    private String directory;
    /**
     * 契约字段 {@code systemCode}。
     */
    @JsonProperty("systemCode")
    private String systemCode;
    /**
     * 契约字段 {@code projectId}。
     */
    @JsonProperty("projectId")
    private String projectId;
    /**
     * 契约字段 {@code submissionPhase}。
     */
    @JsonProperty("submissionPhase")
    private String submissionPhase;
}
