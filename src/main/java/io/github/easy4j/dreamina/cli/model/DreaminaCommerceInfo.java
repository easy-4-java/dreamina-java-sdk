package io.github.easy4j.dreamina.cli.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.List;
import lombok.Data;

/**
 * Billing/benefit summary from the {@code commerce_info} field returned by commands such as {@code list_task}.
 *
 * @see DreaminaTaskItem
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
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
