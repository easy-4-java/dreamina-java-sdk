package io.github.easy4j.dreamina.cli.opts;

import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;
import io.github.easy4j.dreamina.cli.model.DreaminaCanvasResourceDownloadResult;
import lombok.Builder;
import lombok.Getter;

/**
 * 安全下载成功的图片素材。仅显式参数进入命令，不自动运行或批准积分。
 */
@Getter
@Builder
public final class DreaminaCanvasResourceDownloadRequest extends DreaminaCanvasRequest<DreaminaCanvasResourceDownloadResult> {
    /**
     * 项目 UUID；省略时使用当前项目；--project-id。
     */
    @DreaminaCanvasParameter(value = "project-id", positional = false)
    private final String projectId;
    /**
     * 输出文件（路径原样使用，不转码）或已存在目录（采用服务端文件名）；--output。
     */
    @DreaminaCanvasParameter(value = "output", positional = false)
    private final String output;
    /**
     * resource-id；位置参数。
     */
    @DreaminaCanvasParameter(value = "resource-id", positional = true)
    private final String resourceId;
    /**
     * 显式启用确认策略，不替代积分凭证。
     */
    private final boolean yes;

    @Override
    public DreaminaCanvasCommand getCommand() {
        return DreaminaCanvasCommand.RESOURCE_DOWNLOAD;
    }

    @Override
    public Class<DreaminaCanvasResourceDownloadResult> getDataType() {
        return DreaminaCanvasResourceDownloadResult.class;
    }

    @Override
    protected DreaminaCanvasArguments arguments() {
        DreaminaCanvasArguments args = new DreaminaCanvasArguments();
        args.flag("project-id", projectId);
        args.flag("output", output);
        args.position(resourceId);
        args.yes(yes);
        return args;
    }
}
