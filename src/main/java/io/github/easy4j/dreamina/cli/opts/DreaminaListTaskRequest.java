package io.github.easy4j.dreamina.cli.opts;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

/**
 * {@code dreamina list_task} Request object for {@code dreamina list_task}.
 *
 * @see io.github.easy4j.dreamina.cli.DreaminaCliExecutor#listTask()
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 3.0.0
 */
@Getter
@Builder
public class DreaminaListTaskRequest implements DreaminaCliArgumentProvider {

    private final String genStatus;
    private final String genTaskType;
    private final String submitId;
    private final Integer limit;
    private final Integer offset;

    @Singular("additionalArg")
    private final List<String> additionalRawArgs;

    @Override
    public List<String> toCliArgs() {
        List<String> args = new ArrayList<>();
        DreaminaCliRequestSupport.addFlag(args, "--gen_status", genStatus);
        DreaminaCliRequestSupport.addFlag(args, "--gen_task_type", genTaskType);
        DreaminaCliRequestSupport.addFlag(args, "--submit_id", submitId);
        DreaminaCliRequestSupport.requireNonNegative(limit, "limit");
        DreaminaCliRequestSupport.addFlag(args, "--limit", limit);
        DreaminaCliRequestSupport.requireNonNegative(offset, "offset");
        DreaminaCliRequestSupport.addFlag(args, "--offset", offset);
        DreaminaCliRequestSupport.addAdditionalArgs(args, additionalRawArgs);
        return args;
    }
}
