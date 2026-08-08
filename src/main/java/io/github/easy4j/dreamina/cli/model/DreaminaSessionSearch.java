package io.github.easy4j.dreamina.cli.model;

import java.util.Collections;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 * Parsed body for {@code dreamina session search}.
 *
 * @see io.github.easy4j.dreamina.cli.DreaminaCliExecutor#sessionSearch(String)
 *
 * @author [@Loong Wan](https://github.com/loong10k)
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
