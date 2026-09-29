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
public final class DreaminaCanvasNodeQuoteResult {
    /**
     * 契约字段 {@code items}。
     */
    @JsonProperty("items")
    private java.util.List<DreaminaCanvasNodeQuoteResultItem> items;
    /**
     * 契约字段 {@code totalMaxCredits}。
     */
    @JsonProperty("totalMaxCredits")
    private Long totalMaxCredits;
    /**
     * 契约字段 {@code draftVersion}。
     */
    @JsonProperty("draftVersion")
    private Long draftVersion;
    /**
     * 契约字段 {@code confirmable}。
     */
    @JsonProperty("confirmable")
    private Boolean confirmable;
    /**
     * 契约字段 {@code confirmationRequired}。
     */
    @JsonProperty("confirmationRequired")
    private Boolean confirmationRequired;
}
