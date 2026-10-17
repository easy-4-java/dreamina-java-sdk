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
public final class DreaminaCanvasNodeShowResultNode {
    /**
     * 契约字段 {@code nodeId}。
     */
    @JsonProperty("nodeId")
    private String nodeId;
    /**
     * 契约字段 {@code result}。
     */
    @JsonProperty("result")
    private String result;
    /**
     * 契约字段 {@code node}。
     */
    @JsonProperty("node")
    private DreaminaCanvasNode node;
    /**
     * 契约字段 {@code errorCode}。
     */
    @JsonProperty("errorCode")
    private Long errorCode;
    /**
     * 契约字段 {@code errmsg}。
     */
    @JsonProperty("errmsg")
    private String errmsg;
}
