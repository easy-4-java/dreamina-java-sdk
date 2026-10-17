package io.github.easy4j.dreamina.cli.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

/**
 * 画布命令的结构化恢复或诊断信息。未知扩展可从原始输出读取。
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public final class DreaminaCanvasErrorDetails {
    private String reason;
    private String field;
    private String serviceCode;
    private String message;
    private String operationRef;
    private String submitId;
    private DreaminaCanvasValidation validation;
}
