package io.github.easy4j.dreamina.cli;


import io.github.easy4j.dreamina.DreaminaCanvasCliProperties;
import io.github.easy4j.dreamina.cli.model.*;
import io.github.easy4j.dreamina.cli.opts.*;
import io.github.easy4j.dreamina.cli.parser.DreaminaCanvasResponseParser;
import io.github.easy4j.dreamina.cli.support.DreaminaCanvasProcessRunner;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * 即梦画布 CLI Java 8 入口。完整命令见 {@link DreaminaCanvasCommand}，参数见随包 schema。
 * 只执行显式请求；不会自动登录、确认积分、重试生成或修改调用者的幂等标识。
 */
public final class DreaminaCanvasCliExecutor {
    private final DreaminaCanvasCliProperties config;
    private final DreaminaCanvasProcessRunner runner;
    private final DreaminaCanvasResponseParser parser = new DreaminaCanvasResponseParser();

    /**
     * 使用默认配置。
     */
    public DreaminaCanvasCliExecutor() {
        this(DreaminaCanvasCliProperties.builder().build());
    }

    /**
     * 使用独立的配置和并发限制。
     */
    public DreaminaCanvasCliExecutor(DreaminaCanvasCliProperties config) {
        this.config = DreaminaCanvasCliProperties.copyOf(config);
        runner = new DreaminaCanvasProcessRunner(this.config);
    }

    /**
     * 根据请求绑定的具体响应类型执行命令。
     */
    public <T> DreaminaCanvasResponse<T> execute(DreaminaCanvasRequest<T> request) {
        Objects.requireNonNull(request, "request");
        List<String> argv = globals(true);
        argv.addAll(request.toArguments());
        DreaminaCliResult raw = runner.run(request.getCommand().getPath(), argv);
        return parser.parse(request.getCommand().getPath(), raw, request.getDataType());
    }

    /**
     * 返回配置的防御副本，运行中的执行器不受外部 setter 影响。
     */
    public DreaminaCanvasCliProperties getProperties() {
        return DreaminaCanvasCliProperties.copyOf(config);
    }

    /**
     * 查询生成模型及其可调用规格。
     */
    public DreaminaCanvasResponse<DreaminaCanvasModelResult> model(DreaminaCanvasModelRequest request) {
        return execute(request);
    }

    /**
     * 列出生成模型及可填写 flag。
     */
    public DreaminaCanvasResponse<DreaminaCanvasModelListResult> modelList(DreaminaCanvasModelListRequest request) {
        return execute(request);
    }

    /**
     * 按名称或别名定位可调用的 canonical model。
     */
    public DreaminaCanvasResponse<DreaminaCanvasModelFindResult> modelFind(DreaminaCanvasModelFindRequest request) {
        return execute(request);
    }

    /**
     * 列出可用于 tts 的音色名称。
     */
    public DreaminaCanvasResponse<DreaminaCanvasVoiceListResult> voiceList(DreaminaCanvasVoiceListRequest request) {
        return execute(request);
    }

    /**
     * 创建图片节点，不触发生成。
     */
    public DreaminaCanvasResponse<DreaminaCanvasNodeCreateImageResult> nodeCreateImage(DreaminaCanvasNodeCreateImageRequest request) {
        return execute(request);
    }

    /**
     * 创建视频节点，不触发生成。
     */
    public DreaminaCanvasResponse<DreaminaCanvasNodeCreateVideoResult> nodeCreateVideo(DreaminaCanvasNodeCreateVideoRequest request) {
        return execute(request);
    }

    /**
     * 创建音频节点；--run 时保存、报价、按服务端要求批准并运行。
     */
    public DreaminaCanvasResponse<DreaminaCanvasNodeCreateAudioResult> nodeCreateAudio(DreaminaCanvasNodeCreateAudioRequest request) {
        return execute(request);
    }

    /**
     * 创建Element 节点，不触发生成。
     */
    public DreaminaCanvasResponse<DreaminaCanvasNodeCreateElementResult> nodeCreateElement(DreaminaCanvasNodeCreateElementRequest request) {
        return execute(request);
    }

    /**
     * 创建文本节点，不触发生成。
     */
    public DreaminaCanvasResponse<DreaminaCanvasNodeCreateTextResult> nodeCreateText(DreaminaCanvasNodeCreateTextRequest request) {
        return execute(request);
    }

    /**
     * 创建时间轴，把多个素材按顺序拼接，不触发生成。
     */
    public DreaminaCanvasResponse<DreaminaCanvasNodeCreateTimelineResult> nodeCreateTimeline(DreaminaCanvasNodeCreateTimelineRequest request) {
        return execute(request);
    }

    /**
     * 编辑图片节点，不触发生成。
     */
    public DreaminaCanvasResponse<DreaminaCanvasNodeEditImageResult> nodeEditImage(DreaminaCanvasNodeEditImageRequest request) {
        return execute(request);
    }

    /**
     * 编辑视频节点，不触发生成。
     */
    public DreaminaCanvasResponse<DreaminaCanvasNodeEditVideoResult> nodeEditVideo(DreaminaCanvasNodeEditVideoRequest request) {
        return execute(request);
    }

    /**
     * 编辑音频节点；--run 时保存、报价、按服务端要求批准并运行。
     */
    public DreaminaCanvasResponse<DreaminaCanvasNodeEditAudioResult> nodeEditAudio(DreaminaCanvasNodeEditAudioRequest request) {
        return execute(request);
    }

    /**
     * 编辑Element 节点，不触发生成。
     */
    public DreaminaCanvasResponse<DreaminaCanvasNodeEditElementResult> nodeEditElement(DreaminaCanvasNodeEditElementRequest request) {
        return execute(request);
    }

    /**
     * 编辑文本节点，不触发生成。
     */
    public DreaminaCanvasResponse<DreaminaCanvasNodeEditTextResult> nodeEditText(DreaminaCanvasNodeEditTextRequest request) {
        return execute(request);
    }

    /**
     * 编辑时间轴，把多个素材按顺序拼接，不触发生成。
     */
    public DreaminaCanvasResponse<DreaminaCanvasNodeEditTimelineResult> nodeEditTimeline(DreaminaCanvasNodeEditTimelineRequest request) {
        return execute(request);
    }

    /**
     * 图片超清：提交即执行，无 --run；等待终态直接使用 --wait。
     */
    public DreaminaCanvasResponse<DreaminaCanvasNodeUpscaleImageResult> nodeUpscaleImage(DreaminaCanvasNodeUpscaleImageRequest request) {
        return execute(request);
    }

    /**
     * 读取给定节点的视图。
     */
    public DreaminaCanvasResponse<DreaminaCanvasNodeShowResult> nodeShow(DreaminaCanvasNodeShowRequest request) {
        return execute(request);
    }

    /**
     * 按条件查节点，只回定位摘要（nodeId/type/title/status/tags）。
     */
    public DreaminaCanvasResponse<DreaminaCanvasNodeFindResult> nodeFind(DreaminaCanvasNodeFindRequest request) {
        return execute(request);
    }

    /**
     * 对给定节点做运行前积分预估。
     */
    public DreaminaCanvasResponse<DreaminaCanvasNodeQuoteResult> nodeQuote(DreaminaCanvasNodeQuoteRequest request) {
        return execute(request);
    }

    /**
     * 批准给定节点的积分消费上限并取回短期凭证。
     */
    public DreaminaCanvasResponse<DreaminaCanvasNodeConfirmResult> nodeConfirm(DreaminaCanvasNodeConfirmRequest request) {
        return execute(request);
    }

    /**
     * 触发给定节点的运行。
     */
    public DreaminaCanvasResponse<DreaminaCanvasNodeRunResult> nodeRun(DreaminaCanvasNodeRunRequest request) {
        return execute(request);
    }

    /**
     * 查询一次生成操作状态。
     */
    public DreaminaCanvasResponse<DreaminaCanvasOperationStatusResult> operationStatus(DreaminaCanvasOperationStatusRequest request) {
        return execute(request);
    }

    /**
     * 等待生成操作进入终态。
     */
    public DreaminaCanvasResponse<DreaminaCanvasOperationWaitResult> operationWait(DreaminaCanvasOperationWaitRequest request) {
        return execute(request);
    }

    /**
     * 安全下载成功的图片素材。
     */
    public DreaminaCanvasResponse<DreaminaCanvasResourceDownloadResult> resourceDownload(DreaminaCanvasResourceDownloadRequest request) {
        return execute(request);
    }

    /**
     * 查素材状态与稳定事实（running/success/failed/canceled 四态都可查）。
     */
    public DreaminaCanvasResponse<DreaminaCanvasResourceGetResult> resourceGet(DreaminaCanvasResourceGetRequest request) {
        return execute(request);
    }

    /**
     * 上传本地图片、视频或音频并登记为项目资源。
     */
    public DreaminaCanvasResponse<DreaminaCanvasResourceUploadResult> resourceUpload(DreaminaCanvasResourceUploadRequest request) {
        return execute(request);
    }

    /**
     * 创建画布。
     */
    public DreaminaCanvasResponse<DreaminaCanvasCreateResult> canvasCreate(DreaminaCanvasCreateRequest request) {
        return execute(request);
    }

    /**
     * 列举可访问的画布，只回定位摘要（projectId/name/createdAt/updatedAt/webUrl）。
     */
    public DreaminaCanvasResponse<DreaminaCanvasLsResult> canvasLs(DreaminaCanvasLsRequest request) {
        return execute(request);
    }

    /**
     * 读取服务端认证的当前账号。
     */
    public DreaminaCanvasResponse<DreaminaLoginAccount> authAccount(DreaminaCanvasAuthAccountRequest request) {
        return execute(request);
    }

    /**
     * 启动设备授权登录。
     */
    public DreaminaCanvasResponse<DreaminaCanvasAuthLoginResult> authLogin(DreaminaCanvasAuthLoginRequest request) {
        return execute(request);
    }

    /**
     * 清除当前 profile 的本地登录态。
     */
    public DreaminaCanvasResponse<DreaminaLogout> authLogout(DreaminaCanvasAuthLogoutRequest request) {
        return execute(request);
    }

    /**
     * 读取本地登录状态。
     */
    public DreaminaCanvasResponse<DreaminaCanvasAuthStatusResult> authStatus(DreaminaCanvasAuthStatusRequest request) {
        return execute(request);
    }

    /**
     * 等待已启动的设备授权完成。
     */
    public DreaminaCanvasResponse<DreaminaCanvasAuthWaitResult> authWait(DreaminaCanvasAuthWaitRequest request) {
        return execute(request);
    }

    /**
     * 刷新当前登录凭据。
     */
    public DreaminaCanvasResponse<DreaminaCanvasAuthRefreshResult> authRefresh(DreaminaCanvasAuthRefreshRequest request) {
        return execute(request);
    }

    /**
     * 返回当前制品的命令契约。
     */
    public DreaminaCanvasResponse<DreaminaCanvasSchema> schema(DreaminaCanvasSchemaRequest request) {
        return execute(request);
    }

    /**
     * 返回版本和构建信息。
     */
    public DreaminaCanvasResponse<DreaminaVersion> version(DreaminaCanvasVersionRequest request) {
        return execute(request);
    }

    /**
     * 查询当前版本，复用原版本对象。
     */
    public DreaminaCanvasResponse<DreaminaVersion> version() {
        return version(DreaminaCanvasVersionRequest.builder().build());
    }

    public DreaminaCanvasResponse<DreaminaVersion> versionDetails() {
        return version();
    }

    public DreaminaCanvasResponse<DreaminaLoginAccount> authAccountDetails() {
        return authAccount();
    }

    public DreaminaCanvasResponse<DreaminaCanvasSchema> schema() {
        return schema(DreaminaCanvasSchemaRequest.builder().build());
    }

    public DreaminaCanvasResponse<DreaminaCanvasAuthStatusResult> authStatus() {
        return authStatus(DreaminaCanvasAuthStatusRequest.builder().build());
    }

    public DreaminaCanvasResponse<DreaminaLoginAccount> authAccount() {
        return authAccount(DreaminaCanvasAuthAccountRequest.builder().build());
    }

    public DreaminaCanvasResponse<DreaminaCanvasModelListResult> modelList(String type) {
        return modelList(DreaminaCanvasModelListRequest.builder().type(type).build());
    }

    /**
     * 返回根帮助。
     */
    public DreaminaCliResult help() {
        return help("");
    }

    /**
     * 返回命令或分组帮助，如 node create、auth；路径仅接受已建模命令的前缀。
     */
    public DreaminaCliResult help(String commandPath) {
        Objects.requireNonNull(commandPath, "commandPath");
        boolean known = StringUtils.isEmpty(commandPath) || "completion".equals(commandPath)
                || Arrays.asList("completion bash", "completion zsh", "completion fish", "completion powershell", "help").contains(commandPath);
        for (DreaminaCanvasCommand command : DreaminaCanvasCommand.values()) {
            known |= command.getPath().equals(commandPath) || command.getPath().startsWith(commandPath + " ");
        }
        if (!known) {
            throw new IllegalArgumentException("Unknown Canvas help path");
        }
        List<String> argv = globals(false);
        if (StringUtils.isNotEmpty(commandPath)) {
            Collections.addAll(argv, commandPath.split(" "));
        }
        argv.add("--help");
        return runner.run("help", argv);
    }

    /**
     * 生成 bash/zsh/fish/powershell 补全脚本；不自动写入 shell 配置。
     */
    public DreaminaCliResult completion(String shell) {
        return completion(shell, false);
    }

    /**
     * 生成补全脚本，可关闭候选说明文字。
     */
    public DreaminaCliResult completion(String shell, boolean noDescriptions) {
        if (!Arrays.asList("bash", "zsh", "fish", "powershell").contains(shell)) {
            throw new IllegalArgumentException("Unsupported completion shell");
        }
        List<String> argv = globals(false);
        Collections.addAll(argv, "completion", shell);
        if (noDescriptions) {
            argv.add("--no-descriptions");
        }
        return runner.run("completion " + shell, argv);
    }

    /**
     * 执行具名辅助指令，复用文本进程结果。
     */
    public DreaminaCliResult help(DreaminaCanvasHelpRequest request) {
        Objects.requireNonNull(request, "request");
        return help(request.getTopic());
    }

    /**
     * 执行具名辅助指令，复用文本进程结果。
     */
    public DreaminaCliResult completionBash(DreaminaCanvasCompletionBashRequest request) {
        Objects.requireNonNull(request, "request");
        return completion("bash", request.isNoDescriptions());
    }

    /**
     * 执行具名辅助指令，复用文本进程结果。
     */
    public DreaminaCliResult completionZsh(DreaminaCanvasCompletionZshRequest request) {
        Objects.requireNonNull(request, "request");
        return completion("zsh", request.isNoDescriptions());
    }

    /**
     * 执行具名辅助指令，复用文本进程结果。
     */
    public DreaminaCliResult completionFish(DreaminaCanvasCompletionFishRequest request) {
        Objects.requireNonNull(request, "request");
        return completion("fish", request.isNoDescriptions());
    }

    /**
     * 执行具名辅助指令，复用文本进程结果。
     */
    public DreaminaCliResult completionPowershell(DreaminaCanvasCompletionPowershellRequest request) {
        Objects.requireNonNull(request, "request");
        return completion("powershell", request.isNoDescriptions());
    }

    private List<String> globals(boolean json) {
        List<String> argv = new ArrayList<>();
        if (json) {
            argv.add("--format=json");
        }
        argv.add("--non-interactive");
        argv.add("--profile=" + config.getProfile());
        argv.add("--region=" + config.getRegion());
        return argv;
    }
}
