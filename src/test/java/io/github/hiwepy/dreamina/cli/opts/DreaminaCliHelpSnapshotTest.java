package io.github.hiwepy.dreamina.cli.opts;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 官方 CLI help 快照门禁。
 *
 * @author wandl
 * @since 2.0.0
 */
class DreaminaCliHelpSnapshotTest {

    private static final String SNAPSHOT =
        "/cli-contract/dreamina-v1.4.14-help.snapshot.tsv";

    @Test
    void snapshot_shouldMatchSdkPublicTokens() throws IOException {
        List<HelpContract> contracts = readContracts();

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
    }

    @Test
    void installedCliHelp_shouldMatchCommittedSnapshot() throws IOException, InterruptedException {
        Assumptions.assumeTrue(Boolean.getBoolean("dreamina.cli.contract.verify"),
            "set -Ddreamina.cli.contract.verify=true to compare the installed CLI");
        String executable = System.getProperty("dreamina.cli.executable", "dreamina");

        for (HelpContract contract : readContracts()) {
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
            assertEquals(0, process.waitFor(), contract.command() + " help command failed");
            assertTrue(help.contains(contract.literal()),
                () -> contract.command() + " help drifted at key " + contract.key()
                    + "; missing: " + contract.literal());
        }
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
        InputStream input = getClass().getResourceAsStream(SNAPSHOT);
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
