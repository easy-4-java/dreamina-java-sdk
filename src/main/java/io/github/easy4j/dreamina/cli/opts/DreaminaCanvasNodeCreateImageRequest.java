package io.github.easy4j.dreamina.cli.opts;

import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;
import io.github.easy4j.dreamina.cli.model.DreaminaCanvasNodeCreateImageResult;
import lombok.Builder;
import lombok.Getter;

import java.util.Objects;

/**
 * 创建图片节点，不触发生成。仅显式参数进入命令，不自动运行或批准积分。
 */
@Getter
@Builder
public final class DreaminaCanvasNodeCreateImageRequest extends DreaminaCanvasRequest<DreaminaCanvasNodeCreateImageResult> {
    /**
     * 项目 UUID；省略时使用当前项目；--project-id。
     */
    @DreaminaCanvasParameter(value = "project-id", positional = false)
    private final String projectId;
    /**
     * 稳定 node_id；省略时生成；--node-id。
     */
    @DreaminaCanvasParameter(value = "node-id", positional = false)
    private final String nodeId;
    /**
     * 稳定更新 UUID；省略时生成；--update-id。
     */
    @DreaminaCanvasParameter(value = "update-id", positional = false)
    private final String updateId;
    /**
     * 只做本地校验并输出计划；不连接服务端、不写草稿、不报价或扣分；--dry-run。
     */
    @DreaminaCanvasParameter(value = "dry-run", positional = false)
    private final Boolean dryRun;
    /**
     * 节点标题；按 flag 出现更新；--title。
     */
    @DreaminaCanvasParameter(value = "title", positional = false)
    private final String title;
    /**
     * 颜色标签 ID，可重复传入；取值 default-tag-1..default-tag-5，除非用户明确指定标签否则不要默认传；--tag。
     */
    @DreaminaCanvasParameter(value = "tag", positional = false)
    @lombok.Singular("addTag")
    private final java.util.List<String> tag;
    /**
     * 把节点标签完整集合清空；--clear-tags。
     */
    @DreaminaCanvasParameter(value = "clear-tags", positional = false)
    private final Boolean clearTags;
    /**
     * 图片生成模式：t2i（text-to-image，文生图）或 i2i（image-to-image，图生图）；提供生成参数时必须显式指定，纯元信息更新或清空生成配置无需传入；--mode。
     */
    @DreaminaCanvasParameter(value = "mode", positional = false)
    private final String mode;
    /**
     * 完整图片生成提示词；引用写成 {{node:node_xxx}}、{{res:resourceId}}、{{uri:value}} 或 {{vid:value}}，写字面 {{ 用 \{{ 转义；画布已有来源节点时优先使用 Node ID，保留上游连线；Resource ID 用于资产库等无来源节点的素材或用户明确要求冻结，不自动从节点产物提取；--prompt。
     */
    @DreaminaCanvasParameter(value = "prompt", positional = false)
    private final String prompt;
    /**
     * 服务端登记的图片模型标识；--model。
     */
    @DreaminaCanvasParameter(value = "model", positional = false)
    private final String model;
    /**
     * 图片宽高比，例如 1:1；--ratio。
     */
    @DreaminaCanvasParameter(value = "ratio", positional = false)
    private final String ratio;
    /**
     * 图片分辨率，例如 1K；--resolution。
     */
    @DreaminaCanvasParameter(value = "resolution", positional = false)
    private final String resolution;
    /**
     * 生成数量；上限以 model list 返回的 --count max 为准；--count。
     */
    @DreaminaCanvasParameter(value = "count", positional = false)
    private final Long count;
    /**
     * 完整类型化引用集合：node:value、res:value、uri:value 或 vid:value，可重复传入；画布已有来源节点时优先使用 Node ID，保留上游连线；Resource ID 用于资产库等无来源节点的素材或用户明确要求冻结，不自动从节点产物提取；--ref。
     */
    @DreaminaCanvasParameter(value = "ref", positional = false)
    @lombok.Singular("addRef")
    private final java.util.List<String> ref;
    /**
     * 本节点已有产物的资源 UUID，可重复传入；需同时提供 --import-kind local_upload 或 external_generated。多个视为同一批次内的候选，顺序即候选顺序，首个为主选。资源须已登记、状态成功且类型为图片；--resource-id。
     */
    @DreaminaCanvasParameter(value = "resource-id", positional = false)
    @lombok.Singular("addResourceId")
    private final java.util.List<String> resourceId;
    /**
     * 节点导入类别：local_upload（挂本地上传登记的资源）或 external_generated（外部平台生成产物导入）；与 resource upload 的登记意图同一套词汇；--import-kind。
     */
    @DreaminaCanvasParameter(value = "import-kind", positional = false)
    private final String importKind;
    /**
     * external_generated 导入节点在根画布中的 X 坐标；必须与 --y 成组提供；--x。
     */
    @DreaminaCanvasParameter(value = "x", positional = false)
    private final java.math.BigDecimal x;
    /**
     * external_generated 导入节点在根画布中的 Y 坐标；必须与 --x 成组提供；--y。
     */
    @DreaminaCanvasParameter(value = "y", positional = false)
    private final java.math.BigDecimal y;
    /**
     * 保存后依次报价、批准并运行该节点；--run。
     */
    @DreaminaCanvasParameter(value = "run", positional = false)
    private final Boolean run;
    /**
     * 稳定提交 UUID；--run 或 --resource-id 时省略则生成，停顿后续跑或重试导入须复用它；--submit-id。
     */
    @DreaminaCanvasParameter(value = "submit-id", positional = false)
    private final String submitId;
    /**
     * 已批准的 creditConfirmationToken；--run 时使用；--credit-token。
     */
    @DreaminaCanvasParameter(value = "credit-token", positional = false)
    private final String creditToken;
    /**
     * 批准的积分消费上限；--run 时报价超过它会在运行前停止；--credit-ceiling。
     */
    @DreaminaCanvasParameter(value = "credit-ceiling", positional = false)
    private final Long creditCeiling;
    /**
     * --run 后等待该节点生成进入终态；--wait。
     */
    @DreaminaCanvasParameter(value = "wait", positional = false)
    private final Boolean waitForCompletion;
    /**
     * --wait 的最长等待时间；默认 10m，0 表示只查询一次；--timeout。
     */
    @DreaminaCanvasParameter(value = "timeout", positional = false)
    private final java.time.Duration timeout;
    /**
     * --wait 的轮询间隔；默认 5s；--interval。
     */
    @DreaminaCanvasParameter(value = "interval", positional = false)
    private final java.time.Duration interval;
    /**
     * 显式启用确认策略，不替代积分凭证。
     */
    private final boolean yes;

    @Override
    public DreaminaCanvasCommand getCommand() {
        return DreaminaCanvasCommand.NODE_CREATE_IMAGE;
    }

    @Override
    public Class<DreaminaCanvasNodeCreateImageResult> getDataType() {
        return DreaminaCanvasNodeCreateImageResult.class;
    }

    @Override
    protected DreaminaCanvasArguments arguments() {
        DreaminaCanvasArguments args = new DreaminaCanvasArguments();
        args.flag("project-id", projectId);
        args.flag("node-id", nodeId);
        args.flag("update-id", updateId);
        args.flag("dry-run", dryRun);
        args.flag("title", title);
        args.flag("tag", tag);
        args.flag("clear-tags", clearTags);
        args.flag("mode", mode);
        args.flag("prompt", prompt);
        args.flag("model", model);
        args.flag("ratio", ratio);
        args.flag("resolution", resolution);
        args.flag("count", count);
        args.flag("ref", ref);
        args.flag("resource-id", resourceId);
        args.flag("import-kind", importKind);
        args.flag("x", x);
        args.flag("y", y);
        args.flag("run", run);
        args.flag("submit-id", submitId);
        args.flag("credit-token", creditToken);
        args.flag("credit-ceiling", creditCeiling);
        args.flag("wait", waitForCompletion);
        args.flag("timeout", timeout);
        args.flag("interval", interval);
        args.yes(yes);
        return args;
    }

    public static class DreaminaCanvasNodeCreateImageRequestBuilder {
        /**
         * 复用既有画幅枚举。
         */
        public DreaminaCanvasNodeCreateImageRequestBuilder ratio(DreaminaRatio value) {
            this.ratio = Objects.isNull(value) ? null : value.getCliValue();
            return this;
        }

        public DreaminaCanvasNodeCreateImageRequestBuilder ratio(String value) {
            this.ratio = value;
            return this;
        }

        /**
         * 复用既有分辨率枚举，转换 Canvas 大写 K。
         */
        public DreaminaCanvasNodeCreateImageRequestBuilder resolution(DreaminaImageResolutionType value) {
            this.resolution = Objects.isNull(value) ? null : value.getCliValue().replace("k", "K");
            return this;
        }

        public DreaminaCanvasNodeCreateImageRequestBuilder resolution(String value) {
            this.resolution = value;
            return this;
        }
    }
}
