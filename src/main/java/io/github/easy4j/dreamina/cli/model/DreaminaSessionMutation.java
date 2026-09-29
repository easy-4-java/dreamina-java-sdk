package io.github.easy4j.dreamina.cli.model;

import lombok.Builder;
import lombok.Getter;

/**
 * Parsed body for {@code dreamina session create/rename}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @see io.github.easy4j.dreamina.cli.DreaminaCliExecutor#sessionCreate()
 * @since 3.0.0
 * @deprecated 旧 session create/rename；创建改用 CANVAS_CREATE，当前无画布重命名命令。
 */
@Getter
@Builder
@Deprecated
public class DreaminaSessionMutation {

    private final Kind kind;
    private final String sessionId;
    private final String sessionName;
    public enum Kind {
        CREATE,
        RENAME,
        UNKNOWN
    }
}
