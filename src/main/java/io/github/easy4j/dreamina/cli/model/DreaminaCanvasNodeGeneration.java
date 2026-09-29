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
public final class DreaminaCanvasNodeGeneration {
    /**
     * 契约字段 {@code mode}。
     */
    @JsonProperty("mode")
    private String mode;
    /**
     * 契约字段 {@code prompt}。
     */
    @JsonProperty("prompt")
    private String prompt;
    /**
     * 契约字段 {@code model}。
     */
    @JsonProperty("model")
    private String model;
    /**
     * 契约字段 {@code ratio}。
     */
    @JsonProperty("ratio")
    private String ratio;
    /**
     * 契约字段 {@code resolution}。
     */
    @JsonProperty("resolution")
    private String resolution;
    /**
     * 契约字段 {@code outputCount}。
     */
    @JsonProperty("outputCount")
    private Long outputCount;
    /**
     * 契约字段 {@code durationSeconds}。
     */
    @JsonProperty("durationSeconds")
    private java.math.BigDecimal durationSeconds;
    /**
     * 契约字段 {@code voiceName}。
     */
    @JsonProperty("voiceName")
    private String voiceName;
    /**
     * 契约字段 {@code references}。
     */
    @JsonProperty("references")
    private java.util.List<DreaminaCanvasNodeGenerationReference> references;
    /**
     * 契约字段 {@code promptParts}。
     */
    @JsonProperty("promptParts")
    private java.util.List<DreaminaCanvasPromptPart> promptParts;
    /**
     * 契约字段 {@code writebackSupported}。
     */
    @JsonProperty("writebackSupported")
    private Boolean writebackSupported;
    /**
     * 契约字段 {@code writebackBlockers}。
     */
    @JsonProperty("writebackBlockers")
    private java.util.List<DreaminaCanvasWritebackBlocker> writebackBlockers;
}
