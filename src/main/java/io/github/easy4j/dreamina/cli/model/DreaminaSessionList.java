package io.github.easy4j.dreamina.cli.model;

import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

/**
 * Parsed body for {@code dreamina session list}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @see io.github.easy4j.dreamina.cli.DreaminaCliExecutor#sessionList()
 * @since 3.0.0
 * @deprecated 旧数字 session 表格，不是 Canvas UUID 画布列表；替代：CANVAS_LS data.items。
 */
@Getter
@Builder
@Deprecated
public class DreaminaSessionList {

    private final List<DreaminaSessionRow> rows;

    /**
     * @return Non-null row list.
     */
    public List<DreaminaSessionRow> safeRows() {
        return rows == null ? Collections.emptyList() : rows;
    }
}
