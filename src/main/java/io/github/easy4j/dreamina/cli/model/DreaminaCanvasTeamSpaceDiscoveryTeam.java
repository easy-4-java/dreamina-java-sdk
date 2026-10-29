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
public final class DreaminaCanvasTeamSpaceDiscoveryTeam {
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
}
