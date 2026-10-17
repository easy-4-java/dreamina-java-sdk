package io.github.easy4j.dreamina.cli.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * Billing/benefit summary from the {@code commerce_info} field returned by commands such as {@code list_task}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @see DreaminaTaskItem
 * @since 3.0.0
 * @deprecated 旧 list_task commerce_info，不等同 Canvas node quote 报价；替代：NODE_QUOTE data。
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Deprecated
public class DreaminaCommerceInfo {

    @JsonProperty("credit_count")
    private Long creditCount;

    /**
     * Single triplet placeholder (fields may be empty strings in production).
     */
    private DreaminaCommerceTriplet triplet;

    /**
     * List of effective benefit triplets.
     */
    private List<DreaminaCommerceTriplet> triplets;

    /**
     * @return Non-null view of the triplets list.
     */
    public List<DreaminaCommerceTriplet> safeTriplets() {
        return triplets == null ? Collections.emptyList() : triplets;
    }
}
