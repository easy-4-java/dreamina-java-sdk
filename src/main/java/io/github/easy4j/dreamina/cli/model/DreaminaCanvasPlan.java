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
public final class DreaminaCanvasPlan {
    /**
     * 契约字段 {@code action}。
     */
    @JsonProperty("action")
    private String action;
    /**
     * 契约字段 {@code nodeType}。
     */
    @JsonProperty("nodeType")
    private String nodeType;
    /**
     * 契约字段 {@code project}。
     */
    @JsonProperty("project")
    private DreaminaCanvasProject project;
    /**
     * 契约字段 {@code node}。
     */
    @JsonProperty("node")
    private DreaminaCanvasProject node;
    /**
     * 契约字段 {@code update}。
     */
    @JsonProperty("update")
    private DreaminaCanvasProject update;
    /**
     * 契约字段 {@code steps}。
     */
    @JsonProperty("steps")
    private java.util.List<String> steps;
    /**
     * 契约字段 {@code fields}。
     */
    @JsonProperty("fields")
    private DreaminaCanvasField fields;
    /**
     * 契约字段 {@code generation}。
     */
    @JsonProperty("generation")
    private DreaminaCanvasPlanGeneration generation;
    /**
     * 契约字段 {@code references}。
     */
    @JsonProperty("references")
    private java.util.List<DreaminaCanvasReference> references;
    /**
     * 契约字段 {@code bindings}。
     */
    @JsonProperty("bindings")
    private java.util.List<DreaminaCanvasBinding> bindings;
    /**
     * 契约字段 {@code timeline}。
     */
    @JsonProperty("timeline")
    private DreaminaCanvasTimeline timeline;
    /**
     * 契约字段 {@code importKind}。
     */
    @JsonProperty("importKind")
    private String importKind;
    /**
     * 契约字段 {@code initialPosition}。
     */
    @JsonProperty("initialPosition")
    private DreaminaCanvasInitialPosition initialPosition;
    /**
     * 契约字段 {@code submission}。
     */
    @JsonProperty("submission")
    private DreaminaCanvasPlanSubmission submission;
}
