package io.github.easy4j.dreamina.cli.parser;

import io.github.easy4j.dreamina.cli.DreaminaCliResponse;
import io.github.easy4j.dreamina.cli.DreaminaCliResult;
import io.github.easy4j.dreamina.cli.model.DreaminaCheckLogin;
import io.github.easy4j.dreamina.cli.model.DreaminaDeviceLogin;
import io.github.easy4j.dreamina.cli.model.DreaminaGenerateSubmit;
import io.github.easy4j.dreamina.cli.model.DreaminaHelp;
import io.github.easy4j.dreamina.cli.model.DreaminaLogin;
import io.github.easy4j.dreamina.cli.model.DreaminaLoginAccount;
import io.github.easy4j.dreamina.cli.model.DreaminaLogout;
import io.github.easy4j.dreamina.cli.model.DreaminaQueryResult;
import io.github.easy4j.dreamina.cli.model.DreaminaRelogin;
import io.github.easy4j.dreamina.cli.model.DreaminaQueueInfoSupport;
import io.github.easy4j.dreamina.cli.model.DreaminaSessionDelete;
import io.github.easy4j.dreamina.cli.model.DreaminaSessionList;
import io.github.easy4j.dreamina.cli.model.DreaminaSessionMutation;
import io.github.easy4j.dreamina.cli.model.DreaminaSessionRow;
import io.github.easy4j.dreamina.cli.model.DreaminaSessionSearch;
import io.github.easy4j.dreamina.cli.model.DreaminaTaskItem;
import io.github.easy4j.dreamina.cli.model.DreaminaUserCredit;
import io.github.easy4j.dreamina.cli.model.DreaminaVersion;
import io.github.easy4j.dreamina.util.DreaminaStrings;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;

/**
 * Converts {@link DreaminaCliResult} to {@link DreaminaCliResponse} (raw output + parsed body).
 * <p>
 * For JSON commands, {@code body} is the corresponding {@code cli.model} type matching CLI fields;
 * text/table commands follow the same pattern.
 * </p>
 *
 * @see DreaminaCliResult
 * @see DreaminaCliResponse
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 */
@Slf4j
public final class DreaminaCliStructuredPayloadMapper {

    private static final TypeReference<List<DreaminaTaskItem>> TASK_LIST_TYPE =
        new TypeReference<>() {};

    private static final Pattern SESSION_LIST_ROW = Pattern.compile(
        "^(?<id>\\d+)\\s+(?<name>.+?)\\s+(?<pinned>Yes|No)\\s+(?<updated>\\d{4}-\\d{2}-\\d{2}\\s+\\d{2}:\\d{2})$");

    private static final Pattern SESSION_SEARCH_ROW = Pattern.compile(
        "^(?<id>\\d+)\\s+(?<name>.+?)\\s+(?<updated>\\d{4}-\\d{2}-\\d{2}\\s+\\d{2}:\\d{2})$");

    private static final Pattern SESSION_CREATED = Pattern.compile(
        "Created\\s+session\\s+\"([^\"]+)\"\\s+\\(ID:\\s*(\\d+)\\)\\s*", Pattern.CASE_INSENSITIVE);

    private static final Pattern SESSION_RENAMED = Pattern.compile(
        "Renamed\\s+session\\s+(\\d+)\\s+to\\s+\"([^\"]+)\"\\s*", Pattern.CASE_INSENSITIVE);

    private final ObjectMapper objectMapper;

    /**
     * Default constructor: creates a lenient JSON mapper.
     */
    public DreaminaCliStructuredPayloadMapper() {
        this(defaultObjectMapper());
    }

    /**
     * Allows test injection of a custom {@link ObjectMapper}.
     *
     * @param objectMapper Jackson ObjectMapper; must not be null
     */
    public DreaminaCliStructuredPayloadMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Default Jackson configuration for the Dreamina module (ignores unknown fields, compatible with CLI evolution).
     *
     * @return A new ObjectMapper instance
     */
    public static ObjectMapper defaultObjectMapper() {
        ObjectMapper om = new ObjectMapper();
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return om;
    }

    /**
     * Maps {@code version} command output.
     *
     * @param raw CLI snapshot; must not be null
     */
    public DreaminaCliResponse<DreaminaVersion> mapVersion(DreaminaCliResult raw) {
        JsonNode root = tryParseJsonTree(raw);
        return DreaminaCliResponse.of(raw, readPayload(root, DreaminaVersion.class), root);
    }

    /**
     * Maps {@code user_credit} output.
     *
     * @param raw CLI 快照；不得为 null
     */
    public DreaminaCliResponse<DreaminaUserCredit> mapUserCredit(DreaminaCliResult raw) {
        JsonNode root = tryParseJsonTree(raw);
        return DreaminaCliResponse.of(raw, readPayload(root, DreaminaUserCredit.class), root);
    }

    /**
     * Maps {@code help} output.
     *
     * @param topic Subcommand topic; {@code null} for root help
     * @param raw   CLI 快照；不得为 null
     */
    public DreaminaCliResponse<DreaminaHelp> mapHelp(String topic, DreaminaCliResult raw) {
        return DreaminaCliResponse.of(raw, DreaminaHelp.builder().topic(topic).build(), null);
    }

    /**
     * Maps {@code logout} output.
     *
     * @param raw CLI 快照；不得为 null
     */
    public DreaminaCliResponse<DreaminaLogout> mapLogout(DreaminaCliResult raw) {
        boolean cleared = DreaminaLoginTextParser.detectsLogoutCleared(combinedText(raw));
        return DreaminaCliResponse.of(
            raw,
            DreaminaLogout.builder().localSessionCleared(cleared ? Boolean.TRUE : null).build(),
            null);
    }

    /**
     * Maps {@code relogin} output.
     *
     * @param raw CLI 快照；不得为 null
     */
    public DreaminaCliResponse<DreaminaRelogin> mapRelogin(DreaminaCliResult raw) {
        JsonNode root = tryParseJsonTree(raw);
        DreaminaDeviceLogin device = resolveDeviceLogin(raw);
        boolean browser = DreaminaLoginTextParser.detectsDeviceFlowBrowserPrompt(combinedText(raw));
        Boolean requiresBrowser = browser || (device != null && device.isMaterialPresent()) ? Boolean.TRUE : null;
        return DreaminaCliResponse.of(
            raw,
            DreaminaRelogin.builder().requiresBrowserOAuth(requiresBrowser).device(device).build(),
            root);
    }

    /**
     * Maps {@code login checklogin} JSON.
     *
     * @param raw CLI 快照；不得为 null
     */
    public DreaminaCliResponse<DreaminaCheckLogin> mapCheckLogin(DreaminaCliResult raw) {
        JsonNode root = tryParseJsonTree(raw);
        return DreaminaCliResponse.of(raw, readPayload(root, DreaminaCheckLogin.class), root);
    }

    /**
     * Maps {@code list_task} JSON array.
     *
     * @param raw CLI 快照；不得为 null
     */
    public DreaminaCliResponse<List<DreaminaTaskItem>> mapTaskList(DreaminaCliResult raw) {
        JsonNode root = tryParseJsonTree(raw);
        return DreaminaCliResponse.of(raw, readTaskList(root), root);
    }

    /**
     * Maps {@code query_result} output.
     *
     * @param raw CLI 快照；不得为 null
     */
    public DreaminaCliResponse<DreaminaQueryResult> mapQueryResult(DreaminaCliResult raw) {
        JsonNode root = tryParseJsonTree(raw);
        DreaminaQueryResult body = readPayload(root, DreaminaQueryResult.class);
        if (body != null) {
            DreaminaQueueInfoSupport.enrichParsedDebugInfo(objectMapper, body.getQueueInfo());
            if (DreaminaStrings.isBlank(body.getSubmitId())
                && raw.getParsed() != null
                && DreaminaStrings.isNotBlank(raw.getParsed().getSubmitId())) {
                body.setSubmitId(raw.getParsed().getSubmitId());
            }
        }
        return DreaminaCliResponse.of(raw, body, root);
    }

    /**
     * Maps the standard return JSON from asynchronous generation commands.
     *
     * @param raw CLI 快照；不得为 null
     */
    public DreaminaCliResponse<DreaminaGenerateSubmit> mapGenerateSubmit(DreaminaCliResult raw) {
        JsonNode root = tryParseJsonTree(raw);
        DreaminaGenerateSubmit body = readPayload(root, DreaminaGenerateSubmit.class);
        if (body != null) {
            DreaminaQueueInfoSupport.enrichParsedDebugInfo(objectMapper, body.getQueueInfo());
            if (DreaminaStrings.isBlank(body.getSubmitId())
                && raw.getParsed() != null
                && DreaminaStrings.isNotBlank(raw.getParsed().getSubmitId())) {
                body.setSubmitId(raw.getParsed().getSubmitId());
            }
        }
        return DreaminaCliResponse.of(raw, body, root);
    }

    /**
     * Maps {@code session list} table output.
     *
     * @param raw CLI 快照；不得为 null
     */
    public DreaminaCliResponse<DreaminaSessionList> mapSessionList(DreaminaCliResult raw) {
        List<DreaminaSessionRow> rows = parseSessionRows(combinedText(raw), TableKind.FULL);
        return DreaminaCliResponse.of(raw, DreaminaSessionList.builder().rows(rows).build(), null);
    }

    /**
     * Maps {@code session search} output.
     *
     * @param queryTerm Caller-side keyword snapshot; may be null
     * @param raw       CLI 快照；不得为 null
     */
    public DreaminaCliResponse<DreaminaSessionSearch> mapSessionSearch(String queryTerm, DreaminaCliResult raw) {
        List<DreaminaSessionRow> rows = parseSessionRows(combinedText(raw), TableKind.SEARCH);
        return DreaminaCliResponse.of(
            raw,
            DreaminaSessionSearch.builder().queryTerm(queryTerm).rows(rows).build(),
            null);
    }

    /**
     * Maps {@code session delete}/{@code rm} output.
     *
     * @param raw CLI 快照；不得为 null
     */
    public DreaminaCliResponse<DreaminaSessionDelete> mapSessionDelete(DreaminaCliResult raw) {
        boolean deleted = combinedText(raw).trim().equalsIgnoreCase("deleted");
        return DreaminaCliResponse.of(raw, DreaminaSessionDelete.builder().deleted(deleted).build(), null);
    }

    /**
     * Maps {@code session create}/{@code rename} output.
     *
     * @param raw CLI 快照；不得为 null
     */
    public DreaminaCliResponse<DreaminaSessionMutation> mapSessionMutation(DreaminaCliResult raw) {
        String combined = combinedText(raw).trim();
        Matcher c = SESSION_CREATED.matcher(combined);
        if (c.matches()) {
            return DreaminaCliResponse.of(
                raw,
                DreaminaSessionMutation.builder()
                    .kind(DreaminaSessionMutation.Kind.CREATE)
                    .sessionId(c.group(2))
                    .sessionName(c.group(1))
                    .build(),
                null);
        }
        Matcher r = SESSION_RENAMED.matcher(combined);
        if (r.matches()) {
            return DreaminaCliResponse.of(
                raw,
                DreaminaSessionMutation.builder()
                    .kind(DreaminaSessionMutation.Kind.RENAME)
                    .sessionId(r.group(1))
                    .sessionName(r.group(2))
                    .build(),
                null);
        }
        return DreaminaCliResponse.of(
            raw,
            DreaminaSessionMutation.builder().kind(DreaminaSessionMutation.Kind.UNKNOWN).build(),
            null);
    }

    /**
     * Maps the general output of {@code login --headless} / {@code login}.
     *
     * @param raw CLI 快照；不得为 null
     */
    public DreaminaCliResponse<DreaminaLogin> mapLogin(DreaminaCliResult raw) {
        JsonNode root = tryParseJsonTree(raw);
        String combined = combinedText(raw);
        boolean reuseDetected = DreaminaLoginTextParser.detectsOAuthReuse(combined);
        DreaminaLoginAccount account = DreaminaLoginTextParser.parseReusedAccount(combined);
        Boolean reusedFlag = reuseDetected ? Boolean.TRUE : (account != null ? Boolean.TRUE : null);
        DreaminaLogin body = DreaminaLogin.builder()
            .oauthSessionReused(reusedFlag)
            .account(account)
            .device(resolveDeviceLogin(raw))
            .build();
        return DreaminaCliResponse.of(raw, body, root);
    }

    /**
     * Maps Device Flow material.
     */
    public DreaminaCliResponse<DreaminaDeviceLogin> mapDeviceLogin(DreaminaCliResult raw) {
        JsonNode root = tryParseJsonTree(raw);
        return DreaminaCliResponse.of(raw, resolveDeviceLogin(raw), root);
    }

    /**
     * Resolves Device Flow: prefers JSON, then key-value text.
     */
    private DreaminaDeviceLogin resolveDeviceLogin(DreaminaCliResult raw) {
        JsonNode root = tryParseJsonTree(raw);
        DreaminaDeviceLogin fromJson = readPayload(root, DreaminaDeviceLogin.class);
        if (DreaminaLoginTextParser.hasDeviceFlowMaterial(fromJson)) {
            return fromJson;
        }
        DreaminaDeviceLogin fromText = DreaminaLoginTextParser.parseDeviceFlow(combinedText(raw));
        return DreaminaLoginTextParser.hasDeviceFlowMaterial(fromText) ? fromText : null;
    }

    private static String combinedText(DreaminaCliResult raw) {
        return DreaminaCliResponse.of(raw, null).getCombinedText();
    }

    private enum TableKind {
        FULL,
        SEARCH
    }

    /**
     * Uses Jackson to deserialize a JSON root node to the specified type.
     *
     * @param root JSON root; may be null
     * @param type Target type
     * @param <T>  类型参数
     * @return Payload or null
     */
    private <T> T readPayload(JsonNode root, Class<T> type) {
        return treeToValue(root, type);
    }

    /**
     * Deserializes a JSON array root into a task list.
     *
     * @param root JSON root
     * @return Task list or null
     */
    private List<DreaminaTaskItem> readTaskList(JsonNode root) {
        if (root == null || !root.isArray()) {
            return null;
        }
        try {
            return objectMapper.convertValue(root, TASK_LIST_TYPE);
        } catch (Exception ex) {
            log.trace("Dreamina task list convert failed snippet={}", summarized(root.toString()), ex);
            return null;
        }
    }

    /**
     * Parses session table rows into a unified row list.
     *
     * @param combined Text
     * @param kind     Table form
     */
    private List<DreaminaSessionRow> parseSessionRows(String combined, TableKind kind) {
        List<DreaminaSessionRow> rows = new ArrayList<>();
        boolean seenHeader = false;
        try (BufferedReader br = new BufferedReader(new StringReader(combined))) {
            String line;
            while ((line = br.readLine()) != null) {
                String t = line.trim();
                if (t.isEmpty()) {
                    continue;
                }
                if (t.startsWith("Found ") && t.contains("sessions")) {
                    continue;
                }
                if (t.startsWith("ID ") && t.contains("NAME")) {
                    seenHeader = true;
                    continue;
                }
                if (t.startsWith("--")) {
                    seenHeader = true;
                    continue;
                }
                if (!seenHeader
                    && (t.startsWith("ID ")
                        || SESSION_LIST_ROW.matcher(t).matches()
                        || SESSION_SEARCH_ROW.matcher(t).matches())) {
                    seenHeader = true;
                }
                if (!seenHeader) {
                    continue;
                }
                DreaminaSessionRow row = tryParseSessionRow(t, kind);
                if (row != null) {
                    rows.add(row);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("unexpected IO while parsing session rows", e);
        }
        return rows;
    }

    /**
     * Single row parsing: prefers full four columns, then search three columns.
     */
    private DreaminaSessionRow tryParseSessionRow(String line, TableKind preferred) {
        Matcher full = SESSION_LIST_ROW.matcher(line);
        Matcher search = SESSION_SEARCH_ROW.matcher(line);
        if (preferred == TableKind.FULL && full.matches()) {
            return DreaminaSessionRow.builder()
                .id(full.group("id"))
                .name(full.group("name").trim())
                .pinned(full.group("pinned"))
                .updatedAt(full.group("updated"))
                .build();
        }
        if (preferred == TableKind.SEARCH && search.matches()) {
            return DreaminaSessionRow.builder()
                .id(search.group("id"))
                .name(search.group("name").trim())
                .pinned(null)
                .updatedAt(search.group("updated"))
                .build();
        }
        if (full.matches()) {
            return DreaminaSessionRow.builder()
                .id(full.group("id"))
                .name(full.group("name").trim())
                .pinned(full.group("pinned"))
                .updatedAt(full.group("updated"))
                .build();
        }
        if (search.matches()) {
            return DreaminaSessionRow.builder()
                .id(search.group("id"))
                .name(search.group("name").trim())
                .pinned(null)
                .updatedAt(search.group("updated"))
                .build();
        }
        return null;
    }

    /**
     * Attempts to parse JSON: prefers stdout, then bracket-scans the combined text.
     */
    private JsonNode tryParseJsonTree(DreaminaCliResult raw) {
        JsonNode direct = tryParseSingle(raw.getStdout());
        if (direct != null) {
            return direct;
        }
        JsonNode fromCombined = tryParseSingle(DreaminaCliJsonExtract.extractFirstBalancedJson(mergeStreams(raw)));
        if (fromCombined != null) {
            return fromCombined;
        }
        return tryParseSingle(raw.getStderr());
    }

    private JsonNode tryParseSingle(String payload) {
        if (DreaminaStrings.isBlank(payload)) {
            return null;
        }
        String trimmed = payload.trim();
        try {
            return objectMapper.readTree(trimmed);
        } catch (Exception ignored) {
            String extracted = DreaminaCliJsonExtract.extractFirstBalancedJson(trimmed);
            if (extracted == null) {
                return null;
            }
            try {
                return objectMapper.readTree(extracted);
            } catch (Exception ex) {
                log.trace("Dreamina JSON parse failed snippet={}", summarized(extracted), ex);
                return null;
            }
        }
    }

    private static String summarized(String s) {
        if (s.length() <= 256) {
            return s;
        }
        return s.substring(0, 256) + "...";
    }

    private static String mergeStreams(DreaminaCliResult raw) {
        String out = raw.getStdout() == null ? "" : raw.getStdout();
        String err = raw.getStderr() == null ? "" : raw.getStderr();
        if (err.isEmpty()) {
            return out;
        }
        if (out.isEmpty()) {
            return err;
        }
        return out + "\n" + err;
    }

    /**
     * Deserializes a JSON subtree into a strongly-typed object; returns {@code null} when the node is missing or parsing fails.
     */
    private <T> T treeToValue(JsonNode node, Class<T> type) {
        if (node == null || node.isNull()) {
            return null;
        }
        try {
            return objectMapper.treeToValue(node, type);
        } catch (Exception ex) {
            log.trace(
                "Dreamina treeToValue failed type={} snippet={}",
                type.getSimpleName(),
                summarized(node.toString()),
                ex);
            return null;
        }
    }

}
