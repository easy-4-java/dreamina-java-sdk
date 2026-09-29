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
public final class DreaminaCanvasAuthStatusResult {
    /**
     * 契约字段 {@code loggedIn}。
     */
    @JsonProperty("loggedIn")
    private Boolean loggedIn;
    /**
     * 契约字段 {@code expiresAt}。
     */
    @JsonProperty("expiresAt")
    private String expiresAt;
    /**
     * 契约字段 {@code credentialStorage}。
     */
    @JsonProperty("credentialStorage")
    private String credentialStorage;
    /**
     * 契约字段 {@code profile}。
     */
    @JsonProperty("profile")
    private String profile;
    /**
     * 契约字段 {@code region}。
     */
    @JsonProperty("region")
    private String region;
    /**
     * 契约字段 {@code environment}。
     */
    @JsonProperty("environment")
    private String environment;
    /**
     * 契约字段 {@code authMode}。
     */
    @JsonProperty("authMode")
    private String authMode;
    /**
     * 契约字段 {@code distribution}。
     */
    @JsonProperty("distribution")
    private String distribution;
    /**
     * 契约字段 {@code version}。
     */
    @JsonProperty("version")
    private String version;
    /**
     * 契约字段 {@code edition}。
     */
    @JsonProperty("edition")
    private String edition;
}
