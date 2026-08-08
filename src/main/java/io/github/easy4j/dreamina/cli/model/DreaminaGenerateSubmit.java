package io.github.easy4j.dreamina.cli.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Root JSON payload after submitting an asynchronous generation command ({@code text2image}, {@code image2video}, etc.).
 *
 * @see io.github.easy4j.dreamina.cli.DreaminaCliExecutor#text2Image(String)
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DreaminaGenerateSubmit {

    @JsonProperty("submit_id")
    private String submitId;

    @JsonProperty("gen_status")
    private String genStatus;

    @JsonProperty("fail_reason")
    private String failReason;

    @JsonProperty("queue_info")
    private DreaminaQueryQueueInfo queueInfo;

    /**
     * Server-side tracking ID (returned by production {@code text2image} and similar submit responses).
     */
    private String logid;

    @JsonProperty("credit_count")
    private Long creditCount;

    public DreaminaGenerationStatus generationStatus() {
        return DreaminaGenerationStatus.fromCliValue(genStatus);
    }

    public boolean isGenSuccess() {
        return generationStatus() == DreaminaGenerationStatus.SUCCESS;
    }

    public boolean isGenQuerying() {
        return generationStatus() == DreaminaGenerationStatus.QUERYING;
    }

    public boolean isGenFailed() {
        return generationStatus() == DreaminaGenerationStatus.FAIL;
    }

    public boolean isTerminal() {
        return generationStatus().isTerminal();
    }
}
