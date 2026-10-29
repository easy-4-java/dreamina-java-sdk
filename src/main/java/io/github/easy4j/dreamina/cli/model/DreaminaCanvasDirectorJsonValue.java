package io.github.easy4j.dreamina.cli.model;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.util.TokenBuffer;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Objects;

/**
 * 官方 director raw 文档或未公布内部字段的目录值。
 * 使用独立值类型无损保存 JSON token，不把动态文档冒充固定业务模型。
 */
@JsonDeserialize(using = DreaminaCanvasDirectorJsonValue.Reader.class)
@JsonSerialize(using = DreaminaCanvasDirectorJsonValue.Writer.class)
public final class DreaminaCanvasDirectorJsonValue {
    private final TokenBuffer tokens;

    private DreaminaCanvasDirectorJsonValue(TokenBuffer tokens) {
        this.tokens = Objects.requireNonNull(tokens, "tokens");
    }

    /** 返回完整 JSON 文本，保留嵌套结构、标量类型和未来扩展字段。 */
    public String asJson() {
        try {
            StringWriter text = new StringWriter();
            try (JsonGenerator generator = new JsonFactory().createGenerator(text)) {
                tokens.serialize(generator);
            }
            return text.toString();
        } catch (IOException failure) {
            throw new IllegalStateException("Cannot serialize director value", failure);
        }
    }

    /** 由响应解析器读取官方原始 JSON 值，不执行文件或场景操作。 */
    public static final class Reader extends JsonDeserializer<DreaminaCanvasDirectorJsonValue> {
        @Override
        public DreaminaCanvasDirectorJsonValue deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            // 旧 Jackson 默认会把浮点 token 缓存为 double，显式保留十进制精度。
            TokenBuffer copy = new TokenBuffer(parser).forceUseOfBigDecimal(true);
            copy.copyCurrentStructure(parser);
            return new DreaminaCanvasDirectorJsonValue(copy);
        }
    }

    /** 将原始 JSON 值写回响应，避免把正文变成转义字符串。 */
    public static final class Writer extends JsonSerializer<DreaminaCanvasDirectorJsonValue> {
        @Override
        public void serialize(DreaminaCanvasDirectorJsonValue value, JsonGenerator generator, SerializerProvider provider) throws IOException {
            value.tokens.serialize(generator);
        }
    }
}
