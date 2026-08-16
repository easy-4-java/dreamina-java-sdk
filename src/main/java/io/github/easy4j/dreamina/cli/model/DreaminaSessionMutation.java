package io.github.easy4j.dreamina.cli.model;

import lombok.Builder;
import lombok.Getter;

/**
 * Parsed body for {@code dreamina session create/rename}.
 *
 * @see io.github.easy4j.dreamina.cli.DreaminaCliExecutor#sessionCreate()
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 */
@Getter
@Builder
public class DreaminaSessionMutation {

    public enum Kind {
        CREATE,
        RENAME,
        UNKNOWN
    }

    private final Kind kind;
    private final String sessionId;
    private final String sessionName;
}
