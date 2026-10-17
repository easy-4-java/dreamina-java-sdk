package io.github.easy4j.dreamina.cli.canvas;

import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 每条指令必须有具名请求，Canvas 生产代码禁止通用 JSON 树。
 */
class DreaminaCanvasTypedApiTest {
    @Test
    void everyCommandHasConcreteRequest() throws Exception {
        for (DreaminaCanvasCommand command : DreaminaCanvasCommand.values()) {
            StringBuilder name = new StringBuilder("DreaminaCanvas");
            for (String word : command.getPath().split(" ")) {
                name.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
            }
            DreaminaCanvasFixtures.requestClass(command);
        }
    }

    @Test
    void canvasProductionHasNoUntypedPayload() throws Exception {
        try (Stream<Path> paths = Files.walk(Paths.get("src/main/java"))) {
            for (Path path : (Iterable<Path>) paths.filter(p -> p.getFileName().toString().contains("Canvas"))::iterator) {
                String source = new String(Files.readAllBytes(path), java.nio.charset.StandardCharsets.UTF_8);
                assertFalse(source.contains("JsonNode"), path.toString());
                assertFalse(source.contains("Map<String, Object>"), path.toString());
                assertFalse(source.contains("option(String"), path.toString());
            }
        }
    }
}
