package io.github.easy4j.dreamina.cli.canvas;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;
import io.github.easy4j.dreamina.cli.opts.DreaminaCanvasContract;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/** 独立固定行为用例，验证请求在启动子进程前拒绝静态非法组合。 */
class DreaminaCanvasStaticRulesTest {
    static Stream<Arguments> cases() throws Exception {
        List<Map<String, Object>> cases = new ObjectMapper().readValue(
                DreaminaCanvasStaticRulesTest.class.getResourceAsStream("/cli-contract/dual-cli-static-cases.json"),
                new TypeReference<List<Map<String, Object>>>() {});
        return cases.stream().map(c -> Arguments.of(c.get("name"), DreaminaCanvasCommand.valueOf((String) c.get("command")),
                c.get("valid"), ((List<?>) c.get("args")).toArray(new String[0])));
    }
    @ParameterizedTest(name = "{0}")
    @MethodSource("cases")
    void staticContract(String name, DreaminaCanvasCommand command, boolean valid, String[] args) {
        for (String arg : args) {
            if (!arg.startsWith("@")) {
                String key = arg.split("=", 2)[0];
                org.junit.jupiter.api.Assertions.assertTrue(DreaminaCanvasContract.describe(command).getFlags().stream()
                        .anyMatch(flag -> flag.getName().equals(key)), "fixture must use real flag: " + key);
            }
        }
        if (valid) {
            assertDoesNotThrow(() -> DreaminaCanvasFixtures.request(command, args), name);
        } else {
            assertThrows(IllegalArgumentException.class, () -> DreaminaCanvasFixtures.request(command, args), name);
        }
    }
}
