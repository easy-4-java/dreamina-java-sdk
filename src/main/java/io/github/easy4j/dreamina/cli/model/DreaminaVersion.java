package io.github.easy4j.dreamina.cli.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * JSON payload for {@code dreamina version}.
 *
 * @see io.github.easy4j.dreamina.cli.DreaminaCliExecutor#version()
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DreaminaVersion {

    private String version;

    private String commit;

    @JsonProperty("build_time")
    private String buildTime;
}
