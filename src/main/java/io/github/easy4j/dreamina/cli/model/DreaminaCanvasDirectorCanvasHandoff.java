package io.github.easy4j.dreamina.cli.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

/** 渲染条件不足时官方返回的画布交接入口。 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public final class DreaminaCanvasDirectorCanvasHandoff {
    /** 官方字段 nodeId。 */
    private String nodeId;
    /** 官方字段 webUrl。 */
    private String webUrl;
}
