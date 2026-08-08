package io.github.easy4j.dreamina.cli.opts;

import java.util.List;

/**
 * Dreamina CLI argument provider.
 * <p>
 * Converts strongly-typed request objects into argv lists that can be passed directly to the executor,
 * keeping a unified entry point in the execution layer while letting each generation command
 * encapsulate parameter validation, defaults, and documentation constraints within its own model.
 * </p>
 *
 * @see io.github.easy4j.dreamina.cli.DreaminaCliExecutor#invoke(String, java.util.List)
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 */
public interface DreaminaCliArgumentProvider {

    /**
     * Converts to a CLI argument list (excluding the executable name and top-level subcommand).
     *
     * @return Validated argv list
     */
    List<String> toCliArgs();
}
