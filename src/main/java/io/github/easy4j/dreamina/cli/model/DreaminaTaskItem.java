package io.github.easy4j.dreamina.cli.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Objects;

/**
 * A single task record from the {@code dreamina list_task} array.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @see io.github.easy4j.dreamina.cli.DreaminaCliExecutor#listTask()
 * @since 3.0.0
 * @deprecated 旧 list_task 任务/计费汇总，Canvas 无同形列表；替代：已知 submitId 的 OPERATION_STATUS。
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Deprecated
public class DreaminaTaskItem {

    @JsonProperty("submit_id")
    private String submitId;

    /**
     * Task prompt (returned by {@code list_task} for some {@code gen_task_type} values).
     */
    private String prompt;

    @JsonProperty("gen_status")
    private String genStatus;

    @JsonProperty("gen_task_type")
    private String genTaskType;

    @JsonProperty("fail_reason")
    private String failReason;

    @JsonProperty("result_json")
    private DreaminaResultJson resultJson;

    /**
     * Billing and benefit info (production {@code list_task} places {@code credit_count} within this object).
     */
    @JsonProperty("commerce_info")
    private DreaminaCommerceInfo commerceInfo;

    /**
     * Credit count field at the task root in some CLI versions; when both exist, commerce takes precedence.
     */
    @JsonProperty("credit_count")
    private Long creditCount;

    /**
     * Resolves the credit count consumed by this task: prefers {@code commerce_info.credit_count}, falls back to the root-level {@code credit_count}.
     *
     * @return 积分或 null
     */
    public Long resolveCreditCount() {
        if (Objects.nonNull(commerceInfo) && Objects.nonNull(commerceInfo.getCreditCount())) {
            return commerceInfo.getCreditCount();
        }
        return creditCount;
    }

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
