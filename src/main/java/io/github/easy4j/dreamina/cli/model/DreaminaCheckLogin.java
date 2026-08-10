package io.github.easy4j.dreamina.cli.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * JSON payload for {@code dreamina login checklogin}.
 *
 * @see io.github.easy4j.dreamina.cli.DreaminaCliExecutor#checkLogin(String, int)
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DreaminaCheckLogin {

    @JsonProperty("gen_status")
    private String genStatus;

    private String message;
}
