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
public final class DreaminaCanvasInstallCommand {
    /**
     * 契约字段 {@code os}。
     */
    @JsonProperty("os")
    private java.util.List<String> os;
    /**
     * 契约字段 {@code terminal}。
     */
    @JsonProperty("terminal")
    private String terminal;
    /**
     * 契约字段 {@code command}。
     */
    @JsonProperty("command")
    private String command;
}
