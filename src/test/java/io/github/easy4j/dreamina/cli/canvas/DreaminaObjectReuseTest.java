package io.github.easy4j.dreamina.cli.canvas;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.easy4j.dreamina.cli.DreaminaCliResult;
import io.github.easy4j.dreamina.cli.opts.DreaminaCliArgumentProvider;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class DreaminaObjectReuseTest {
    @Test
    void everyNewProductionTypeHasDreaminaPrefix() throws Exception {
        try (Stream<Path> files = Files.walk(Paths.get("src/main/java"))) {
            files.filter(p -> p.toString().endsWith(".java") && !p.getFileName().toString().equals("package-info.java"))
                    .forEach(p -> assertTrue(p.getFileName().toString().startsWith("Dreamina") || p.getFileName().toString().equals("SubprocessExecutionSupport.java"), p.toString()));
        }
    }

    @Test
    void newRequestUsesExistingArgumentInterface() throws Exception {
        assertTrue(DreaminaCliArgumentProvider.class.isAssignableFrom(Class.forName("io.github.easy4j.dreamina.cli.opts.DreaminaCanvasRequest")));
    }

    @Test
    void canvasResponseReusesExistingTransportResponse() throws Exception {
        Class<?> type = Class.forName("io.github.easy4j.dreamina.cli.DreaminaCanvasResponse");
        assertEquals(DreaminaCliResult.class, type.getMethod("getRaw").getReturnType());
    }

    @Test
    void allExistingAndNewTypesHaveExplicitReuseDecision() throws Exception {
        java.util.List<java.util.Map<String, String>> inventory = new ObjectMapper().readValue(Paths.get("docs/validation/dreamina-object-reuse.json").toFile(), new com.fasterxml.jackson.core.type.TypeReference<java.util.List<java.util.Map<String, String>>>() {
        });
        Set<String> expected = new HashSet<>();
        for (java.util.Map<String, String> entry : inventory) {
            String path = entry.get("path");
            assertTrue(expected.add(path));
            assertFalse(entry.get("reason").trim().isEmpty(), path);
            String name = path.substring("src/main/java/".length(), path.length() - 5).replace('/', '.');
            assertEquals(entry.get("status").equals("legacy-only"), Class.forName(name).isAnnotationPresent(Deprecated.class), name);
        }
        Set<String> actual = new HashSet<>();
        try (Stream<Path> files = Files.walk(Paths.get("src/main/java"))) {
            files.filter(p -> p.toString().endsWith(".java") && !p.getFileName().toString().equals("package-info.java")).forEach(p -> actual.add(p.toString()));
        }
        assertEquals(actual, expected);
    }

    @Test
    void sharedArgumentInterfacePreservesLiteralPayloadAndEnumValues() {
        io.github.easy4j.dreamina.cli.opts.DreaminaCanvasRequest request = io.github.easy4j.dreamina.cli.opts.DreaminaCanvasNodeCreateImageRequest.builder()
                .prompt("  text\n$(echo NO)  ")
                .ratio(io.github.easy4j.dreamina.cli.opts.DreaminaRatio.RATIO_1_1)
                .resolution(io.github.easy4j.dreamina.cli.opts.DreaminaImageResolutionType.RESOLUTION_1K).build();
        DreaminaCliArgumentProvider provider = request;
        assertEquals(java.util.Arrays.asList("--prompt=  text\n$(echo NO)  ", "--ratio=1:1", "--resolution=1K"), provider.toCliArgs());
        assertEquals(java.util.Arrays.asList("node", "create", "image"), request.toArguments().subList(0, 3));
        assertEquals("--resolution=1080p", io.github.easy4j.dreamina.cli.opts.DreaminaCanvasNodeCreateVideoRequest.builder()
                .resolution(io.github.easy4j.dreamina.cli.opts.DreaminaVideoResolutionType.RESOLUTION_1080P).build().toCliArgs().get(0));
    }

    @Test
    void sharedResponsePreservesRawIdentityAndCanvasFailureSemantics() {
        DreaminaCliResult raw = DreaminaCliResult.builder().stdout("out").stderr("err").exitCode(0).success(true).build();
        io.github.easy4j.dreamina.cli.DreaminaCanvasResponse<io.github.easy4j.dreamina.cli.model.DreaminaVersion> canvas = new io.github.easy4j.dreamina.cli.DreaminaCanvasResponse<>();
        canvas.setRaw(raw);
        canvas.setOk(false);
        assertSame(raw, canvas.getRaw());
        assertFalse(canvas.isSuccess());
        assertEquals("out", canvas.getStdout());
    }

    @Test
    void originalVersionAndDeviceDtosAcceptBothWireFormats() throws Exception {
        ObjectMapper mapper = io.github.easy4j.dreamina.cli.parser.DreaminaCliStructuredPayloadMapper.defaultObjectMapper();
        for (String key : java.util.Arrays.asList("build_time", "buildTime")) {
            assertEquals("now", mapper.readValue("{\"" + key + "\":\"now\"}", io.github.easy4j.dreamina.cli.model.DreaminaVersion.class).getBuildTime());
        }
        io.github.easy4j.dreamina.cli.model.DreaminaDeviceLogin device = mapper.readValue("{\"deviceCode\":\"d\",\"verificationUri\":\"https://example.test\",\"userCode\":\"u\",\"expiresAt\":\"later\",\"interval\":5}", io.github.easy4j.dreamina.cli.model.DreaminaDeviceLogin.class);
        assertEquals("d", device.getDeviceCode());
        assertEquals("later", device.getExpiresAt());
        assertEquals("5", device.getPollInterval());
        assertTrue(device.isMaterialPresent());
    }
}
