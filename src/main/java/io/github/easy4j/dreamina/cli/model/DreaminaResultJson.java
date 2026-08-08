package io.github.easy4j.dreamina.cli.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Collections;
import java.util.List;
import lombok.Data;

/**
 * The {@code result_json} object within a {@code query_result} response.
 *
 * @see DreaminaQueryResult
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DreaminaResultJson {

    /**
     * Image list for successful generations; {@code null} when absent (callers can use {@link #safeImages()} for an empty list).
     */
    private List<DreaminaQueryImage> images;

    /**
     * Video list for successful generations; {@code null} when absent.
     */
    private List<DreaminaQueryVideo> videos;

    /**
     * Returns a non-null image list view.
     *
     * @return Image list; never null.
     */
    public List<DreaminaQueryImage> safeImages() {
        return images == null ? Collections.emptyList() : images;
    }

    /**
     * Returns a non-null video list view.
     *
     * @return Video list; never null.
     */
    public List<DreaminaQueryVideo> safeVideos() {
        return videos == null ? Collections.emptyList() : videos;
    }
}
