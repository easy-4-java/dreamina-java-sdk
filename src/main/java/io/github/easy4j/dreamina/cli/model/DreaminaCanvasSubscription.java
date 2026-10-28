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
public final class DreaminaCanvasSubscription {
    /**
     * 契约字段 {@code levelStartsAt}。
     */
    @JsonProperty("levelStartsAt")
    private String levelStartsAt;
    /**
     * 契约字段 {@code levelExpiresAt}。
     */
    @JsonProperty("levelExpiresAt")
    private String levelExpiresAt;
    /**
     * 契约字段 {@code isVip}。
     */
    @JsonProperty("isVip")
    private Boolean isVip;
    /**
     * 契约字段 {@code level}。
     */
    @JsonProperty("level")
    private String level;
}
