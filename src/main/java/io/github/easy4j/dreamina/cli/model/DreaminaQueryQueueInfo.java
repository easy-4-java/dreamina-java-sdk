package io.github.easy4j.dreamina.cli.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * The {@code queue_info} object from {@code query_result} or some generation submit responses.
 *
 * @see DreaminaQueryResult
 * @see DreaminaGenerateSubmit
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DreaminaQueryQueueInfo {

    /**
     * Index within the queue.
     */
    @JsonProperty("queue_idx")
    private Integer queueIdx;

    /**
     * Task priority.
     */
    private Integer priority;

    /**
     * Queue status (e.g., {@code Finish}, {@code Waiting}; semantics defined by the official CLI).
     */
    @JsonProperty("queue_status")
    private String queueStatus;

    /**
     * Current queue length.
     */
    @JsonProperty("queue_length")
    private Integer queueLength;

    /**
     * Raw debug JSON string (output as-is by the CLI).
     */
    @JsonProperty("debug_info")
    private String debugInfo;

    /**
     * Structured view parsed from {@link #debugInfo} by the SDK; {@code null} when parsing fails or the field is absent.
     */
    private DreaminaQueryQueueDebugInfo parsedDebugInfo;
}
