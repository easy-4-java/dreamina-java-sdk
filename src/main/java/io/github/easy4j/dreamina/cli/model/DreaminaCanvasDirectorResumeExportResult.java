package io.github.easy4j.dreamina.cli.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/** 官方导演台响应；由 generate_director_types.py 增量生成。 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public final class DreaminaCanvasDirectorResumeExportResult {
    /** 官方字段 operationRef。 */
    @JsonProperty("operationRef")
    private String operationRef;
    /** 官方字段 canvas。 */
    @JsonProperty("canvas")
    private DreaminaCanvasDirectorResumeExportResultCanvas canvas;
    /** 官方字段 source。 */
    @JsonProperty("source")
    private DreaminaCanvasDirectorResumeExportResultSource source;
    /** 官方字段 options。 */
    @JsonProperty("options")
    private DreaminaCanvasDirectorResumeExportResultOptions options;
    /** 官方字段 path。 */
    @JsonProperty("path")
    private String path;
    /** 官方字段 format。 */
    @JsonProperty("format")
    private String format;
    /** 官方字段 byteSize。 */
    @JsonProperty("byteSize")
    private Long byteSize;
    /** 官方字段 sha256。 */
    @JsonProperty("sha256")
    private String sha256;
}
