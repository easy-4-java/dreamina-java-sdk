package io.github.easy4j.dreamina.cli.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * CLI envelope 错误，与服务端生成错误分别建模。
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public final class DreaminaCanvasError {
    private String code;
    /** 发生错误的画布项目身份。 */
    private String projectId;
    /** CLI 认证或本地存储错误的分类原因。 */
    private String reason;
    /** 本地业务状态读写诊断，submissionPhase 决定是否只能恢复原任务。 */
    private DreaminaCanvasLocalStorageWarning localStorage;
    @JsonProperty("class")
    private String errorClass;
    private String message;
    /** 官方服务错误编号，保持数值类型。 */
    private Long serviceCode;
    /** 官方请求与链路标识，用于恢复和服务端排障。 */
    private String requestId;
    private String traceId;
    private String requiredAction;
    private Boolean retryable;
    private DreaminaCanvasValidation validation;
    private DreaminaCanvasErrorDetails details;
}
