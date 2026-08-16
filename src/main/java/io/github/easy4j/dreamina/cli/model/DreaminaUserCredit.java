package io.github.easy4j.dreamina.cli.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * JSON payload for {@code dreamina user_credit}.
 *
 * @see io.github.easy4j.dreamina.cli.DreaminaCliExecutor#userCredit()
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DreaminaUserCredit {

    @JsonProperty("total_credit")
    private Long totalCredit;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("user_name")
    private String userName;

    @JsonProperty("vip_level")
    private String vipLevel;
}
