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
public final class DreaminaCanvasNodeConfirmResult {
    /**
     * 契约字段 {@code creditConfirmationToken}。
     */
    @JsonProperty("creditConfirmationToken")
    private String creditConfirmationToken;
    /**
     * 契约字段 {@code creditCeiling}。
     */
    @JsonProperty("creditCeiling")
    private Long creditCeiling;
    /**
     * 契约字段 {@code expiresAt}。
     */
    @JsonProperty("expiresAt")
    private String expiresAt;
}
