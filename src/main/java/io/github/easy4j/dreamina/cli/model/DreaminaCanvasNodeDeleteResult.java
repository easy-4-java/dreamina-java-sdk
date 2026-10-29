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
public final class DreaminaCanvasNodeDeleteResult {
    /**
     * 契约字段 {@code localStorageWarning}。
     */
    @JsonProperty("localStorageWarning")
    private DreaminaCanvasLocalStorageWarning localStorageWarning;
    /**
     * 契约字段 {@code nodeIds}。
     */
    @JsonProperty("nodeIds")
    private java.util.List<String> nodeIds;
    /**
     * 契约字段 {@code updateId}。
     */
    @JsonProperty("updateId")
    private String updateId;
    /**
     * 契约字段 {@code savedDraftVersion}。
     */
    @JsonProperty("savedDraftVersion")
    private Long savedDraftVersion;
    /**
     * 契约字段 {@code idempotentReplay}。
     */
    @JsonProperty("idempotentReplay")
    private Boolean idempotentReplay;
}
