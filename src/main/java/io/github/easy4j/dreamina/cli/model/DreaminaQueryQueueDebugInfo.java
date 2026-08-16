package io.github.easy4j.dreamina.cli.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Diagnostic structure parsed from the {@code queue_info.debug_info} embedded JSON string.
 * <p>
 * This field is parsed by the SDK during the mapping phase and is not a top-level CLI key.
 * </p>
 *
 * @see DreaminaQueryQueueInfo
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DreaminaQueryQueueDebugInfo {

    @JsonProperty("have_no_dreamina_queue_name")
    private Boolean haveNoDreaminaQueueName;

    @JsonProperty("dreamina_matrix_queue_name")
    private String dreaminaMatrixQueueName;

    @JsonProperty("dreamina_matrix_req_key")
    private String dreaminaMatrixReqKey;

    @JsonProperty("dreamina_matrix_second_req_key")
    private String dreaminaMatrixSecondReqKey;

    @JsonProperty("have_no_queue_name")
    private Boolean haveNoQueueName;

    @JsonProperty("queue_name")
    private String queueName;

    @JsonProperty("matrix_req_key")
    private String matrixReqKey;

    @JsonProperty("matrix_second_req_key")
    private String matrixSecondReqKey;
}
