package io.github.easy4j.dreamina.cli.canvas;

import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;
import io.github.easy4j.dreamina.cli.opts.DreaminaCanvasParameter;
import io.github.easy4j.dreamina.cli.opts.DreaminaCanvasRequest;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 仅测试使用的独立文本用例加载器；生产入口没有通用参数 map。
 */
final class DreaminaCanvasFixtures {
    static Class<?> requestClass(DreaminaCanvasCommand command) throws ClassNotFoundException {
        StringBuilder name = new StringBuilder("io.github.easy4j.dreamina.cli.opts.DreaminaCanvas");
        for (String word : command.getPath().split(" ")) {
            name.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        }
        return Class.forName((name + "Request").replace("DreaminaCanvasCanvas", "DreaminaCanvas"));
    }

    static DreaminaCanvasRequest<?> request(DreaminaCanvasCommand command, String... args) {
        try {
            Class<?> type = requestClass(command);
            Object builder = type.getMethod("builder").invoke(null);
            Map<String, List<String>> values = new LinkedHashMap<>();
            for (String arg : args) {
                String[] kv = arg.startsWith("@") ? new String[]{"@", arg.substring(1)} : arg.split("=", 2);
                values.computeIfAbsent(kv[0], k -> new ArrayList<>()).add(kv[1]);
            }
            for (Field field : type.getDeclaredFields()) {
                DreaminaCanvasParameter p = field.getAnnotation(DreaminaCanvasParameter.class);
                if (p == null) {
                    continue;
                }
                List<String> list = values.remove(p.positional() ? "@" : p.value());
                if (list == null) {
                    continue;
                }
                Class<?> ft = field.getType();
                if (ft != List.class && list.size() != 1) {
                    throw new IllegalArgumentException("duplicate scalar");
                }
                String text = list.get(0);
                Object v = text;
                if (ft == List.class) {
                    v = list;
                } else if (ft == Long.class) {
                    v = Long.valueOf(text);
                } else if (ft == Boolean.class) {
                    v = Boolean.valueOf(text);
                } else if (ft == BigDecimal.class) {
                    v = new BigDecimal(text);
                } else if (ft == Duration.class) {
                    v = Duration.ofNanos(new BigDecimal(text.replace("s", "")).multiply(BigDecimal.valueOf(1000000000)).longValueExact());
                }
                builder.getClass().getMethod(field.getName(), ft == List.class ? java.util.Collection.class : ft).invoke(builder, v);
            }
            if (!values.isEmpty()) {
                throw new IllegalArgumentException("unknown fixture arguments " + values.keySet());
            }
            DreaminaCanvasRequest<?> result = (DreaminaCanvasRequest<?>) builder.getClass().getMethod("build").invoke(builder);
            result.toArguments();
            return result;
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }
}
