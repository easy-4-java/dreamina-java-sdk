package io.github.easy4j.dreamina.cli;


import lombok.Getter;

/**
 * Canvas CLI 1.0.1 全部业务调用入口；模型与音色通过运行时命令发现。
 */
@Getter
public enum DreaminaCanvasCommand {
    /**
     * 查询生成模型及其可调用规格。
     */
    MODEL("model"),
    /**
     * 列出生成模型及可填写 flag。
     */
    MODEL_LIST("model list"),
    /**
     * 按名称或别名定位可调用的 canonical model。
     */
    MODEL_FIND("model find"),
    /**
     * 列出可用于 tts 的音色名称。
     */
    VOICE_LIST("voice list"),
    /**
     * 创建图片节点，不触发生成。
     */
    NODE_CREATE_IMAGE("node create image"),
    /**
     * 创建视频节点，不触发生成。
     */
    NODE_CREATE_VIDEO("node create video"),
    /**
     * 创建音频节点；--run 时保存、报价、按服务端要求批准并运行。
     */
    NODE_CREATE_AUDIO("node create audio"),
    /**
     * 创建Element 节点，不触发生成。
     */
    NODE_CREATE_ELEMENT("node create element"),
    /**
     * 创建文本节点，不触发生成。
     */
    NODE_CREATE_TEXT("node create text"),
    /**
     * 创建时间轴，把多个素材按顺序拼接，不触发生成。
     */
    NODE_CREATE_TIMELINE("node create timeline"),
    /**
     * 编辑图片节点，不触发生成。
     */
    NODE_EDIT_IMAGE("node edit image"),
    /**
     * 编辑视频节点，不触发生成。
     */
    NODE_EDIT_VIDEO("node edit video"),
    /**
     * 编辑音频节点；--run 时保存、报价、按服务端要求批准并运行。
     */
    NODE_EDIT_AUDIO("node edit audio"),
    /**
     * 编辑Element 节点，不触发生成。
     */
    NODE_EDIT_ELEMENT("node edit element"),
    /**
     * 编辑文本节点，不触发生成。
     */
    NODE_EDIT_TEXT("node edit text"),
    /**
     * 编辑时间轴，把多个素材按顺序拼接，不触发生成。
     */
    NODE_EDIT_TIMELINE("node edit timeline"),
    /**
     * 图片超清：提交即执行，无 --run；等待终态直接使用 --wait。
     */
    NODE_UPSCALE_IMAGE("node upscale image"),
    /**
     * 读取给定节点的视图。
     */
    NODE_SHOW("node show"),
    /**
     * 按条件查节点，只回定位摘要（nodeId/type/title/status/tags）。
     */
    NODE_FIND("node find"),
    /**
     * 对给定节点做运行前积分预估。
     */
    NODE_QUOTE("node quote"),
    /**
     * 批准给定节点的积分消费上限并取回短期凭证。
     */
    NODE_CONFIRM("node confirm"),
    /**
     * 触发给定节点的运行。
     */
    NODE_RUN("node run"),
    /**
     * 查询一次生成操作状态。
     */
    OPERATION_STATUS("operation status"),
    /**
     * 等待生成操作进入终态。
     */
    OPERATION_WAIT("operation wait"),
    /**
     * 安全下载成功的图片素材。
     */
    RESOURCE_DOWNLOAD("resource download"),
    /**
     * 查素材状态与稳定事实（running/success/failed/canceled 四态都可查）。
     */
    RESOURCE_GET("resource get"),
    /**
     * 上传本地图片、视频或音频并登记为项目资源。
     */
    RESOURCE_UPLOAD("resource upload"),
    /**
     * 创建画布。
     */
    CANVAS_CREATE("canvas create"),
    /**
     * 列举可访问的画布，只回定位摘要（projectId/name/createdAt/updatedAt/webUrl）。
     */
    CANVAS_LS("canvas ls"),
    /**
     * 读取服务端认证的当前账号。
     */
    AUTH_ACCOUNT("auth account"),
    /**
     * 启动设备授权登录。
     */
    AUTH_LOGIN("auth login"),
    /**
     * 清除当前 profile 的本地登录态。
     */
    AUTH_LOGOUT("auth logout"),
    /**
     * 读取本地登录状态。
     */
    AUTH_STATUS("auth status"),
    /**
     * 等待已启动的设备授权完成。
     */
    AUTH_WAIT("auth wait"),
    /**
     * 刷新当前登录凭据。
     */
    AUTH_REFRESH("auth refresh"),
    /**
     * 返回当前制品的命令契约。
     */
    SCHEMA("schema"),
    /**
     * 返回版本和构建信息。
     */
    VERSION("version");

    private final String path;

    DreaminaCanvasCommand(String path) {
        this.path = path;
    }
}
