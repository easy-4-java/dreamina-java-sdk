package io.github.easy4j.dreamina.cli.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Single benefit triplet from {@code commerce_info.triplet} or {@code commerce_info.triplets[]}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @see DreaminaCommerceInfo
 * @since 3.0.0
 * @deprecated 旧计费 triplet 结构，Canvas quote 不返回该协议；替代：NODE_QUOTE data。
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Deprecated
public class DreaminaCommerceTriplet {

    @JsonProperty("resource_type")
    private String resourceType;

    @JsonProperty("resource_id")
    private String resourceId;

    @JsonProperty("benefit_type")
    private String benefitType;
}
