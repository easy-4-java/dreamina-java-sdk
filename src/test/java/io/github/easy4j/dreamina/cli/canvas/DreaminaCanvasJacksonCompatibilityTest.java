package io.github.easy4j.dreamina.cli.canvas;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/** 使用真实 Boot 2.3 下游空缓存中的 Jackson 2.11 验证运行时兼容与严格解析。 */
class DreaminaCanvasJacksonCompatibilityTest {
    @Test
    @EnabledIfSystemProperty(named = "dual.cli.jackson211.runtime", matches = ".+")
    void oldBootJacksonCanStartAndRejectScalarCoercion() throws Exception {
        List<URL> urls = new ArrayList<>();
        urls.add(Paths.get("target/classes").toUri().toURL());
        try (Stream<Path> paths = Files.walk(Paths.get(System.getProperty("dual.cli.jackson211.runtime")))) {
            paths.filter(p -> p.toString().endsWith(".jar")).sorted().forEach(p -> {
                try { urls.add(p.toUri().toURL()); } catch (Exception e) { throw new IllegalStateException(e); }
            });
        }
        try (URLClassLoader runtime = new URLClassLoader(urls.toArray(new URL[0]), null)) {
            Class<?> mapper = runtime.loadClass("com.fasterxml.jackson.databind.ObjectMapper");
            assertTrue(mapper.getPackage().getImplementationVersion().startsWith("2.11."));
            Class<?> parser = runtime.loadClass("io.github.easy4j.dreamina.cli.parser.DreaminaCanvasResponseParser");
            Object instance = parser.getConstructor().newInstance();
            Class<?> rawType = runtime.loadClass("io.github.easy4j.dreamina.cli.DreaminaCliResult");
            Class<?> dataType = runtime.loadClass("io.github.easy4j.dreamina.cli.model.DreaminaVersion");
            for (String value : new String[]{"\"v1\"", "null", "7", "1.25", "true", "{}", "[]"}) {
                Object builder = rawType.getMethod("builder").invoke(null);
                Class<?> type = builder.getClass();
                String json = "{\"schemaVersion\":\"1\",\"ok\":true,\"data\":{\"version\":" + value + "}}";
                type.getMethod("stdout", String.class).invoke(builder, json);
                type.getMethod("exitCode", Integer.class).invoke(builder, 0);
                Object raw = type.getMethod("build").invoke(builder);
                if ("\"v1\"".equals(value) || "null".equals(value)) {
                    assertNotNull(parser.getMethod("parse", String.class, rawType, Class.class)
                            .invoke(instance, "version", raw, dataType));
                } else {
                    InvocationTargetException error = assertThrows(InvocationTargetException.class,
                            () -> parser.getMethod("parse", String.class, rawType, Class.class)
                                    .invoke(instance, "version", raw, dataType));
                    assertEquals("DreaminaCanvasCliException", error.getCause().getClass().getSimpleName());
                }
            }
            Object builder = rawType.getMethod("builder").invoke(null);
            String decimal = "0.12345678901234567890123456789";
            String document = "{\"camera\":{\"position\":[1," + decimal + ",3]},\"custom\":[true,null,\"x\"]}";
            builder.getClass().getMethod("stdout", String.class).invoke(builder,
                    "{\"schemaVersion\":\"1\",\"ok\":true,\"data\":{\"document\":" + document + "}}");
            builder.getClass().getMethod("exitCode", Integer.class).invoke(builder, 0);
            Object raw = builder.getClass().getMethod("build").invoke(builder);
            Class<?> directorType = runtime.loadClass("io.github.easy4j.dreamina.cli.model.DreaminaCanvasDirectorShowResult");
            Object response = parser.getMethod("parse", String.class, rawType, Class.class)
                    .invoke(instance, "director show", raw, directorType);
            Object data = response.getClass().getMethod("getData").invoke(response);
            Object value = directorType.getMethod("getDocument").invoke(data);
            assertEquals(document, value.getClass().getMethod("asJson").invoke(value));
        }
    }
}
