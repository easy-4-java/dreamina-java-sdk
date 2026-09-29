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
    @JsonProperty("class")
    private String errorClass;
    private String message;
    private String requiredAction;
    private Boolean retryable;
    private DreaminaCanvasValidation validation;
    private DreaminaCanvasErrorDetails details;
}
