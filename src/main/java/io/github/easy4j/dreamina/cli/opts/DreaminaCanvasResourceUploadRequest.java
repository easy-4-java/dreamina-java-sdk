package io.github.easy4j.dreamina.cli.opts;

import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;
import io.github.easy4j.dreamina.cli.model.DreaminaCanvasResourceUploadResult;
import lombok.Builder;
import lombok.Getter;

/**
 * 上传本地图片、视频或音频并登记为项目资源。仅显式参数进入命令，不自动运行或批准积分。
 */
@Getter
@Builder
public final class DreaminaCanvasResourceUploadRequest extends DreaminaCanvasRequest<DreaminaCanvasResourceUploadResult> {
    /**
     * 本地图片、视频或音频文件路径；与 --source-url 二选一；--file。
     */
    @DreaminaCanvasParameter(value = "file", positional = false)
    private final String file;
    /**
     * 媒体类型覆盖：image、video 或 audio；省略时按扩展名判断；--type。
     */
    @DreaminaCanvasParameter(value = "type", positional = false)
    private final String type;
    /**
     * 服务端可访问的图片 URL；与 --file 二选一（仅图片）；--source-url。
     */
    @DreaminaCanvasParameter(value = "source-url", positional = false)
    private final String sourceUrl;
    /**
     * 项目 UUID；省略时使用当前项目；--project-id。
     */
    @DreaminaCanvasParameter(value = "project-id", positional = false)
    private final String projectId;
    /**
     * 资源 UUID 幂等键；省略时自动生成，跨进程重试须复用同一值；--resource-id。
     */
    @DreaminaCanvasParameter(value = "resource-id", positional = false)
    private final String resourceId;
    /**
     * 素材展示名；省略时用文件名；--name。
     */
    @DreaminaCanvasParameter(value = "name", positional = false)
    private final String name;
    /**
     * 登记意图：local_upload（默认）或 external_generated（外部平台生成产物导入）；--import-kind。
     */
    @DreaminaCanvasParameter(value = "import-kind", positional = false)
    private final String importKind;
    /**
     * 显式启用确认策略，不替代积分凭证。
     */
    private final boolean yes;

    @Override
    public DreaminaCanvasCommand getCommand() {
        return DreaminaCanvasCommand.RESOURCE_UPLOAD;
    }

    @Override
    public Class<DreaminaCanvasResourceUploadResult> getDataType() {
        return DreaminaCanvasResourceUploadResult.class;
    }

    @Override
    protected DreaminaCanvasArguments arguments() {
        DreaminaCanvasArguments args = new DreaminaCanvasArguments();
        args.flag("file", file);
        args.flag("type", type);
        args.flag("source-url", sourceUrl);
        args.flag("project-id", projectId);
        args.flag("resource-id", resourceId);
        args.flag("name", name);
        args.flag("import-kind", importKind);
        args.yes(yes);
        return args;
    }
}
