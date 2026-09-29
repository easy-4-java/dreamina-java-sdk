package io.github.easy4j.dreamina.cli;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.github.easy4j.dreamina.cli.model.DreaminaCanvasCreditConfirmation;
import io.github.easy4j.dreamina.cli.model.DreaminaCanvasError;
import io.github.easy4j.dreamina.cli.model.DreaminaCanvasMeta;
import io.github.easy4j.dreamina.cli.model.DreaminaCanvasPartialData;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * 强类型 Canvas envelope。复用既有原始进程结果，保留双流及退出码。
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public final class DreaminaCanvasResponse<T> {
    private String schemaVersion;
    private Boolean ok;
    private T data;
    private DreaminaCanvasError error;
    private DreaminaCanvasPartialData partialData;
    private DreaminaCanvasCreditConfirmation creditConfirmation;
    private DreaminaCanvasMeta meta;
    @JsonIgnore
    private DreaminaCliResult raw;

    /**
     * 成功同时要求进程正常退出和协议 ok=true。
     */
    @JsonIgnore
    public boolean isSuccess() {
        return Objects.nonNull(raw) && Integer.valueOf(0).equals(raw.getExitCode()) && Boolean.TRUE.equals(ok);
    }

    @JsonIgnore
    public Integer getExitCode() {
        return raw.getExitCode();
    }

    @JsonIgnore
    public String getStdout() {
        return raw.getStdout();
    }

    @JsonIgnore
    public String getStderr() {
        return raw.getStderr();
    }
}
