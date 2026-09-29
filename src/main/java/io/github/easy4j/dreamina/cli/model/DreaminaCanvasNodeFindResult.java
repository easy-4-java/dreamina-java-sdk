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
public final class DreaminaCanvasNodeFindResult {
    /**
     * 契约字段 {@code projectId}。
     */
    @JsonProperty("projectId")
    private String projectId;
    /**
     * 契约字段 {@code items}。
     */
    @JsonProperty("items")
    private java.util.List<DreaminaCanvasNodeFindResultItem> items;
    /**
     * 契约字段 {@code draftVersion}。
     */
    @JsonProperty("draftVersion")
    private Long draftVersion;
    /**
     * 契约字段 {@code total}。
     */
    @JsonProperty("total")
    private Long total;
    /**
     * 契约字段 {@code hasMore}。
     */
    @JsonProperty("hasMore")
    private Boolean hasMore;
    /**
     * 契约字段 {@code offset}。
     */
    @JsonProperty("offset")
    private Long offset;
}
