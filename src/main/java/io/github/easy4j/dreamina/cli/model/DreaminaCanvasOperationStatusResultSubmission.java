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
public final class DreaminaCanvasOperationStatusResultSubmission {
    /**
     * 契约字段 {@code state}。
     */
    @JsonProperty("state")
    private String state;
    /**
     * 契约字段 {@code resubmittable}。
     */
    @JsonProperty("resubmittable")
    private Boolean resubmittable;
    /**
     * 契约字段 {@code adviceCode}。
     */
    @JsonProperty("adviceCode")
    private String adviceCode;
    /**
     * 契约字段 {@code advice}。
     */
    @JsonProperty("advice")
    private String advice;
}
