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
public final class DreaminaCanvasTeamSpaceDiscovery {
    /**
     * 契约字段 {@code status}。
     */
    @JsonProperty("status")
    private String status;
    /**
     * 契约字段 {@code hasTeamSpace}。
     */
    @JsonProperty("hasTeamSpace")
    private Boolean hasTeamSpace;
    /**
     * 契约字段 {@code teams}。
     */
    @JsonProperty("teams")
    private java.util.List<DreaminaCanvasTeamSpaceDiscoveryTeam> teams;
    /**
     * 契约字段 {@code requestId}。
     */
    @JsonProperty("requestId")
    private String requestId;
    /**
     * 契约字段 {@code error}。
     */
    @JsonProperty("error")
    private DreaminaCanvasTeamSpaceDiscoveryError error;
    /**
     * 契约字段 {@code clientUpgrade}。
     */
    @JsonProperty("clientUpgrade")
    private DreaminaCanvasClientUpgrade clientUpgrade;
}
