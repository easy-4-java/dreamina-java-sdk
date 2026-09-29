package io.github.easy4j.dreamina.cli.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * JSON payload for {@code dreamina login checklogin}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @see io.github.easy4j.dreamina.cli.DreaminaCliExecutor#checkLogin(String, int)
 * @since 3.0.0
 * @deprecated 旧 gen_status/message 登录轮询体不同于 auth wait envelope；替代：AUTH_WAIT 的 data/error。
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Deprecated
public class DreaminaCheckLogin {

    @JsonProperty("gen_status")
    private String genStatus;

    private String message;
}
