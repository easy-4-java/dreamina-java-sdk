package io.github.easy4j.dreamina.cli.model;

import java.util.Collections;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 * Parsed body for {@code dreamina session list}.
 *
 * @see io.github.easy4j.dreamina.cli.DreaminaCliExecutor#sessionList()
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 */
@Getter
@Builder
public class DreaminaSessionList {

    private final List<DreaminaSessionRow> rows;

    /**
     * @return Non-null row list.
     */
    public List<DreaminaSessionRow> safeRows() {
        return rows == null ? Collections.emptyList() : rows;
    }
}
