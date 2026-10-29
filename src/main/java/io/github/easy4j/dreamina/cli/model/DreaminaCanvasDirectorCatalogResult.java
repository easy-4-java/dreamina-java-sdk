package io.github.easy4j.dreamina.cli.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/** 官方导演台响应；由 generate_director_types.py 增量生成。 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public final class DreaminaCanvasDirectorCatalogResult {
    /** 官方字段 primitives。 */
    @JsonProperty("primitives")
    private java.util.List<String> primitives;
    /** 官方字段 presets。 */
    @JsonProperty("presets")
    private java.util.List<DreaminaCanvasDirectorJsonValue> presets;
    /** 官方字段 commands。 */
    @JsonProperty("commands")
    private java.util.List<String> commands;
    /** 官方字段 commandFields。 */
    @JsonProperty("commandFields")
    private DreaminaCanvasDirectorJsonValue commandFields;
    /** 官方字段 items。 */
    @JsonProperty("items")
    private java.util.List<DreaminaCanvasDirectorJsonValue> items;
    /** 官方字段 hasMore。 */
    @JsonProperty("hasMore")
    private Boolean hasMore;
    /** 官方字段 nextOffset。 */
    @JsonProperty("nextOffset")
    private Long nextOffset;
    /** 官方字段 rendering。 */
    @JsonProperty("rendering")
    private DreaminaCanvasDirectorCatalogResultRendering rendering;
}
