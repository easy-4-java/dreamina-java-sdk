package io.github.easy4j.dreamina.cli.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Single video artifact from {@code result_json.videos[]}.
 *
 * @see DreaminaQueryResult#videos()
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DreaminaQueryVideo {

    /**
     * Downloadable signed video URL.
     */
    @JsonProperty("video_url")
    private String videoUrl;

    /**
     * Cover image URL (if provided by the CLI).
     */
    @JsonProperty("cover_url")
    private String coverUrl;

    /**
     * Video width in pixels.
     */
    private Integer width;

    /**
     * Video height in pixels.
     */
    private Integer height;

    /**
     * Frame rate (24 in production {@code multiframe2video} success samples).
     */
    private Integer fps;

    /**
     * Container format (e.g., {@code mp4}).
     */
    private String format;

    /**
     * Duration in seconds (may be fractional; e.g., {@code 3.208} in production samples).
     */
    private Double duration;
}
