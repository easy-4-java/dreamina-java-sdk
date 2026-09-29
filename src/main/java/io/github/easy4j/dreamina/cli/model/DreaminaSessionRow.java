package io.github.easy4j.dreamina.cli.model;

import lombok.Builder;
import lombok.Getter;

/**
 * A single row summary from the {@code dreamina session list} table.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @see DreaminaSessionList
 * @since 3.0.0
 * @deprecated 旧 session id/pinned/updated 表格行与 Canvas projectId 结构不同；替代：CANVAS_LS data.items。
 */
@Getter
@Builder
@Deprecated
public class DreaminaSessionRow {

    /**
     * Numeric session ID.
     */
    private final String id;

    /**
     * Session display name.
     */
    private final String name;

    /**
     * Whether the session is pinned (Yes/No, text-level snapshot).
     */
    private final String pinned;

    /**
     * Last update time (CLI raw format).
     */
    private final String updatedAt;
}
