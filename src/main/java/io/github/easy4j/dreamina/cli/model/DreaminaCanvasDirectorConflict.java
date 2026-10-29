package io.github.easy4j.dreamina.cli.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

/** 导演台并发版本冲突，版本标识按十进制字符串保留。 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public final class DreaminaCanvasDirectorConflict {
    /** 官方字段 nodeId。 */
    private String nodeId;
    /** 官方字段 baseVersionId。 */
    private String baseVersionId;
    /** 官方字段 latestVersionId。 */
    private String latestVersionId;
}
