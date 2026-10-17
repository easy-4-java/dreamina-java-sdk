package io.github.easy4j.dreamina.cli.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * JSON payload for {@code dreamina user_credit}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @see io.github.easy4j.dreamina.cli.DreaminaCliExecutor#userCredit()
 * @since 3.0.0
 * @deprecated 旧 user_credit 余额字段，Canvas auth account 不返回积分余额；报价使用 NODE_QUOTE，不能拿会员状态充当余额。
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Deprecated
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
