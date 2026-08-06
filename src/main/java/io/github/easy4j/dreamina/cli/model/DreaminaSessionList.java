package io.github.easy4j.dreamina.cli.model;

import java.util.Collections;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 * {@code dreamina session list} 解析体。
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 1.0.0
 */
@Getter
@Builder
public class DreaminaSessionList {

    private final List<DreaminaSessionRow> rows;

    /**
     * @return 非 null 行列表
     */
    public List<DreaminaSessionRow> safeRows() {
        return rows == null ? Collections.emptyList() : rows;
    }
}
