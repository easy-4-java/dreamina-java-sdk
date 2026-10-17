package io.github.easy4j.dreamina.cli.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * JSON payload for {@code dreamina version}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @see io.github.easy4j.dreamina.cli.DreaminaCliExecutor#version()
 * @since 3.0.0
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DreaminaVersion {

    private String version;

    private String commit;

    @JsonProperty("build_time")
    @JsonAlias("buildTime")
    private String buildTime;
    private String edition;
    private String distribution;
    private String releaseDate;
    private String releaseNotes;
}
