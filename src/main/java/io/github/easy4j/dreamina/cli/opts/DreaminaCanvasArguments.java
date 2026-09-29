package io.github.easy4j.dreamina.cli.opts;

import java.time.Duration;
import java.util.*;

/**
 * 包内参数编码器；对外仅暴露每条指令的具体请求。
 */
final class DreaminaCanvasArguments {
    final Map<String, List<String>> options = new LinkedHashMap<>();
    final List<String> positions = new ArrayList<>();
    private boolean yes;

    void flag(String name, String value) {
        if (Objects.nonNull(value)) {
            options.computeIfAbsent(name, key -> new ArrayList<>()).add(value);
        }
    }

    void flag(String name, Boolean value) {
        if (Objects.nonNull(value)) {
            flag(name, value.toString());
        }
    }

    void flag(String name, Number value) {
        if (Objects.nonNull(value)) {
            flag(name, value.toString());
        }
    }

    void flag(String name, Duration value) {
        if (Objects.nonNull(value)) {
            try {
                flag(name, value.toNanos() + "ns");
            } catch (ArithmeticException e) {
                throw new IllegalArgumentException(name + " duration outside range", e);
            }
        }
    }

    void flag(String name, List<String> values) {
        if (Objects.nonNull(values)) {
            for (String value : values) {
                flag(name, Objects.requireNonNull(value, name));
            }
        }
    }

    void position(String value) {
        if (Objects.nonNull(value)) {
            positions.add(value);
        }
    }

    void position(List<String> values) {
        if (Objects.nonNull(values)) {
            for (String value : values) {
                positions.add(Objects.requireNonNull(value, "position"));
            }
        }
    }

    void yes(boolean enabled) {
        yes = enabled;
    }

    List<String> render() {
        List<String> result = new ArrayList<>();
        options.forEach((key, values) -> values.forEach(value -> result.add("--" + key + "=" + value)));
        if (yes) {
            result.add("--yes");
        }
        if (!positions.isEmpty()) {
            result.add("--");
            result.addAll(positions);
        }
        return result;
    }
}
