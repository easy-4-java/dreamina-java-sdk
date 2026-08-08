package io.github.easy4j.dreamina.cli.opts;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

class DreaminaOptsEnumTest {

    // --- DreaminaImageModelVersion ---
    @Test
    void shouldSupportImageToImageForModels4Plus() {
        assertFalse(DreaminaImageModelVersion.MODEL_3_0.supportsImageToImage());
        assertFalse(DreaminaImageModelVersion.MODEL_3_1.supportsImageToImage());
        assertTrue(DreaminaImageModelVersion.MODEL_4_0.supportsImageToImage());
        assertTrue(DreaminaImageModelVersion.MODEL_4_1.supportsImageToImage());
        assertTrue(DreaminaImageModelVersion.MODEL_4_5.supportsImageToImage());
        assertTrue(DreaminaImageModelVersion.MODEL_4_6.supportsImageToImage());
        assertTrue(DreaminaImageModelVersion.MODEL_4_7.supportsImageToImage());
        assertTrue(DreaminaImageModelVersion.MODEL_5_0.supportsImageToImage());
        assertTrue(DreaminaImageModelVersion.MODEL_5_0_PRO.supportsImageToImage());
    }

    @ParameterizedTest
    @EnumSource(DreaminaImageModelVersion.class)
    void shouldHaveCliValue(DreaminaImageModelVersion v) {
        assertNotNull(v.getCliValue());
        assertFalse(v.getCliValue().isEmpty());
    }

    // --- DreaminaImageResolutionType ---
    @ParameterizedTest
    @EnumSource(DreaminaImageResolutionType.class)
    void shouldHaveCliValue(DreaminaImageResolutionType v) {
        assertNotNull(v.getCliValue());
    }

    @Test
    void shouldDefineExpectedResolutions() {
        assertEquals("1k", DreaminaImageResolutionType.RESOLUTION_1K.getCliValue());
        assertEquals("2k", DreaminaImageResolutionType.RESOLUTION_2K.getCliValue());
        assertEquals("4k", DreaminaImageResolutionType.RESOLUTION_4K.getCliValue());
        assertEquals("8k", DreaminaImageResolutionType.RESOLUTION_8K.getCliValue());
    }

    // --- DreaminaVideoModelVersion ---
    @ParameterizedTest
    @EnumSource(DreaminaVideoModelVersion.class)
    void shouldHaveCliValue(DreaminaVideoModelVersion v) {
        assertNotNull(v.getCliValue());
    }

    @Test
    void shouldSupportText2Video() {
        assertTrue(DreaminaVideoModelVersion.SEEDANCE_2_0.supportsText2Video());
        assertTrue(DreaminaVideoModelVersion.SEEDANCE_2_0_FAST.supportsText2Video());
        assertTrue(DreaminaVideoModelVersion.SEEDANCE_2_0_VIP.supportsText2Video());
        assertTrue(DreaminaVideoModelVersion.SEEDANCE_2_0_FAST_VIP.supportsText2Video());
        assertTrue(DreaminaVideoModelVersion.SEEDANCE_2_0_MINI.supportsText2Video());
        assertTrue(DreaminaVideoModelVersion.SEEDANCE_2_5.supportsText2Video());
        assertFalse(DreaminaVideoModelVersion.SEEDANCE_1_0_FAST.supportsText2Video());
        assertFalse(DreaminaVideoModelVersion.SEEDANCE_1_0.supportsText2Video());
        assertFalse(DreaminaVideoModelVersion.SEEDANCE_1_5_PRO.supportsText2Video());
    }

    @Test
    void shouldSupportImage2Video() {
        assertTrue(DreaminaVideoModelVersion.SEEDANCE_1_0_FAST.supportsImage2Video());
        assertTrue(DreaminaVideoModelVersion.SEEDANCE_1_5_PRO.supportsImage2Video());
        assertTrue(DreaminaVideoModelVersion.SEEDANCE_2_0.supportsImage2Video());
        assertTrue(DreaminaVideoModelVersion.SEEDANCE_2_5.supportsImage2Video());
    }

    @Test
    void shouldSupportFrames2Video() {
        assertTrue(DreaminaVideoModelVersion.SEEDANCE_1_5_PRO.supportsFrames2Video());
        assertTrue(DreaminaVideoModelVersion.SEEDANCE_2_0.supportsFrames2Video());
        assertFalse(DreaminaVideoModelVersion.SEEDANCE_1_0_FAST.supportsFrames2Video());
    }

    @Test
    void shouldSupportMultimodal2Video() {
        assertTrue(DreaminaVideoModelVersion.SEEDANCE_2_0.supportsMultimodal2Video());
        assertTrue(DreaminaVideoModelVersion.SEEDANCE_2_5.supportsMultimodal2Video());
        assertFalse(DreaminaVideoModelVersion.SEEDANCE_1_0_FAST.supportsMultimodal2Video());
    }

    @Test
    void shouldHaveValidDurationRanges() {
        for (DreaminaVideoModelVersion v : DreaminaVideoModelVersion.values()) {
            assertTrue(v.minDurationSeconds() > 0, v + " minDuration should be positive");
            assertTrue(v.maxDurationSeconds() >= v.minDurationSeconds(), v + " max should be >= min");
        }
    }

    @Test
    void shouldHave25DurationRange() {
        assertEquals(4, DreaminaVideoModelVersion.SEEDANCE_2_5.minDurationSeconds());
        assertEquals(30, DreaminaVideoModelVersion.SEEDANCE_2_5.maxDurationSeconds());
    }

    // --- DreaminaVideoResolutionType ---
    @ParameterizedTest
    @EnumSource(DreaminaVideoResolutionType.class)
    void shouldHaveCliValue(DreaminaVideoResolutionType v) {
        assertNotNull(v.getCliValue());
    }

    @Test
    void shouldDefineExpectedVideoResolutions() {
        assertEquals("720p", DreaminaVideoResolutionType.RESOLUTION_720P.getCliValue());
        assertEquals("1080p", DreaminaVideoResolutionType.RESOLUTION_1080P.getCliValue());
        assertEquals("480p", DreaminaVideoResolutionType.RESOLUTION_480P.getCliValue());
        assertEquals("4k", DreaminaVideoResolutionType.RESOLUTION_4K.getCliValue());
    }

    // --- DreaminaRatio ---
    @ParameterizedTest
    @EnumSource(DreaminaRatio.class)
    void shouldHaveCliValue(DreaminaRatio v) {
        assertNotNull(v.getCliValue());
        assertTrue(v.getCliValue().contains(":"));
    }
}
