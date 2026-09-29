package io.github.easy4j.dreamina.cli.opts;

import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;
import io.github.easy4j.dreamina.cli.model.DreaminaCanvasArgument;
import io.github.easy4j.dreamina.cli.model.DreaminaCanvasSchema;
import io.github.easy4j.dreamina.cli.model.DreaminaCanvasSchemaCommand;
import io.github.easy4j.dreamina.cli.model.DreaminaCanvasSchemaFlag;
import io.github.easy4j.dreamina.cli.parser.DreaminaCliStructuredPayloadMapper;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 随包强类型契约；动态模型和远端资源校验仍由 CLI 执行。
 */
public final class DreaminaCanvasContract {
    private static final Pattern UUID = Pattern.compile("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}");
    private static final Pattern RANGE = Pattern.compile("^(\\d+)\\.\\.(\\d+)(?:;.*)?$");

    private DreaminaCanvasContract() {
    }


    /**
     * 读取独立的契约对象，调用者修改不会污染后续请求。
     */
    public static DreaminaCanvasSchema schema() {
        try (InputStream stream = DreaminaCanvasContract.class.getResourceAsStream("schema-1.0.1.json")) {
            if (Objects.isNull(stream)) {
                throw new IllegalStateException("Missing Canvas schema resource");
            }
            return DreaminaCliStructuredPayloadMapper.defaultObjectMapper().readValue(stream, DreaminaCanvasSchema.class);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read Canvas schema", e);
        }
    }

    /**
     * 返回指定指令的具名参数与响应契约。
     */
    public static DreaminaCanvasSchemaCommand describe(DreaminaCanvasCommand command) {
        List<DreaminaCanvasSchemaCommand> children = schema().getSubcommands();
        DreaminaCanvasSchemaCommand result = null;
        for (String word : command.getPath().split(" ")) {
            result = null;
            for (DreaminaCanvasSchemaCommand child : children) {
                if (word.equals(child.getName())) {
                    result = child;
                    break;
                }
            }
            if (Objects.isNull(result)) {
                throw new IllegalStateException("Missing bundled command contract");
            }
            children = result.getSubcommands();
        }
        return result;
    }

    static void validate(DreaminaCanvasCommand command, DreaminaCanvasArguments args) {
        DreaminaCanvasSchemaCommand spec = describe(command);
        Map<String, List<String>> options = args.options;
        for (DreaminaCanvasSchemaFlag flag : spec.getFlags()) {
            List<String> values = options.get(flag.getName());
            require(!Boolean.TRUE.equals(flag.getRequired()) || Objects.nonNull(values), "missing option " + flag.getName());
            if (Objects.nonNull(values)) {
                for (String value : values) {
                    validateValue(flag.getName(), flag.getType(), StringUtils.defaultString(flag.getConstraints()), value);
                }
            }
        }
        List<DreaminaCanvasArgument> positions = Objects.isNull(spec.getArguments()) ? Collections.emptyList() : spec.getArguments();
        int minimum = (int) positions.stream().filter(p -> Boolean.TRUE.equals(p.getRequired())).count();
        if (command == DreaminaCanvasCommand.MODEL) {
            minimum = 1;
        }
        boolean repeated = !positions.isEmpty() && Boolean.TRUE.equals(positions.get(positions.size() - 1).getRepeatable());
        DreaminaCliRequestSupport.requireRange(args.positions.size(), minimum, repeated ? Integer.MAX_VALUE : positions.size(), "positional argument count");
        for (int i = 0; i < args.positions.size(); i++) {
            String value = args.positions.get(i);
            require(StringUtils.isNotBlank(value) && value.indexOf('\0') < 0, "invalid positional argument");
            DreaminaCanvasArgument position = positions.get(Math.min(i, positions.size() - 1));
            if (StringUtils.startsWith(position.getConstraints(), "UUID")) {
                require(UUID.matcher(value).matches(), "invalid operation reference UUID");
            }
        }
        validateRelations(command, options);
    }

    private static void validateValue(String name, String type, String constraints, String value) {
        if ("boolean".equals(type)) {
            require("true".equals(value) || "false".equals(value), name + " requires Boolean");
        } else if ("integer".equals(type) || "number".equals(type)) {
            if ("integer".equals(type)) {
                new BigInteger(value);
            }
            BigDecimal numeric = new BigDecimal(value.toString());
            if (constraints.startsWith(">=0")) {
                require(numeric.signum() >= 0, name + " must be nonnegative");
            } else if (constraints.startsWith(">0")) {
                require(numeric.signum() > 0, name + " must be positive");
            }
            if (constraints.contains("int32")) {
                require(numeric.compareTo(BigDecimal.valueOf(Integer.MAX_VALUE)) <= 0, name + " exceeds int32");
            }
            Matcher range = RANGE.matcher(constraints);
            if (range.matches()) {
                require(numeric.compareTo(new BigDecimal(range.group(1))) >= 0 && numeric.compareTo(new BigDecimal(range.group(2))) <= 0, name + " outside range");
            }
        } else if ("duration".equals(type)) {
            BigDecimal nanos = durationNanos(value, name);
            require(nanos.signum() >= 0 && nanos.compareTo(BigDecimal.valueOf(Long.MAX_VALUE)) <= 0, name + " duration outside range");
            require(!"interval".equals(name) || nanos.signum() > 0, "interval must be positive");
        } else {
            String text = value;
            require(text.indexOf('\0') < 0, name + " contains NUL");
            if (!Arrays.asList("prompt", "text", "description").contains(name)) {
                require(StringUtils.isNotBlank(text), name + " must be nonblank");
            }
            if ("title".equals(name)) {
                require(text.length() <= 512, "title exceeds 512 UTF-16 units");
            }
            if (constraints.startsWith("UUID") || constraints.startsWith("unique UUIDs")) {
                require(UUID.matcher(text).matches(), name + " requires UUID");
            }
            String enumPart = constraints.split("[;(]", 2)[0].trim();
            if (enumPart.matches("[\\w-]+(?:\\s*\\|\\s*[\\w-]+)+")) {
                require(Arrays.asList(enumPart.split("\\s*\\|\\s*")).contains(text), name + " outside allowed values");
            }
        }
    }

    private static BigDecimal durationNanos(String value, String name) {
        require(StringUtils.endsWith(value, "ns"), name + " invalid duration");
        try {
            return new BigDecimal(value.substring(0, value.length() - 2));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(name + " invalid duration", e);
        }
    }

    private static void validateRelations(DreaminaCanvasCommand command, Map<String, List<String>> o) {
        require(!(o.containsKey("credit-token") && o.containsKey("credit-ceiling")), "credit-token and credit-ceiling are mutually exclusive");
        if (command.getPath().startsWith("node create ") || command.getPath().startsWith("node edit ")) {
            for (String key : Arrays.asList("credit-token", "credit-ceiling")) {
                require(!o.containsKey(key) || enabled(o, "run"), key + " requires run");
            }
            require(!enabled(o, "wait") || enabled(o, "run"), "wait requires run");
        }
        if (command.getPath().startsWith("node ")) {
            require(!(o.containsKey("timeout") || o.containsKey("interval")) || enabled(o, "wait"), "timeout/interval requires wait");
        }
        if (command == DreaminaCanvasCommand.NODE_RUN || command == DreaminaCanvasCommand.NODE_UPSCALE_IMAGE) {
            if (o.containsKey("submit-id")) {
                require(o.get("submit-id").size() == o.get("node-id").size(), "submit-id count must equal node-id count");
                require(new HashSet<>(o.get("submit-id")).size() == o.get("submit-id").size(), "submit-id values must be unique");
            }
        }
        if (command == DreaminaCanvasCommand.RESOURCE_UPLOAD) {
            require(o.containsKey("file") != o.containsKey("source-url"), "exactly one of file/source-url required");
        }
    }

    private static boolean enabled(Map<String, List<String>> options, String name) {
        return options.containsKey(name) && "true".equals(options.get(name).get(0));
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }
}
