package io.github.easy4j.dreamina.cli.canvas;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.easy4j.dreamina.cli.DreaminaCanvasCliExecutor;
import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;
import io.github.easy4j.dreamina.cli.model.DreaminaCanvasSchemaCommand;
import io.github.easy4j.dreamina.cli.model.DreaminaCanvasSchemaField;
import io.github.easy4j.dreamina.cli.model.DreaminaCanvasSchemaFlag;
import io.github.easy4j.dreamina.cli.opts.DreaminaCanvasContract;
import io.github.easy4j.dreamina.cli.opts.DreaminaCanvasParameter;
import io.github.easy4j.dreamina.cli.opts.DreaminaCanvasRequest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 独立按官方 schema 深度遍历，逐一检查参数、嵌套响应字段及真实反序列化。
 */
class DreaminaCanvasFieldCoverageTest {
    @ParameterizedTest(name = "typed parameters and executor: {0}")
    @EnumSource(DreaminaCanvasCommand.class)
    void everyParameterHasCorrectJavaFieldAndBuilder(DreaminaCanvasCommand command) throws Exception {
        Class<?> type = DreaminaCanvasFixtures.requestClass(command);
        Object builder = type.getMethod("builder").invoke(null);
        DreaminaCanvasSchemaCommand spec = DreaminaCanvasContract.describe(command);
        Map<String, Field> mapped = new LinkedHashMap<>();
        for (Field f : type.getDeclaredFields()) {
            DreaminaCanvasParameter p = f.getAnnotation(DreaminaCanvasParameter.class);
            if (p != null) {
                assertNull(mapped.put((p.positional() ? "@" : "") + p.value(), f));
            }
        }
        for (DreaminaCanvasSchemaFlag flag : spec.getFlags()) {
            Field f = mapped.remove(flag.getName());
            assertNotNull(f, command + " --" + flag.getName());
            Class<?> expected = parameterType(flag.getType());
            assertEquals(expected, f.getType(), flag.getName());
            assertNotNull(builder.getClass().getMethod(f.getName(), expected == List.class ? java.util.Collection.class : expected));
        }
        if (spec.getArguments() != null) {
            for (io.github.easy4j.dreamina.cli.model.DreaminaCanvasArgument arg : spec.getArguments()) {
                Field f = mapped.remove("@" + arg.getName());
                assertNotNull(f);
                assertEquals(Boolean.TRUE.equals(arg.getRepeatable()) ? List.class : String.class, f.getType());
            }
        }
        assertTrue(mapped.isEmpty(), mapped.toString());
        StringBuilder words = new StringBuilder();
        for (String word : command.getPath().split(" ")) {
            words.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        }
        String method = words.toString();
        method = Character.toLowerCase(method.charAt(0)) + method.substring(1);
        assertNotNull(DreaminaCanvasCliExecutor.class.getMethod(method, type));
    }

    @ParameterizedTest(name = "typed success and dry-run payloads: {0}")
    @EnumSource(DreaminaCanvasCommand.class)
    void everyResponseFieldDeserializesIntoConcreteType(DreaminaCanvasCommand command) throws Exception {
        DreaminaCanvasRequest<?> request = DreaminaCanvasFixtures.request(command, DreaminaCanvasCommandCoverageTest.cases().get(command).args);
        DreaminaCanvasSchemaCommand spec = DreaminaCanvasContract.describe(command);
        verify(request.getDataType(), spec.getSuccessData().getFields(), command + " success");
        if (spec.getDryRunData() != null) {
            verify(request.getDataType(), spec.getDryRunData().getFields(), command + " dry-run");
        }
    }

    private Class<?> parameterType(String type) {
        switch (type) {
            case "string[]":
                return List.class;
            case "boolean":
                return Boolean.class;
            case "integer":
                return Long.class;
            case "number":
                return java.math.BigDecimal.class;
            case "duration":
                return java.time.Duration.class;
            default:
                return String.class;
        }
    }

    private void verify(Class<?> type, List<DreaminaCanvasSchemaField> fields, String path) throws Exception {
        if (fields == null) {
            return;
        }
        Map<String, Object> sample = new LinkedHashMap<>();
        for (DreaminaCanvasSchemaField spec : fields) {
            Field field = find(type, spec.getName());
            assertNotNull(field, path + "." + spec.getName());
            sample.put(spec.getName(), sample(field.getGenericType(), spec, path + "." + spec.getName()));
        }
        ObjectMapper mapper = new ObjectMapper().disable(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        Object body = mapper.readValue(mapper.writeValueAsString(sample), type);
        for (DreaminaCanvasSchemaField spec : fields) {
            Field f = find(type, spec.getName());
            f.setAccessible(true);
            assertNotNull(f.get(body), path + "." + spec.getName());
        }
    }

    private Object sample(Type type, DreaminaCanvasSchemaField spec, String path) throws Exception {
        String wire = spec.getType();
        if (wire.startsWith("array<") || wire.startsWith("map<")) {
            assertInstanceOf(ParameterizedType.class, type, path);
            ParameterizedType parameter = (ParameterizedType) type;
            assertEquals(wire.startsWith("array") ? List.class : Map.class, parameter.getRawType());
            Type element = parameter.getActualTypeArguments()[wire.startsWith("array") ? 0 : 1];
            DreaminaCanvasSchemaField nested = new DreaminaCanvasSchemaField();
            nested.setName(spec.getName());
            nested.setType(wire.startsWith("array") ? wire.substring(6, wire.length() - 1) : "object");
            nested.setFields(spec.getFields());
            Object value = sample(element, nested, path + "[]");
            return wire.startsWith("array") ? Collections.singletonList(value) : Collections.singletonMap("dynamic-model", value);
        }
        if ("object".equals(wire)) {
            assertInstanceOf(Class.class, type, path);
            assertNotEquals(Object.class, type, path);
            verify((Class<?>) type, spec.getFields(), path);
            // 实際嵌套数据由递归调用验证，不用空 JSON 树冒充字段覆盖。
            return new LinkedHashMap<String, String>();
        }
        Class<?> expected = "string".equals(wire) ? String.class : "integer".equals(wire) ? Long.class : "boolean".equals(wire) ? Boolean.class : java.math.BigDecimal.class;
        assertEquals(expected, type, path);
        return expected == String.class ? "typed value" : expected == Boolean.class ? Boolean.TRUE : expected == Long.class ? 7L : new java.math.BigDecimal("1.25");
    }

    private Field find(Class<?> type, String name) {
        for (Field f : type.getDeclaredFields()) {
            JsonProperty p = f.getAnnotation(JsonProperty.class);
            JsonAlias a = f.getAnnotation(JsonAlias.class);
            if (f.getName().equals(name) || p != null && p.value().equals(name) || a != null && Arrays.asList(a.value()).contains(name)) {
                return f;
            }
        }
        return null;
    }
}
