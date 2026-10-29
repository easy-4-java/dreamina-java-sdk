package io.github.easy4j.dreamina.cli.model;

import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

/**
 * Parsed body for {@code dreamina session search}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @see io.github.easy4j.dreamina.cli.DreaminaCliExecutor#sessionSearch(String)
 * @since 3.0.0
 */
@Getter
@Builder
public class DreaminaSessionSearch {

    private final String queryTerm;
    private final List<DreaminaSessionRow> rows;

    /**
     * @return Non-null matching rows.
     */
    public List<DreaminaSessionRow> safeRows() {
        return rows == null ? Collections.emptyList() : rows;
    }
}
