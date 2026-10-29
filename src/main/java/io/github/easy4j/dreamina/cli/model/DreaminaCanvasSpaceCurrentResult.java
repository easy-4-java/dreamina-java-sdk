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
public final class DreaminaCanvasSpaceCurrentResult {
    /**
     * 契约字段 {@code displayName}。
     */
    @JsonProperty("displayName")
    private String displayName;
    /**
     * 契约字段 {@code credits}。
     */
    @JsonProperty("credits")
    private DreaminaCanvasCredit credits;
    /**
     * 契约字段 {@code isVip}。
     */
    @JsonProperty("isVip")
    private Boolean isVip;
    /**
     * 契约字段 {@code vipLevel}。
     */
    @JsonProperty("vipLevel")
    private String vipLevel;
    /**
     * 契约字段 {@code vipLevelStartsAt}。
     */
    @JsonProperty("vipLevelStartsAt")
    private String vipLevelStartsAt;
    /**
     * 契约字段 {@code vipLevelExpiresAt}。
     */
    @JsonProperty("vipLevelExpiresAt")
    private String vipLevelExpiresAt;
    /**
     * 契约字段 {@code userId}。
     */
    @JsonProperty("userId")
    private String userId;
    /**
     * 契约字段 {@code kind}。
     */
    @JsonProperty("kind")
    private String kind;
    /**
     * 契约字段 {@code team}。
     */
    @JsonProperty("team")
    private DreaminaCanvasTeam team;
    /**
     * 契约字段 {@code currentCanvas}。
     */
    @JsonProperty("currentCanvas")
    private DreaminaCanvasCurrentCanva currentCanvas;
    /**
     * 契约字段 {@code checkedAt}。
     */
    @JsonProperty("checkedAt")
    private Long checkedAt;
}
