package io.github.easy4j.dreamina.cli;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.easy4j.dreamina.cli.parser.DreaminaParsedFields;
import io.github.easy4j.dreamina.util.DreaminaStrings;
import lombok.Builder;
import lombok.Getter;

/**
 * WYSIWYG result for a single CLI command: raw output + parsed business object.
 * <p>
 * {@link #getBody()} corresponds to the structured view of the command's stdout
 * (JSON deserialization or text/table parsing); when parsing fails it is {@code null},
 * and the caller can still read the raw text via {@link #getStdout()} / {@link #getStderr()}.
 * </p>
 *
 * @see DreaminaCliResult
 * @see io.github.easy4j.dreamina.cli.parser.DreaminaCliStructuredPayloadMapper
 *
 * @param <T> 本命令解析体类型（如 {@link io.github.easy4j.dreamina.cli.model.DreaminaVersion}）
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 */
@Getter
@Builder
public final class DreaminaCliResponse<T> {

    private final String stdout;
    private final String stderr;
    private final Integer exitCode;
    private final boolean success;
    private final DreaminaParsedFields parsed;
    private final T body;
    private final JsonNode json;

    /**
     * Binds a raw snapshot with a parsed body (no JSON tree).
     *
     * @param raw  CLI 原始结果
     * @param body 解析体，可为 null
     */
    public static <T> DreaminaCliResponse<T> of(DreaminaCliResult raw, T body) {
        return of(raw, body, null);
    }

    /**
     * Binds a raw snapshot, parsed body, and JSON root node.
     *
     * @param raw  CLI 原始结果
     * @param body 解析体，可为 null
     * @param json JSON 根；非 JSON 命令可为 null
     */
    public static <T> DreaminaCliResponse<T> of(DreaminaCliResult raw, T body, JsonNode json) {
        return DreaminaCliResponse.<T>builder()
            .stdout(raw.getStdout())
            .stderr(raw.getStderr())
            .exitCode(raw.getExitCode())
            .success(raw.isSuccess())
            .parsed(raw.getParsed())
            .body(body)
            .json(json)
            .build();
    }

    /**
     * @return Merges stdout + stderr (consistent with the legacy {@code combinedText}).
     */
    public String getCombinedText() {
        String out = stdout == null ? "" : stdout;
        String err = stderr == null ? "" : stderr;
        if (DreaminaStrings.isBlank(err)) {
            return out;
        }
        if (DreaminaStrings.isBlank(out)) {
            return err;
        }
        return out + System.lineSeparator() + err;
    }

    /**
     * @return Whether a non-null body was parsed.
     */
    public boolean hasBody() {
        return body != null;
    }
}
