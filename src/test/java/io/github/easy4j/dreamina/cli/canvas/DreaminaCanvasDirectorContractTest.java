package io.github.easy4j.dreamina.cli.canvas;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.easy4j.dreamina.cli.DreaminaCanvasCliExecutor;
import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;
import io.github.easy4j.dreamina.cli.opts.DreaminaCanvasContract;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/** 公共根契约的 detailCommand 必须继续展开，不能遗漏导演台。 */
class DreaminaCanvasDirectorContractTest {
    @Test void everyDirectorDetailRouteHasNamedRequestAndExecutor() throws Exception {
        try (InputStream in = getClass().getResourceAsStream("/io/github/easy4j/dreamina/cli/opts/schema-director-1.2.0.json")) {
            assertNotNull(in);
            JsonNode schema = new ObjectMapper().readTree(in);
            for (JsonNode command : schema.path("subcommands").get(0).path("subcommands")) {
                String action = command.path("name").asText();
                StringBuilder suffix = new StringBuilder();
                for (String part : action.split("-")) {
                    suffix.append(part.substring(0, 1).toUpperCase(Locale.ROOT)).append(part.substring(1));
                }
                String type = "DreaminaCanvasDirector" + suffix + "Request";
                Class<?> request = Class.forName("io.github.easy4j.dreamina.cli.opts." + type);
                assertNotNull(DreaminaCanvasCliExecutor.class.getMethod("director" + suffix, request));
                DreaminaCanvasCommand route = DreaminaCanvasCommand.valueOf("DIRECTOR_" + action.toUpperCase(Locale.ROOT).replace('-', '_'));
                assertEquals(command.path("flags").size(), DreaminaCanvasContract.describe(route).getFlags().size());
            }
        }
    }
    @Test void directorRecoveryFieldsRemainAccessible() throws Exception {
        String json = "{\"schemaVersion\":\"1\",\"ok\":false,\"error\":{\"code\":\"cli.director_version_conflict\"},\"operationRef\":\"/own/pending.json\",\"directorConflict\":{\"nodeId\":\"node_0000000001\",\"baseVersionId\":\"1\",\"latestVersionId\":\"18446744073709551615\"},\"canvasHandoff\":{\"nodeId\":\"node_0000000001\",\"webUrl\":\"https://example.test/canvas\"}}";
        io.github.easy4j.dreamina.cli.DreaminaCanvasResponse<?> response = new io.github.easy4j.dreamina.cli.parser.DreaminaCanvasResponseParser().parse("director push",
                io.github.easy4j.dreamina.cli.DreaminaCliResult.builder().stdout("").stderr(json).exitCode(20).success(false).build(),
                io.github.easy4j.dreamina.cli.model.DreaminaCanvasDirectorPushResult.class);
        JsonNode error = new ObjectMapper().valueToTree(response);
        assertEquals("/own/pending.json", error.path("operationRef").asText());
        assertEquals("18446744073709551615", error.path("directorConflict").path("latestVersionId").asText());
        assertEquals("node_0000000001", error.path("canvasHandoff").path("nodeId").asText());
    }

    @Test void officialRawDocumentPreservesNestedValues() throws Exception {
        String json = "{\"schemaVersion\":\"1\",\"ok\":true,\"data\":{\"document\":{\"schemaVersion\":5,\"unknownFutureField\":[1,true,{\"x\":1.25}]},\"versionId\":\"18446744073709551615\"}}";
        io.github.easy4j.dreamina.cli.DreaminaCanvasResponse<io.github.easy4j.dreamina.cli.model.DreaminaCanvasDirectorShowResult> response = new io.github.easy4j.dreamina.cli.parser.DreaminaCanvasResponseParser().parse("director show",
                io.github.easy4j.dreamina.cli.DreaminaCliResult.builder().stdout(json).stderr("").exitCode(0).success(true).build(),
                io.github.easy4j.dreamina.cli.model.DreaminaCanvasDirectorShowResult.class);
        assertEquals(new ObjectMapper().readTree(json).path("data").path("document"), new ObjectMapper().readTree(response.getData().getDocument().asJson()));
        assertEquals("18446744073709551615", response.getData().getVersionId());
        assertEquals(new ObjectMapper().readTree(json).path("data").path("document"),
                new ObjectMapper().readTree(new ObjectMapper().writeValueAsString(response.getData())).path("document"));
    }

    @Test void everyAdvertisedDetailCommandIsResolved() {
        for (io.github.easy4j.dreamina.cli.model.DreaminaCanvasSchemaCommand command : DreaminaCanvasContract.schema().getSubcommands()) {
            if (java.util.Objects.nonNull(command.getDetailCommand())) {
                assertEquals("dreamina-canvas schema director", command.getDetailCommand(), "new public detail schema must be explicitly integrated");
                assertFalse(DreaminaCanvasContract.directorSchema().getSubcommands().get(0).getSubcommands().isEmpty());
            }
        }
    }

    @Test void newCommandsAppendWithoutReorderingExistingConstants() throws Exception {
        try (InputStream input = getClass().getResourceAsStream("/cli-contract/canvas-legacy-enum-order.json")) {
            JsonNode previous = new ObjectMapper().readTree(input);
            for (int index = 0; index < previous.size(); index++) {
                assertEquals(previous.get(index).asText(), DreaminaCanvasCommand.values()[index].name(), "preserve existing command order");
            }
        }
    }

    @Test void rawDocumentPreservesHighPrecisionDecimalValues() throws Exception {
        String exact = "1234567890.12345678901234567890";
        String json = "{\"schemaVersion\":\"1\",\"ok\":true,\"data\":{\"document\":{\"precise\":" + exact + "}}}";
        io.github.easy4j.dreamina.cli.DreaminaCanvasResponse<io.github.easy4j.dreamina.cli.model.DreaminaCanvasDirectorShowResult> result = new io.github.easy4j.dreamina.cli.parser.DreaminaCanvasResponseParser().parse("director show",
                io.github.easy4j.dreamina.cli.DreaminaCliResult.builder().stdout(json).stderr("").exitCode(0).success(true).build(), io.github.easy4j.dreamina.cli.model.DreaminaCanvasDirectorShowResult.class);
        JsonNode preserved = new ObjectMapper().enable(com.fasterxml.jackson.databind.DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS).readTree(result.getData().getDocument().asJson());
        assertEquals(0, new java.math.BigDecimal(exact).compareTo(preserved.path("precise").decimalValue()));
    }

}
