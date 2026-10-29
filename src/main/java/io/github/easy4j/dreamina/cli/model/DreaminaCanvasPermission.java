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
public final class DreaminaCanvasPermission {
    /**
     * 契约字段 {@code canRead}。
     */
    @JsonProperty("canRead")
    private Boolean canRead;
    /**
     * 契约字段 {@code canEdit}。
     */
    @JsonProperty("canEdit")
    private Boolean canEdit;
    /**
     * 契约字段 {@code canManage}。
     */
    @JsonProperty("canManage")
    private Boolean canManage;
    /**
     * 契约字段 {@code canCopy}。
     */
    @JsonProperty("canCopy")
    private Boolean canCopy;
}
