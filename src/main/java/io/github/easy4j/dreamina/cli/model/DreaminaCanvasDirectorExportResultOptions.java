package io.github.easy4j.dreamina.cli.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/** 官方导演台响应；由 generate_director_types.py 增量生成。 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public final class DreaminaCanvasDirectorExportResultOptions {
    /** 官方字段 format。 */
    @JsonProperty("format")
    private String format;
    /** 官方字段 workspace。 */
    @JsonProperty("workspace")
    private String workspace;
    /** 官方字段 motionId。 */
    @JsonProperty("motionId")
    private String motionId;
    /** 官方字段 cameraId。 */
    @JsonProperty("cameraId")
    private String cameraId;
    /** 官方字段 storyboard。 */
    @JsonProperty("storyboard")
    private Boolean storyboard;
    /** 官方字段 frame。 */
    @JsonProperty("frame")
    private Long frame;
    /** 官方字段 longEdge。 */
    @JsonProperty("longEdge")
    private Long longEdge;
}
