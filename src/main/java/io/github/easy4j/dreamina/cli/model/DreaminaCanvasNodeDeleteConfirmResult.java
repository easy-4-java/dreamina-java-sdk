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
public final class DreaminaCanvasNodeDeleteConfirmResult {
    /**
     * 契约字段 {@code deleteConfirmationToken}。
     */
    @JsonProperty("deleteConfirmationToken")
    private String deleteConfirmationToken;
    /**
     * 契约字段 {@code expiresAtMs}。
     */
    @JsonProperty("expiresAtMs")
    private Long expiresAtMs;
}
