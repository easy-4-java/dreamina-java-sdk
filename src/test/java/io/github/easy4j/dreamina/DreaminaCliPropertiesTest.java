package io.github.easy4j.dreamina;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DreaminaCliPropertiesTest {

    @Test
    void shouldHaveDefaultExecutable() {
        DreaminaCliProperties props = new DreaminaCliProperties();
        assertEquals("dreamina", props.getExecutable());
    }

    @Test
    void shouldHaveDefaultTimeout() {
        DreaminaCliProperties props = new DreaminaCliProperties();
        assertEquals(120_000L, props.getCommandTimeoutMillis());
    }

    @Test
    void shouldHaveDefaultStartupProbeTimeout() {
        DreaminaCliProperties props = new DreaminaCliProperties();
        assertEquals(30_000L, props.getStartupProbeTimeoutMillis());
    }

    @Test
    void shouldHaveDefaultPollInterval() {
        DreaminaCliProperties props = new DreaminaCliProperties();
        assertEquals(5, props.getDefaultPollIntervalSeconds());
    }

    @Test
    void shouldHaveDefaultMaxConcurrentExecutions() {
        DreaminaCliProperties props = new DreaminaCliProperties();
        assertEquals(0, props.getMaxConcurrentExecutions());
    }

    @Test
    void shouldHaveNullWorkingDirectoryByDefault() {
        DreaminaCliProperties props = new DreaminaCliProperties();
        assertNull(props.getWorkingDirectory());
    }

    @Test
    void shouldAllowSettingExecutable() {
        DreaminaCliProperties props = new DreaminaCliProperties();
        props.setExecutable("/usr/local/bin/dreamina");
        assertEquals("/usr/local/bin/dreamina", props.getExecutable());
    }

    @Test
    void shouldAllowSettingWorkingDirectory() {
        DreaminaCliProperties props = new DreaminaCliProperties();
        props.setWorkingDirectory("/tmp");
        assertEquals("/tmp", props.getWorkingDirectory());
    }

    @Test
    void shouldAllowSettingTimeout() {
        DreaminaCliProperties props = new DreaminaCliProperties();
        props.setCommandTimeoutMillis(60_000L);
        assertEquals(60_000L, props.getCommandTimeoutMillis());
    }

    @Test
    void shouldAllowSettingStartupProbeTimeout() {
        DreaminaCliProperties props = new DreaminaCliProperties();
        props.setStartupProbeTimeoutMillis(10_000L);
        assertEquals(10_000L, props.getStartupProbeTimeoutMillis());
    }

    @Test
    void shouldAllowSettingPollInterval() {
        DreaminaCliProperties props = new DreaminaCliProperties();
        props.setDefaultPollIntervalSeconds(10);
        assertEquals(10, props.getDefaultPollIntervalSeconds());
    }

    @Test
    void shouldAllowSettingMaxConcurrentExecutions() {
        DreaminaCliProperties props = new DreaminaCliProperties();
        props.setMaxConcurrentExecutions(4);
        assertEquals(4, props.getMaxConcurrentExecutions());
    }

    @Test
    void shouldSupportLombokEqualsAndHashCode() {
        DreaminaCliProperties a = new DreaminaCliProperties();
        DreaminaCliProperties b = new DreaminaCliProperties();
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void shouldSupportLombokToString() {
        DreaminaCliProperties props = new DreaminaCliProperties();
        String str = props.toString();
        assertNotNull(str);
        assertTrue(str.contains("dreamina"));
    }
}
