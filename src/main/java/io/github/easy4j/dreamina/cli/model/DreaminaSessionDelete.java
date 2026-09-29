package io.github.easy4j.dreamina.cli.model;

import lombok.Builder;
import lombok.Getter;

/**
 * Parsed body for {@code dreamina session delete/rm} (CLI typically outputs {@code deleted} on success).
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @see io.github.easy4j.dreamina.cli.DreaminaCliExecutor#sessionDelete(String)
 * @since 3.0.0
 * @deprecated 旧 session delete，Canvas 1.0.1 未提供画布删除；无等价命令，不伪造替代。
 */
@Getter
@Builder
@Deprecated
public class DreaminaSessionDelete {

    private final boolean deleted;
}
