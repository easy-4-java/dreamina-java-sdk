package io.github.easy4j.dreamina.cli.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Single image artifact from {@code result_json.images[]}.
 *
 * @see DreaminaQueryResult#images()
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DreaminaQueryImage {

    /**
     * Downloadable signed image URL.
     */
    @JsonProperty("image_url")
    private String imageUrl;

    /**
     * Image width in pixels; {@code null} when not returned by the CLI.
     */
    private Integer width;

    /**
     * Image height in pixels; {@code null} when not returned by the CLI.
     */
    private Integer height;
}
