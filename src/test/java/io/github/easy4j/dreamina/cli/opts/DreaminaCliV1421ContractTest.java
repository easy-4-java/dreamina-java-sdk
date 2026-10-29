package io.github.easy4j.dreamina.cli.opts;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.nio.file.Files;
import static org.junit.jupiter.api.Assertions.*;

/** 视频比例与当前旧 CLI 契约回归，使用隔离本地素材。 */
class DreaminaCliV1421ContractTest {
    @TempDir Path dir;
    @Test void referenceVideoRatiosAreExplicitAndOptional() throws Exception {
        Path image = Files.write(dir.resolve("frame.png"), new byte[]{1});
        for (DreaminaRatio ratio : DreaminaRatio.values()) {
            DreaminaImage2VideoRequest request = DreaminaImage2VideoRequest.builder().prompt("ratio contract").imagePath(image.toString()).ratio(ratio).build();
            DreaminaFrames2VideoRequest frames = DreaminaFrames2VideoRequest.builder().prompt("ratio contract").firstImagePath(image.toString()).lastImagePath(image.toString()).ratio(ratio).build();
            if (ratio == DreaminaRatio.RATIO_3_2 || ratio == DreaminaRatio.RATIO_2_3) {
                assertThrows(IllegalArgumentException.class, request::toCliArgs);
                assertThrows(IllegalArgumentException.class, frames::toCliArgs);
            } else {
                assertTrue(request.toCliArgs().contains("--ratio=" + ratio.getCliValue()));
                assertTrue(frames.toCliArgs().contains("--ratio=" + ratio.getCliValue()));
            }
        }
        assertFalse(DreaminaImage2VideoRequest.builder().prompt("ratio contract").imagePath(image.toString()).build().toCliArgs().stream().anyMatch(a -> a.startsWith("--ratio=")));
        assertThrows(IllegalArgumentException.class, () -> DreaminaImage2VideoRequest.builder().prompt("ratio contract").imagePath(image.toString()).modelVersion(DreaminaVideoModelVersion.SEEDANCE_2_5).ratio(DreaminaRatio.RATIO_16_9).build().toCliArgs());
        assertThrows(IllegalArgumentException.class, () -> DreaminaFrames2VideoRequest.builder().prompt("ratio contract").firstImagePath(image.toString()).lastImagePath(image.toString()).modelVersion(DreaminaVideoModelVersion.SEEDANCE_2_5).ratio(DreaminaRatio.RATIO_16_9).build().toCliArgs());
    }
    @Test void textAndMultimodalRejectUnsupportedVideoRatios() {
        assertThrows(IllegalArgumentException.class, () -> DreaminaText2VideoRequest.builder().prompt("ratio contract").ratio(DreaminaRatio.RATIO_3_2).build().toCliArgs());
        assertThrows(IllegalArgumentException.class, () -> DreaminaMultimodal2VideoRequest.builder().prompt("ratio contract").ratio(DreaminaRatio.RATIO_2_3).build().toCliArgs());
    }
}
