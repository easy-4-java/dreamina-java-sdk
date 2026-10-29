package io.github.easy4j.dreamina.cli.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/** 官方导演台响应；由 generate_director_types.py 增量生成。 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public final class DreaminaCanvasDirectorCatalogResultRendering {
    /** 官方字段 available。 */
    @JsonProperty("available")
    private Boolean available;
    /** 官方字段 code。 */
    @JsonProperty("code")
    private String code;
}
