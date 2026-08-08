package io.github.easy4j.dreamina.cli.opts;

import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

/**
 * {@code dreamina query_result} Request object for {@code dreamina query_result}.
 *
 * @see io.github.easy4j.dreamina.cli.DreaminaCliExecutor#queryResult(String)
 *
 * @author [@Loong Wan](https://github.com/loong10k)
 * @since 3.0.0
 */
@Getter
@Builder
public class DreaminaQueryResultRequest implements DreaminaCliArgumentProvider {

    private final String submitId;
    private final String downloadDir;

    @Singular("additionalArg")
    private final List<String> additionalRawArgs;

    @Override
    public List<String> toCliArgs() {
        List<String> args = new ArrayList<>();
        DreaminaCliRequestSupport.addFlag(
            args, "--submit_id", DreaminaCliRequestSupport.requireNonBlank(submitId, "submitId"));
        DreaminaCliRequestSupport.addFlag(args, "--download_dir", downloadDir);
        DreaminaCliRequestSupport.addAdditionalArgs(args, additionalRawArgs);
        return args;
    }
}
