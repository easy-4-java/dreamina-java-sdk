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
public final class DreaminaCanvasModelItem {
    /**
     * 契约字段 {@code voiceName}。
     */
    @JsonProperty("voiceName")
    private String voiceName;
    /**
     * 契约字段 {@code voiceId}。
     */
    @JsonProperty("voiceId")
    private String voiceId;
    /**
     * 契约字段 {@code platform}。
     */
    @JsonProperty("platform")
    private String platform;
    /**
     * 契约字段 {@code tags}。
     */
    @JsonProperty("tags")
    private java.util.Map<String, String> tags;
    /**
     * 契约字段 {@code previewAvailable}。
     */
    @JsonProperty("previewAvailable")
    private Boolean previewAvailable;
}
