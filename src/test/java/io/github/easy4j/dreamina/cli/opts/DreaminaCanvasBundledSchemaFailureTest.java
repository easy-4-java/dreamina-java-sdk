package io.github.easy4j.dreamina.cli.opts;

import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 缺包、损坏及与 SDK 不匹配的随包命令契约，应在执行前暴露清晰错误。
 */
class DreaminaCanvasBundledSchemaFailureTest {
    private static final String NAME = "io.github.easy4j.dreamina.cli.opts.DreaminaCanvasContract";

    @Test
    void missingOrCorruptSchemaFailsClearly() throws Exception {
        assertEquals("Missing Canvas schema resource", failure(null, "schema").getMessage());
        assertEquals("Cannot read Canvas schema", failure("{".getBytes(StandardCharsets.UTF_8), "schema").getMessage());
        assertEquals("Missing bundled command contract", failure("{\"subcommands\":[]}".getBytes(StandardCharsets.UTF_8), "describe").getMessage());
    }

    private IllegalStateException failure(byte[] schema, String methodName) throws Exception {
        ClassLoader parent = DreaminaCanvasContract.class.getClassLoader();
        ClassLoader isolated = new ClassLoader(parent) {
            @Override
            protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
                if (!NAME.equals(name)) {
                    return super.loadClass(name, resolve);
                }
                Class<?> found = findLoadedClass(name);
                if (Objects.isNull(found)) {
                    try (InputStream source = parent.getResourceAsStream(NAME.replace('.', '/') + ".class")) {
                        if (Objects.isNull(source)) {
                            throw new ClassNotFoundException(name);
                        }
                        byte[] bytes = new byte[32768];
                        int count = source.read(bytes);
                        if (count < 1 || source.read() != -1) {
                            throw new ClassNotFoundException("Unexpected Canvas contract bytecode size");
                        }
                        found = defineClass(name, bytes, 0, count);
                    } catch (IOException e) {
                        throw new ClassNotFoundException(name, e);
                    }
                }
                if (resolve) {
                    resolveClass(found);
                }
                return found;
            }

            @Override
            public InputStream getResourceAsStream(String path) {
                if (path.endsWith("schema-1.0.1.json")) {
                    return Objects.isNull(schema) ? null : new ByteArrayInputStream(schema);
                }
                return super.getResourceAsStream(path);
            }
        };
        Class<?> contract = isolated.loadClass(NAME);
        Method method = "schema".equals(methodName) ? contract.getMethod("schema") : contract.getMethod("describe", DreaminaCanvasCommand.class);
        InvocationTargetException invoked = assertThrows(InvocationTargetException.class,
                () -> {
                    if ("schema".equals(methodName)) {
                        method.invoke(null);
                    } else {
                        method.invoke(null, DreaminaCanvasCommand.VERSION);
                    }
                });
        assertInstanceOf(IllegalStateException.class, invoked.getCause());
        return (IllegalStateException) invoked.getCause();
    }
}
