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
public final class DreaminaCanvasSchema {
    /**
     * 契约字段 {@code schemaVersion}。
     */
    @JsonProperty("schemaVersion")
    private String schemaVersion;
    /**
     * 契约字段 {@code edition}。
     */
    @JsonProperty("edition")
    private String edition;
    /**
     * 契约字段 {@code distribution}。
     */
    @JsonProperty("distribution")
    private String distribution;
    /**
     * 契约字段 {@code locale}。
     */
    @JsonProperty("locale")
    private String locale;
    /**
     * 契约字段 {@code command}。
     */
    @JsonProperty("command")
    private String command;
    /**
     * 契约字段 {@code globalFlags}。
     */
    @JsonProperty("globalFlags")
    private java.util.List<DreaminaCanvasSchemaFlag> globalFlags;
    /**
     * 契约字段 {@code subcommands}。
     */
    @JsonProperty("subcommands")
    private java.util.List<DreaminaCanvasSchemaCommand> subcommands;
}
