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
public final class DreaminaCanvasNode {
    /**
     * 契约字段 {@code nodeId}。
     */
    @JsonProperty("nodeId")
    private String nodeId;
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
     * 契约字段 {@code description}。
     */
    @JsonProperty("description")
    private String description;
    /**
     * 契约字段 {@code tags}。
     */
    @JsonProperty("tags")
    private java.util.List<String> tags;
    /**
     * 契约字段 {@code text}。
     */
    @JsonProperty("text")
    private String text;
    /**
     * 契约字段 {@code status}。
     */
    @JsonProperty("status")
    private String status;
    /**
     * 契约字段 {@code upstreamNodeIds}。
     */
    @JsonProperty("upstreamNodeIds")
    private java.util.List<String> upstreamNodeIds;
    /**
     * 契约字段 {@code downstreamNodeIds}。
     */
    @JsonProperty("downstreamNodeIds")
    private java.util.List<String> downstreamNodeIds;
    /**
     * 契约字段 {@code executionDependencyNodeIds}。
     */
    @JsonProperty("executionDependencyNodeIds")
    private java.util.List<String> executionDependencyNodeIds;
    /**
     * 契约字段 {@code resources}。
     */
    @JsonProperty("resources")
    private java.util.List<DreaminaCanvasNodeResource> resources;
    /**
     * 契约字段 {@code generation}。
     */
    @JsonProperty("generation")
    private DreaminaCanvasNodeGeneration generation;
    /**
     * 契约字段 {@code subjectBindings}。
     */
    @JsonProperty("subjectBindings")
    private DreaminaCanvasSubjectBinding subjectBindings;
    /**
     * 契约字段 {@code timelineTracks}。
     */
    @JsonProperty("timelineTracks")
    private DreaminaCanvasTimelineTrack timelineTracks;
    /**
     * 契约字段 {@code layout}。
     */
    @JsonProperty("layout")
    private DreaminaCanvasLayout layout;
    /**
     * 契约字段 {@code resourceObservedAtMs}。
     */
    @JsonProperty("resourceObservedAtMs")
    private Long resourceObservedAtMs;
}
