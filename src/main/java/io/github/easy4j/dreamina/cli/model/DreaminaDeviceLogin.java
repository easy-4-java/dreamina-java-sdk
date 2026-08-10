package io.github.easy4j.dreamina.cli.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * OAuth Device Flow material (JSON or key-value text from commands such as {@code relogin}).
 *
 * @see io.github.easy4j.dreamina.cli.parser.DreaminaLoginTextParser#parseDeviceFlow(String)
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DreaminaDeviceLogin {

    @JsonProperty("device_code")
    @JsonAlias("deviceCode")
    private String deviceCode;

    @JsonProperty("verification_uri")
    @JsonAlias("verificationUri")
    private String verificationUri;

    @JsonProperty("user_code")
    @JsonAlias("userCode")
    private String userCode;

    /**
     * Polling interval (e.g., {@code 1s}); common in text output.
     */
    @JsonProperty("poll_interval")
    private String pollInterval;

    /**
     * Device code expiration time (ISO-8601 string).
     */
    @JsonProperty("expires_at")
    private String expiresAt;

    /**
     * Whether the core Device Flow fields required for {@code checklogin} are present.
     *
     * @return Returns true if the material is present.
     */
    public boolean isMaterialPresent() {
        return io.github.easy4j.dreamina.cli.parser.DreaminaLoginTextParser.hasDeviceFlowMaterial(this);
    }
}
