package io.github.easy4j.dreamina.cli.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

/**
 * Parsed body for {@code dreamina logout}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @see io.github.easy4j.dreamina.cli.DreaminaCliExecutor#logout()
 * @since 3.0.0
 */
@Getter
@Builder
public class DreaminaLogout {

    @com.fasterxml.jackson.annotation.JsonAlias("loggedOut")
    private final Boolean localSessionCleared;

    /**
     * 复用本地凭据清理语义；保留原 builder 和 getter。
     */
    @JsonCreator
    public DreaminaLogout(@JsonProperty("localSessionCleared") @com.fasterxml.jackson.annotation.JsonAlias("loggedOut") Boolean localSessionCleared) {
        this.localSessionCleared = localSessionCleared;
    }

    @com.fasterxml.jackson.annotation.JsonIgnore
    public Boolean getLoggedOut() {
        return localSessionCleared;
    }
}
