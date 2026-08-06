package io.github.easy4j.dreamina.cli.model;

import java.util.Collections;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 * {@code dreamina session search} 解析体。
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
@Getter
@Builder
public class DreaminaSessionSearch {

    private final String queryTerm;
    private final List<DreaminaSessionRow> rows;

    /**
     * @return 非 null 匹配行
     */
    public List<DreaminaSessionRow> safeRows() {
        return rows == null ? Collections.emptyList() : rows;
    }
}
