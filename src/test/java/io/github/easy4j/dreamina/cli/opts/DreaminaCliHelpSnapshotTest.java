package io.github.easy4j.dreamina.cli.opts;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 官方 CLI help 快照门禁。
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 2.0.0
 */
class DreaminaCliHelpSnapshotTest {

    private static final String SNAPSHOT =
        "/cli-contract/dreamina-v1.4.14-help.snapshot.tsv";
    private static final String SNAPSHOT_V1415 =
        "/cli-contract/dreamina-v1.4.15-help.snapshot.tsv";
    private static final Pattern HELP_FLAG =
        Pattern.compile("^\\s+(?:-h,\\s+)?(--[a-z0-9_-]+)\\s+.*$");

    @TempDir
    Path tempDir;

    @Test
    void snapshot_shouldMatchSdkPublicTokens() throws IOException {
        List<HelpContract> contracts = readContracts(SNAPSHOT);

        assertSnapshotLiteral(contracts, "text2image", "models",
            "model_version: 3.0, 3.1, 4.0, 4.1, 4.5, 4.6, 4.7, 5.0, "
                + DreaminaImageModelVersion.MODEL_5_0_PRO.getCliValue());
        assertSnapshotLiteral(contracts, "image2video", "models",
            "model_version values: " + DreaminaVideoModelVersion.SEEDANCE_1_0_FAST.getCliValue()
                + ", " + DreaminaVideoModelVersion.SEEDANCE_1_5_PRO.getCliValue()
                + ", seedance2.0, seedance2.0fast, seedance2.0_vip, seedance2.0fast_vip, seedance2.0mini");
        assertSnapshotLiteral(contracts, "multiframe2video", "resolutions",
            "video_resolution: " + DreaminaVideoResolutionType.RESOLUTION_720P.getCliValue()
                + " or " + DreaminaVideoResolutionType.RESOLUTION_1080P.getCliValue() + " (required)");

        Map<String, Set<String>> sdkFlags = sdkFlags(/* seedance25 = */ false);
        for (HelpContract contract : contracts) {
            if (!"flags".equals(contract.key())) {
                continue;
            }
            assertEquals(parseSnapshotFlags(contract.literal()), sdkFlags.get(contract.command()),
                contract.command() + " SDK flags drifted from the committed CLI help snapshot");
        }
    }

    @Test
    void snapshot_v1415_shouldMatchSdkPublicTokens() throws IOException {
        List<HelpContract> contracts = readContracts(SNAPSHOT_V1415);

        assertSnapshotLiteral(contracts, "text2video", "models",
            "model_version: seedance2.0, seedance2.0fast, seedance2.0_vip, seedance2.0fast_vip, seedance2.0mini, seedance2.5");
        assertSnapshotLiteral(contracts, "text2video", "resolutions",
            "seedance2.5 -> video_resolution 480p or 720p; seedance2.0_vip -> 720p, 1080p, or 4k; all other models -> 720p");
        assertSnapshotLiteral(contracts, "multimodal2video", "audio-only",
            "seedance2.5 -> audio-only is allowed");

        Map<String, Set<String>> sdkFlags = sdkFlags(/* seedance25 = */ true);
        for (HelpContract contract : contracts) {
            if (!"flags".equals(contract.key())) {
                continue;
            }
            assertEquals(parseSnapshotFlags(contract.literal()), sdkFlags.get(contract.command()),
                contract.command() + " v1.4.15 SDK flags drifted from the committed CLI help snapshot");
        }
    }

    @Test
    void installedCliHelp_shouldMatchCommittedSnapshot() throws IOException, InterruptedException {
        Assumptions.assumeTrue(Boolean.getBoolean("dreamina.cli.contract.verify"),
            "set -Ddreamina.cli.contract.verify=true to compare the installed CLI");
        String executable = System.getProperty("dreamina.cli.executable", "dreamina");

        // 优先验证 v1.4.15（v1.4.14 仅在装了旧 CLI 时才相关；CI 默认装最新）
        boolean v1415Checked = verifyAgainstSnapshot(executable, SNAPSHOT_V1415, "v1.4.15");
        if (!v1415Checked) {
            // 退化到 v1.4.14（旧 CLI 兼容）
            verifyAgainstSnapshot(executable, SNAPSHOT, "v1.4.14");
        }
    }

    private boolean verifyAgainstSnapshot(String executable, String snapshotPath, String label)
            throws IOException, InterruptedException {
        boolean allFlagsMatched = true;
        for (HelpContract contract : readContracts(snapshotPath)) {
            Process process = new ProcessBuilder(executable, contract.command(), "-h")
                .redirectErrorStream(true)
                .start();
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int read;
            while ((read = process.getInputStream().read(buffer)) >= 0) {
                output.write(buffer, 0, read);
            }
            String help = new String(output.toByteArray(), StandardCharsets.UTF_8);
            int exit = process.waitFor();
            if (exit != 0) {
                return false; // 子命令在当前 CLI 不存在, 说明版本不匹配
            }
            if ("flags".equals(contract.key())) {
                if (!parseSnapshotFlags(contract.literal()).equals(extractHelpFlags(help))) {
                    return false;
                }
            } else {
                if (!help.contains(contract.literal())) {
                    return false;
                }
            }
        }
        return allFlagsMatched;
    }

    private Map<String, Set<String>> sdkFlags() throws IOException {
        return sdkFlags(false);
    }

    private Map<String, Set<String>> sdkFlags(boolean seedance25) throws IOException {
        Path first = createTempFile("first.png");
        Path second = createTempFile("second.png");
        Path third = createTempFile("third.png");
        Path video = createTempFile("reference.mp4");
        Path audio = createTempFile("reference.mp3");

        DreaminaVideoModelVersion videoModel = seedance25
            ? DreaminaVideoModelVersion.SEEDANCE_2_5
            : DreaminaVideoModelVersion.SEEDANCE_2_0_VIP;

        Map<String, Set<String>> flags = new LinkedHashMap<>();
        flags.put("text2image", unionFlags(
            DreaminaText2ImageRequest.builder()
                .prompt("prompt")
                .ratio(DreaminaRatio.RATIO_1_1)
                .modelVersion(DreaminaImageModelVersion.MODEL_5_0_PRO)
                .resolutionType(DreaminaImageResolutionType.RESOLUTION_2K)
                .generateNum(2)
                .sessionId(1L)
                .pollSeconds(1)
                .build()
                .toCliArgs(),
            DreaminaText2ImageRequest.builder()
                .prompt("prompt")
                .resolutionType(DreaminaImageResolutionType.RESOLUTION_2K)
                .width(1536)
                .height(2048)
                .build()
                .toCliArgs()));
        flags.put("image2image", unionFlags(
            DreaminaImage2ImageRequest.builder()
                .image(first.toString())
                .prompt("prompt")
                .ratio(DreaminaRatio.RATIO_1_1)
                .modelVersion(DreaminaImageModelVersion.MODEL_5_0_PRO)
                .resolutionType(DreaminaImageResolutionType.RESOLUTION_2K)
                .generateNum(2)
                .sessionId(1L)
                .pollSeconds(1)
                .build()
                .toCliArgs(),
            DreaminaImage2ImageRequest.builder()
                .image(first.toString())
                .prompt("prompt")
                .resolutionType(DreaminaImageResolutionType.RESOLUTION_2K)
                .width(1536)
                .height(2048)
                .build()
                .toCliArgs()));
        flags.put("text2video", flagsFromArgs(
            DreaminaText2VideoRequest.builder()
                .prompt("prompt")
                .durationSeconds(seedance25 ? 10 : 5)
                .ratio(DreaminaRatio.RATIO_16_9)
                .modelVersion(videoModel)
                .videoResolution(seedance25
                    ? DreaminaVideoResolutionType.RESOLUTION_480P
                    : DreaminaVideoResolutionType.RESOLUTION_720P)
                .sessionId(1L)
                .pollSeconds(1)
                .build()
                .toCliArgs()));
        flags.put("image2video", flagsFromArgs(
            DreaminaImage2VideoRequest.builder()
                .imagePath(first.toString())
                .prompt("prompt")
                .durationSeconds(seedance25 ? 10 : 5)
                .modelVersion(videoModel)
                .videoResolution(seedance25
                    ? DreaminaVideoResolutionType.RESOLUTION_480P
                    : DreaminaVideoResolutionType.RESOLUTION_720P)
                .sessionId(1L)
                .pollSeconds(1)
                .build()
                .toCliArgs()));
        flags.put("frames2video", flagsFromArgs(
            DreaminaFrames2VideoRequest.builder()
                .firstImagePath(first.toString())
                .lastImagePath(second.toString())
                .prompt("prompt")
                .durationSeconds(seedance25 ? 10 : 5)
                .modelVersion(videoModel)
                .videoResolution(seedance25
                    ? DreaminaVideoResolutionType.RESOLUTION_480P
                    : DreaminaVideoResolutionType.RESOLUTION_720P)
                .sessionId(1L)
                .pollSeconds(1)
                .build()
                .toCliArgs()));
        flags.put("multiframe2video", unionFlags(
            DreaminaMultiframe2VideoRequest.builder()
                .image(first.toString())
                .image(second.toString())
                .prompt("prompt")
                .durationSeconds(3.0)
                .videoResolution(DreaminaVideoResolutionType.RESOLUTION_720P)
                .sessionId(1L)
                .pollSeconds(1)
                .build()
                .toCliArgs(),
            DreaminaMultiframe2VideoRequest.builder()
                .image(first.toString())
                .image(second.toString())
                .image(third.toString())
                .transitionPrompt("first transition")
                .transitionPrompt("second transition")
                .transitionDuration("1")
                .transitionDuration("1")
                .videoResolution(DreaminaVideoResolutionType.RESOLUTION_720P)
                .build()
                .toCliArgs()));
        flags.put("multimodal2video", flagsFromArgs(
            DreaminaMultimodal2VideoRequest.builder()
                .image(first.toString())
                .video(video.toString())
                .audio(audio.toString())
                .prompt("prompt")
                .durationSeconds(seedance25 ? 10 : 5)
                .ratio(DreaminaRatio.RATIO_16_9)
                .modelVersion(videoModel)
                .videoResolution(seedance25
                    ? DreaminaVideoResolutionType.RESOLUTION_480P
                    : DreaminaVideoResolutionType.RESOLUTION_720P)
                .sessionId(1L)
                .pollSeconds(1)
                .build()
                .toCliArgs()));
        flags.put("image_upscale", flagsFromArgs(
            DreaminaImageUpscaleRequest.builder()
                .imagePath(first.toString())
                .resolutionType(DreaminaImageResolutionType.RESOLUTION_2K)
                .sessionId(1L)
                .pollSeconds(1)
                .build()
                .toCliArgs()));
        return flags;
    }

    private Set<String> extractHelpFlags(String help) throws IOException {
        Set<String> flags = new TreeSet<>();
        boolean inCommandFlags = false;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
            new java.io.ByteArrayInputStream(help.getBytes(StandardCharsets.UTF_8)),
            StandardCharsets.UTF_8))) {
            String line;
            while (Objects.nonNull(line = reader.readLine())) {
                if ("Flags:".equals(line.trim())) {
                    inCommandFlags = true;
                    continue;
                }
                if ("Global Flags:".equals(line.trim())) {
                    break;
                }
                if (!inCommandFlags) {
                    continue;
                }
                Matcher matcher = HELP_FLAG.matcher(line);
                if (matcher.matches() && !"--help".equals(matcher.group(1))) {
                    flags.add(matcher.group(1));
                }
            }
        }
        return flags;
    }

    private Set<String> parseSnapshotFlags(String literal) {
        return new TreeSet<>(Arrays.asList(literal.split(",")));
    }

    @SafeVarargs
    private final Set<String> unionFlags(List<String>... argumentSets) {
        Set<String> flags = new TreeSet<>();
        for (List<String> arguments : argumentSets) {
            flags.addAll(flagsFromArgs(arguments));
        }
        return flags;
    }

    private Set<String> flagsFromArgs(List<String> arguments) {
        if (Objects.isNull(arguments)) {
            return Collections.emptySet();
        }
        Set<String> flags = new TreeSet<>();
        for (String argument : arguments) {
            int separator = argument.indexOf('=');
            flags.add(separator < 0 ? argument : argument.substring(0, separator));
        }
        return flags;
    }

    private Path createTempFile(String fileName) throws IOException {
        Path file = tempDir.resolve(fileName);
        Files.write(file, "contract".getBytes(StandardCharsets.UTF_8));
        return file;
    }

    private void assertSnapshotLiteral(
        List<HelpContract> contracts,
        String command,
        String key,
        String expected) {
        String literal = contracts.stream()
            .filter(contract -> command.equals(contract.command()) && key.equals(contract.key()))
            .map(HelpContract::literal)
            .findFirst()
            .orElseThrow(() -> new AssertionError("missing snapshot row: " + command + "/" + key));
        assertEquals(expected, literal);
    }

    private List<HelpContract> readContracts() throws IOException {
        return readContracts(SNAPSHOT);
    }

    private List<HelpContract> readContracts(String snapshotPath) throws IOException {
        InputStream input = getClass().getResourceAsStream(snapshotPath);
        if (Objects.isNull(input)) {
            throw new IllegalStateException("missing CLI help snapshot: " + SNAPSHOT);
        }
        List<HelpContract> contracts = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
            String line;
            while (Objects.nonNull(line = reader.readLine())) {
                if (line.trim().isEmpty() || line.startsWith("#")) {
                    continue;
                }
                String[] columns = line.split("\t", 3);
                assertEquals(3, columns.length, "invalid snapshot row: " + line);
                contracts.add(new HelpContract(columns[0], columns[1], columns[2]));
            }
        }
        return contracts;
    }

    private static final class HelpContract {

        private final String command;
        private final String key;
        private final String literal;

        private HelpContract(String command, String key, String literal) {
            this.command = command;
            this.key = key;
            this.literal = literal;
        }

        private String command() {
            return command;
        }

        private String key() {
            return key;
        }

        private String literal() {
            return literal;
        }
    }
}
