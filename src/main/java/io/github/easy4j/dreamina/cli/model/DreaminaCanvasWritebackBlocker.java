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
public final class DreaminaCanvasWritebackBlocker {
    /**
     * 契约字段 {@code referenceIndex}。
     */
    @JsonProperty("referenceIndex")
    private Long referenceIndex;
    /**
     * 契约字段 {@code code}。
     */
    @JsonProperty("code")
    private String code;
    /**
     * 契约字段 {@code retryable}。
     */
    @JsonProperty("retryable")
    private Boolean retryable;
    /**
     * 契约字段 {@code requiredAction}。
     */
    @JsonProperty("requiredAction")
    private String requiredAction;
}
