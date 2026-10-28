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
public final class DreaminaCanvasTeam {
    /**
     * 契约字段 {@code teamId}。
     */
    @JsonProperty("teamId")
    private String teamId;
    /**
     * 契约字段 {@code name}。
     */
    @JsonProperty("name")
    private String name;
    /**
     * 契约字段 {@code role}。
     */
    @JsonProperty("role")
    private String role;
    /**
     * 契约字段 {@code status}。
     */
    @JsonProperty("status")
    private String status;
    /**
     * 契约字段 {@code creatorName}。
     */
    @JsonProperty("creatorName")
    private String creatorName;
    /**
     * 契约字段 {@code isCreator}。
     */
    @JsonProperty("isCreator")
    private Boolean isCreator;
    /**
     * 契约字段 {@code subscription}。
     */
    @JsonProperty("subscription")
    private DreaminaCanvasSubscription subscription;
    /**
     * 契约字段 {@code credits}。
     */
    @JsonProperty("credits")
    private DreaminaCanvasTeamCredit credits;
}
