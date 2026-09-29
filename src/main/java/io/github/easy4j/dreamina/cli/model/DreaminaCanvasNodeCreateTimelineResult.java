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
public final class DreaminaCanvasNodeCreateTimelineResult {
    /**
     * 契约字段 {@code node}。
     */
    @JsonProperty("node")
    private DreaminaCanvasNode node;
    /**
     * 契约字段 {@code mutation}。
     */
    @JsonProperty("mutation")
    private DreaminaCanvasMutation mutation;
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
     * 契约字段 {@code sideEffects}。
     */
    @JsonProperty("sideEffects")
    private java.util.List<String> sideEffects;
    /**
     * 契约字段 {@code plan}。
     */
    @JsonProperty("plan")
    private DreaminaCanvasPlan plan;
    /**
     * 契约字段 {@code unverified}。
     */
    @JsonProperty("unverified")
    private java.util.List<String> unverified;
}
