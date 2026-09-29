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
public final class DreaminaCanvasSchemaCommand {
    /**
     * 契约字段 {@code name}。
     */
    @JsonProperty("name")
    private String name;
    /**
     * 契约字段 {@code summary}。
     */
    @JsonProperty("summary")
    private String summary;
    /**
     * 契约字段 {@code reads}。
     */
    @JsonProperty("reads")
    private Boolean reads;
    /**
     * 契约字段 {@code writes}。
     */
    @JsonProperty("writes")
    private Boolean writes;
    /**
     * 契约字段 {@code formats}。
     */
    @JsonProperty("formats")
    private java.util.List<String> formats;
    /**
     * 契约字段 {@code dryRun}。
     */
    @JsonProperty("dryRun")
    private Boolean dryRun;
    /**
     * 契约字段 {@code confirmation}。
     */
    @JsonProperty("confirmation")
    private Boolean confirmation;
    /**
     * 契约字段 {@code declaredExitCodes}。
     */
    @JsonProperty("declaredExitCodes")
    private java.util.List<Long> declaredExitCodes;
    /**
     * 契约字段 {@code arguments}。
     */
    @JsonProperty("arguments")
    private java.util.List<DreaminaCanvasArgument> arguments;
    /**
     * 契约字段 {@code flags}。
     */
    @JsonProperty("flags")
    private java.util.List<DreaminaCanvasSchemaFlag> flags;
    /**
     * 契约字段 {@code constraints}。
     */
    @JsonProperty("constraints")
    private java.util.List<DreaminaCanvasConstraint> constraints;
    /**
     * 契约字段 {@code successData}。
     */
    @JsonProperty("successData")
    private DreaminaCanvasSuccessData successData;
    /**
     * 契约字段 {@code dryRunData}。
     */
    @JsonProperty("dryRunData")
    private DreaminaCanvasSuccessData dryRunData;
    /**
     * 契约字段 {@code subcommands}。
     */
    @JsonProperty("subcommands")
    private java.util.List<DreaminaCanvasSchemaCommand> subcommands;
}
