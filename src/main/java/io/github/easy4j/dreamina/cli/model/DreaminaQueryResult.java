package io.github.easy4j.dreamina.cli.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import lombok.Data;

/**
 * Parsed body for {@code dreamina query_result} (one-to-one mapping with the CLI JSON).
 *
 * @see io.github.easy4j.dreamina.cli.DreaminaCliExecutor#queryResult(String)
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DreaminaQueryResult {

    @JsonProperty("submit_id")
    private String submitId;

    /**
     * Generation prompt (returned by {@code query_result} for in-progress / some task types).
     */
    private String prompt;

    /**
     * Server-side tracking ID (consistent with the submit response {@code logid}).
     */
    private String logid;

    @JsonProperty("gen_status")
    private String genStatus;

    @JsonProperty("fail_reason")
    private String failReason;

    @JsonProperty("result_json")
    private DreaminaResultJson resultJson;

    @JsonProperty("queue_info")
    private DreaminaQueryQueueInfo queueInfo;

    @JsonProperty("credit_count")
    private Long creditCount;

    /**
     * @return Whether the status is success.
     */
    public boolean isGenSuccess() {
        return generationStatus() == DreaminaGenerationStatus.SUCCESS;
    }

    /**
     * @return Whether the task is querying (still queued / generating).
     */
    public boolean isGenQuerying() {
        return generationStatus() == DreaminaGenerationStatus.QUERYING;
    }

    /**
     * @return Whether the status is fail.
     */
    public boolean isGenFailed() {
        return generationStatus() == DreaminaGenerationStatus.FAIL;
    }

    /**
     * @return The current stable status enum.
     */
    public DreaminaGenerationStatus generationStatus() {
        return DreaminaGenerationStatus.fromCliValue(genStatus);
    }

    /**
     * @return Whether the task has reached a terminal state (success or fail).
     */
    public boolean isTerminal() {
        return generationStatus().isTerminal();
    }

    /**
     * @return Image list; never null.
     */
    public List<DreaminaQueryImage> images() {
        return Objects.isNull(resultJson) ? Collections.emptyList() : resultJson.safeImages();
    }

    /**
     * @return Video list; never null.
     */
    public List<DreaminaQueryVideo> videos() {
        return Objects.isNull(resultJson) ? Collections.emptyList() : resultJson.safeVideos();
    }

    /**
     * @return First image_url, or null if none.
     */
    public String firstImageUrl() {
        return images().stream()
            .map(DreaminaQueryImage::getImageUrl)
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(null);
    }

    /**
     * @return First video_url, or null if none.
     */
    public String firstVideoUrl() {
        return videos().stream()
            .map(DreaminaQueryVideo::getVideoUrl)
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(null);
    }

    /**
     * @return Whether the queue status is Finish.
     */
    public boolean isQueueFinished() {
        if (Objects.isNull(queueInfo) || Objects.isNull(queueInfo.getQueueStatus())) {
            return false;
        }
        return "finish".equals(queueInfo.getQueueStatus().trim().toLowerCase(Locale.ROOT));
    }
}
