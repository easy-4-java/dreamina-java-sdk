package io.github.easy4j.dreamina.cli.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/** 官方导演台响应；由 generate_director_types.py 增量生成。 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public final class DreaminaCanvasDirectorCreateResult {
    /** 官方字段 file。 */
    @JsonProperty("file")
    private String file;
    /** 官方字段 nodeId。 */
    @JsonProperty("nodeId")
    private String nodeId;
    /** 官方字段 projectId。 */
    @JsonProperty("projectId")
    private String projectId;
    /** 官方字段 versionId。 */
    @JsonProperty("versionId")
    private String versionId;
    /** 官方字段 savedDraftVersion。 */
    @JsonProperty("savedDraftVersion")
    private Long savedDraftVersion;
    /** 官方字段 document。 */
    @JsonProperty("document")
    private DreaminaCanvasDirectorJsonValue document;
    /** 官方字段 webUrl。 */
    @JsonProperty("webUrl")
    private String webUrl;
    /** 官方字段 overwrittenVersionId。 */
    @JsonProperty("overwrittenVersionId")
    private String overwrittenVersionId;
}
