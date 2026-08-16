package io.github.easy4j.dreamina.cli.model;

import lombok.Builder;
import lombok.Getter;

/**
 * Parsed body for {@code dreamina session delete/rm} (CLI typically outputs {@code deleted} on success).
 *
 * @see io.github.easy4j.dreamina.cli.DreaminaCliExecutor#sessionDelete(String)
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 */
@Getter
@Builder
public class DreaminaSessionDelete {

    private final boolean deleted;
}
