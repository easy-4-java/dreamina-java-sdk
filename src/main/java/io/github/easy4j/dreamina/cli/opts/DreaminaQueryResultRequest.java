package io.github.easy4j.dreamina.cli.opts;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.ArrayList;
import java.util.List;

/**
 * {@code dreamina query_result} Request object for {@code dreamina query_result}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @see io.github.easy4j.dreamina.cli.DreaminaCliExecutor#queryResult(String)
 * @since 3.0.0
 * @deprecated 旧 submit_id/download_dir 参数 与新 project/node/ref 协议不兼容；替代：DreaminaCanvasRequest + OPERATION_STATUS/RESOURCE_DOWNLOAD，素材先上传资源。
 */
@Getter
@Builder
@Deprecated
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
