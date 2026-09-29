package io.github.easy4j.dreamina.cli.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.fasterxml.jackson.databind.type.LogicalType;
import io.github.easy4j.dreamina.cli.DreaminaCanvasResponse;
import io.github.easy4j.dreamina.cli.DreaminaCliResult;
import io.github.easy4j.dreamina.exception.DreaminaCanvasCliException;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * Canvas envelope 解析；复用既有 Jackson 通用配置，保留双流和业务错误。
 */
public final class DreaminaCanvasResponseParser {
    private final ObjectMapper mapper = strictMapper();

    private static ObjectMapper strictMapper() {
        ObjectMapper mapper = new ObjectMapper()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
                .disable(MapperFeature.ALLOW_COERCION_OF_SCALARS);
        mapper.coercionConfigFor(LogicalType.Textual)
                .setCoercion(CoercionInputShape.Integer, CoercionAction.Fail)
                .setCoercion(CoercionInputShape.Float, CoercionAction.Fail)
                .setCoercion(CoercionInputShape.Boolean, CoercionAction.Fail);
        return mapper;
    }

    /**
     * 将 CLI 快照转换成 Canvas 响应；仅错误协议或 DTO 映射失败抛异常。
     */
    public <T> DreaminaCanvasResponse<T> parse(String command, DreaminaCliResult raw, Class<T> dataType) {
        Objects.requireNonNull(raw, "raw");
        Objects.requireNonNull(dataType, "dataType");
        try {
            String json = StringUtils.isBlank(raw.getStdout()) && raw.getExitCode() != 0 ? raw.getStderr() : raw.getStdout();
            DreaminaCanvasResponse<T> response = mapper.readValue(json,
                    mapper.getTypeFactory().constructParametricType(DreaminaCanvasResponse.class, dataType));
            if (Objects.isNull(response) || Objects.isNull(response.getOk()) || !"1".equals(response.getSchemaVersion())) {
                throw invalid(command, raw);
            }
            response.setRaw(raw);
            return response;
        } catch (JsonProcessingException | IllegalArgumentException e) {
            throw invalid(command, raw);
        }
    }

    private DreaminaCanvasCliException invalid(String command, DreaminaCliResult raw) {
        return new DreaminaCanvasCliException(DreaminaCanvasCliException.Reason.INVALID_RESPONSE, command,
                raw.getExitCode(), raw.getStdout(), raw.getStderr());
    }
}
