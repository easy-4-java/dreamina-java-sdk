package io.github.easy4j.dreamina.cli.opts;

import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

/** Canvas 静态组合规则；仅处理本地可确定的语法，资源类型和权益仍由官方 CLI 判定。 */
final class DreaminaCanvasInputRules {
    private static final Pattern RESOURCE = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
    private static final List<String> GENERATION = Arrays.asList("mode", "prompt", "model", "ratio", "resolution", "count", "ref", "duration", "voice-id", "voice-name", "scenario");

    private DreaminaCanvasInputRules() {
    }

    /** 根据命令校验参数组合，不改变顺序、默认参数或任务标识。 */
    static void validate(DreaminaCanvasCommand command, Map<String, List<String>> o) {
        require(o.containsKey("x") == o.containsKey("y"), "x and y must be supplied together");
        exclusive(o, "clear-tags", "tag");
        exclusive(o, "clear-description", "description");
        exclusive(o, "clear-voice", "voice");
        exclusive(o, "clear-auxiliary", "auxiliary");
        exclusive(o, "clear-tracks", "clip", "audio-clip");
        exclusive(o, "clear-main", "main", "voice", "auxiliary");
        if (on(o, "clear-generation")) {
            require(!any(o, GENERATION) && !on(o, "run"), "clear-generation conflicts with generation fields or run");
        }
        require(values(o, "tag").size() <= 5, "at most five tags are allowed");
        for (String ref : values(o, "ref")) {
            validateReference(ref);
        }
        for (String binding : Arrays.asList("main", "voice", "auxiliary", "resource-id")) {
            for (String value : values(o, binding)) {
                if (command == DreaminaCanvasCommand.RESOURCE_UPLOAD && "resource-id".equals(binding)) {
                    continue; // 上传幂等 UUID 的大小写按通用 UUID 契约处理。
                }
                require("resource-id".equals(binding) ? resource(value) : identity(value), "invalid resource or node identity");
            }
        }
        if (o.containsKey("resource-id") && command.getPath().startsWith("node create ")) {
            require(o.containsKey("import-kind"), "resource-id requires import-kind");
        }
        if (command.getPath().startsWith("node create ") || command.getPath().startsWith("node edit ")) {
            validateGeneration(command, o);
        }
        if (command == DreaminaCanvasCommand.NODE_CREATE_TEXT) {
            require(StringUtils.isNotBlank(first(o, "title")), "create text requires title");
            require(o.containsKey("text"), "create text requires explicit text");
        }
        if (command == DreaminaCanvasCommand.NODE_CREATE_TIMELINE) {
            require(StringUtils.isNotBlank(first(o, "title")), "create timeline requires title");
        }
        validateDirector(command, o);
        validateSources(o);
        for (String clip : values(o, "clip")) {
            validateClip(clip, false);
        }
        for (String clip : values(o, "audio-clip")) {
            validateClip(clip, true);
        }
        if (command == DreaminaCanvasCommand.NODE_UPSCALE_IMAGE) {
            boolean pro = "pro".equals(StringUtils.defaultString(first(o, "mode"), "pro"));
            require(pro == o.containsKey("resolution"), "pro requires resolution; normal rejects resolution");
            require(pro || !o.containsKey("detail"), "detail requires pro mode");
        }
        if (command == DreaminaCanvasCommand.NODE_DELETE && o.containsKey("delete-token")) {
            require(o.containsKey("update-id"), "delete-token requires update-id");
        }
        if (command == DreaminaCanvasCommand.RESOURCE_UPLOAD && o.containsKey("source-url")) {
            require(!o.containsKey("type") || "image".equals(first(o, "type")), "source-url supports images only");
        }
        if (command.getPath().startsWith("node delete")) {
            Set<String> ids = new HashSet<>(values(o, "node-id"));
            require(!ids.isEmpty() && ids.size() <= 100, "delete requires 1..100 distinct node IDs");
        }
    }

    /** 导演台文件与画布来源互斥；导出选项只按官方公开规则判断。 */
    private static void validateDirector(DreaminaCanvasCommand command, Map<String, List<String>> o) {
        if (!command.getPath().startsWith("director ")) {
            return;
        }
        if (Arrays.asList(DreaminaCanvasCommand.DIRECTOR_SHOW, DreaminaCanvasCommand.DIRECTOR_APPLY,
                DreaminaCanvasCommand.DIRECTOR_EXPORT, DreaminaCanvasCommand.DIRECTOR_PREVIEW).contains(command)) {
            require(o.containsKey("file") != o.containsKey("node-id"), "director requires exactly one file or node-id");
        }
        require(!on(o, "storyboard") || !o.containsKey("camera-id"), "camera-id and storyboard are mutually exclusive");
        if (o.containsKey("version-id")) {
            String version = first(o, "version-id");
            require(version.matches("0|[1-9][0-9]*") && new BigInteger(version).bitLength() <= 64,
                    "version-id requires canonical uint64");
        }
        if (command == DreaminaCanvasCommand.DIRECTOR_EXPORT) {
            String type = StringUtils.defaultString(first(o, "type"), "image");
            boolean canvas = on(o, "to-canvas");
            require(canvas != o.containsKey("output"), "export requires output or to-canvas, exclusively");
            require(!"fbx".equals(type) || o.containsKey("motion-id") && !canvas, "fbx requires motion-id and local output");
            require(!o.containsKey("frame") || "image".equals(type), "frame applies to image export only");
        }
    }

    private static void validateGeneration(DreaminaCanvasCommand command, Map<String, List<String>> o) {
        String path = command.getPath();
        boolean media = path.endsWith(" image") || path.endsWith(" video") || path.endsWith(" audio");
        if (!media) {
            return;
        }
        require(!o.containsKey("submit-id") || on(o, "run") || o.containsKey("resource-id"), "submit-id requires run or resource-id");
        boolean audio = path.endsWith(" audio");
        boolean createRun = path.startsWith("node create ") && on(o, "run");
        if (!audio && (any(o, GENERATION) || createRun)) {
            require(o.containsKey("mode"), "generation fields require mode");
        }
        if (command == DreaminaCanvasCommand.NODE_CREATE_IMAGE) {
            require(o.containsKey("title") || (on(o, "run") && StringUtils.isNotBlank(first(o, "prompt"))), "create image requires title or run with nonblank prompt");
        }
        String mode = first(o, "mode");
        if (audio && Objects.isNull(mode) && (any(o, GENERATION) || createRun)) {
            mode = "tts"; // 保留官方音频命令的默认 TTS 行为，不向 argv 注入 mode。
        }
        if ("t2v".equals(mode)) {
            require(values(o, "ref").isEmpty(), "t2v rejects explicit references");
        }
        if ("first_last_frame".equals(mode)) {
            int count = values(o, "ref").size();
            require(count >= 1 && count <= 2, "first_last_frame requires one or two ordered references");
        }
        if ("i2i".equals(mode)) {
            require(!values(o, "ref").isEmpty(), "i2i requires references");
        }
        if (path.endsWith(" audio") && Objects.nonNull(mode)) {
            require(StringUtils.isNotBlank(first(o, "prompt")), "audio generation requires nonblank prompt");
            require(!(o.containsKey("voice-id") && o.containsKey("voice-name")), "voice-id and voice-name are mutually exclusive");
            if (o.containsKey("voice-id")) {
                String id = first(o, "voice-id");
                require(id.matches("[1-9][0-9]*") && new BigInteger(id).compareTo(BigInteger.valueOf(Long.MAX_VALUE)) <= 0, "voice-id requires canonical positive int64");
            }
            if ("music".equals(mode)) {
                require(o.containsKey("model") && o.containsKey("duration"), "music requires model and duration");
                require(!o.containsKey("voice-id") && !o.containsKey("voice-name") && !o.containsKey("scenario") && !o.containsKey("ref"), "music rejects TTS voice, scenario and references");
            } else {
                String scenario = first(o, "scenario");
                require(!o.containsKey("duration"), "duration is for music only");
                require(Objects.isNull(scenario) || o.containsKey("model"), "scenario requires explicit model");
                require(!o.containsKey("ref") || "omni-dubbing".equals(scenario), "audio references require omni-dubbing");
                require("omni-dubbing".equals(scenario) || o.containsKey("voice-id") || o.containsKey("voice-name"), "TTS requires voice-id or voice-name");
            }
        }
    }

    private static void validateReference(String ref) {
        int colon = ref.indexOf(':');
        require(colon > 0 && colon < ref.length() - 1, "reference requires type:value");
        String kind = ref.substring(0, colon);
        String value = ref.substring(colon + 1);
        require(Arrays.asList("node", "res", "uri", "vid").contains(kind), "unsupported reference type");
        if ("node".equals(kind)) {
            require(node(value), "node reference requires node_ identity");
        } else if ("res".equals(kind)) {
            require(resource(value), "resource reference requires canonical lowercase UUID");
        } else {
            require(StringUtils.isNotBlank(value), "empty reference value");
        }
    }

    private static void validateSources(Map<String, List<String>> o) {
        Set<String> assigned = new HashSet<>(values(o, "resource-id"));
        for (String name : Arrays.asList("main", "voice", "auxiliary")) {
            for (String value : values(o, name)) {
                if (resource(value)) {
                    assigned.add(value);
                }
            }
        }
        for (String ref : values(o, "ref")) {
            if (ref.startsWith("res:")) {
                assigned.add(ref.substring(4));
            }
        }
        for (String name : Arrays.asList("clip", "audio-clip")) {
            for (String clip : values(o, name)) {
                String value = clip.split(",", -1)[0];
                if (resource(value)) {
                    assigned.add(value);
                }
            }
        }
        Map<String, String> sources = new HashMap<>();
        for (String source : values(o, "resource-source")) {
            String[] parts = source.split("@", -1);
            require(parts.length == 2 && resource(parts[0]) && resource(parts[1]), "resource-source requires ResourceUUID@SourceProjectUUID");
            require(assigned.contains(parts[0]), "resource-source must be assigned in this request");
            String previous = sources.put(parts[0], parts[1]);
            require(Objects.isNull(previous) || previous.equals(parts[1]), "conflicting resource-source declarations");
        }
    }

    private static void validateClip(String clip, boolean audio) {
        String[] parts = clip.split(",", -1);
        require(identity(parts[0]), "clip requires node or canonical resource identity");
        Set<String> keys = new HashSet<>();
        for (int i = 1; i < parts.length; i++) {
            String[] option = parts[i].split("=", 2);
            String key = option[0];
            require(keys.add(key), "duplicate clip option");
            if ("muted".equals(key)) {
                require(option.length == 1, "muted is a bare clip option");
                continue;
            }
            require(option.length == 2, "clip option requires a value");
            String value = option[1];
            if ("kind".equals(key)) {
                require(node(parts[0]) ? "canvas_node".equals(value) : "resource".equals(value), "clip kind must match source identity");
            } else if ("duration".equals(key)) {
                require(!audio && integer(value, true), "visual duration requires positive integer milliseconds");
            } else if ("start".equals(key)) {
                require(audio && integer(value, false), "start is a nonnegative audio-only offset");
            } else if ("trim".equals(key)) {
                String[] trim = value.split("-", -1);
                require(trim.length == 2 && integer(trim[0], false) && integer(trim[1], true), "trim requires nonnegative start and positive duration");
            } else if ("speed".equals(key) || "volume".equals(key)) {
                BigDecimal number = decimal(value);
                require("speed".equals(key) ? number.signum() > 0 : number.signum() >= 0 && number.compareTo(BigDecimal.ONE) <= 0, "invalid clip speed or volume");
            } else {
                throw new IllegalArgumentException("unknown clip option");
            }
        }
    }

    private static BigDecimal decimal(String value) {
        try {
            BigDecimal number = new BigDecimal(value);
            require(Double.isFinite(number.doubleValue()), "number must be finite");
            return number;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("invalid finite number", e);
        }
    }

    private static boolean integer(String value, boolean positive) {
        if (!value.matches("[0-9]+")) {
            return false;
        }
        BigInteger number = new BigInteger(value);
        return (positive ? number.signum() > 0 : number.signum() >= 0) && number.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) <= 0;
    }

    private static boolean identity(String value) {
        return node(value) || resource(value);
    }

    private static boolean node(String value) {
        return Objects.nonNull(value) && value.matches("node_[0-9abcdefghjkmnpqrstvwxyz]{10}");
    }

    private static boolean resource(String value) {
        return Objects.nonNull(value) && RESOURCE.matcher(value).matches();
    }

    private static List<String> values(Map<String, List<String>> o, String name) {
        return o.getOrDefault(name, Collections.emptyList());
    }

    private static String first(Map<String, List<String>> o, String name) {
        List<String> values = values(o, name);
        return values.isEmpty() ? null : values.get(0);
    }

    private static boolean any(Map<String, List<String>> o, List<String> names) {
        for (String name : names) {
            if (o.containsKey(name)) {
                return true;
            }
        }
        return false;
    }

    private static boolean on(Map<String, List<String>> o, String name) {
        return "true".equals(first(o, name));
    }

    private static void exclusive(Map<String, List<String>> o, String clear, String... names) {
        if (on(o, clear)) {
            require(!any(o, Arrays.asList(names)), "clear flag conflicts with assigned values");
        }
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }
}
