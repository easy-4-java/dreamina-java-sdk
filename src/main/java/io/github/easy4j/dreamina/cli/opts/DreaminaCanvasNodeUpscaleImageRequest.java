package io.github.easy4j.dreamina.cli.opts;

import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;
import io.github.easy4j.dreamina.cli.model.DreaminaCanvasNodeUpscaleImageResult;
import lombok.Builder;
import lombok.Getter;

import java.util.Objects;

/**
 * 图片超清：提交即执行，无 --run；等待终态直接使用 --wait。仅显式参数进入命令，不自动运行或批准积分。
 */
@Getter
@Builder
public final class DreaminaCanvasNodeUpscaleImageRequest extends DreaminaCanvasRequest<DreaminaCanvasNodeUpscaleImageResult> {
    /**
     * 项目 UUID；省略时读取当前项目；--project-id。
     */
    @DreaminaCanvasParameter(value = "project-id", positional = false)
    private final String projectId;
    /**
     * 1..20 个唯一来源图片节点，可重复传入；--node-id。
     */
    @DreaminaCanvasParameter(value = "node-id", positional = false)
    @lombok.Singular("addNodeId")
    private final java.util.List<String> nodeId;
    /**
     * 逐节点提交 UUID，等长同序；省略时独立生成；--submit-id。
     */
    @DreaminaCanvasParameter(value = "submit-id", positional = false)
    @lombok.Singular("addSubmitId")
    private final java.util.List<String> submitId;
    /**
     * normal=超清；pro=智能超清；--mode。
     */
    @DreaminaCanvasParameter(value = "mode", positional = false)
    private final String mode;
    /**
     * pro 必填：目标分辨率 2K、4K 或 8K；normal 不接受；--resolution。
     */
    @DreaminaCanvasParameter(value = "resolution", positional = false)
    private final String resolution;
    /**
     * 仅 pro：细节强度 1..100，默认 50；--detail。
     */
    @DreaminaCanvasParameter(value = "detail", positional = false)
    private final Long detail;
    /**
     * 新产物节点标题；不修改来源节点；--title。
     */
    @DreaminaCanvasParameter(value = "title", positional = false)
    private final String title;
    /**
     * 本操作专用短期凭证，不接受 node confirm 凭证；--credit-token。
     */
    @DreaminaCanvasParameter(value = "credit-token", positional = false)
    private final String creditToken;
    /**
     * 批准的整批积分上限，不能与 --credit-token 同用；--credit-ceiling。
     */
    @DreaminaCanvasParameter(value = "credit-ceiling", positional = false)
    private final Long creditCeiling;
    /**
     * 提交后只读等待生成终态；默认只返回受理结果；--wait。
     */
    @DreaminaCanvasParameter(value = "wait", positional = false)
    private final Boolean waitForCompletion;
    /**
     * --wait 最长等待时间；0 表示只查一次；--timeout。
     */
    @DreaminaCanvasParameter(value = "timeout", positional = false)
    private final java.time.Duration timeout;
    /**
     * --wait 轮询间隔；--interval。
     */
    @DreaminaCanvasParameter(value = "interval", positional = false)
    private final java.time.Duration interval;
    /**
     * 只校验本地参数，不联网、报价、生成或写 journal；--dry-run。
     */
    @DreaminaCanvasParameter(value = "dry-run", positional = false)
    private final Boolean dryRun;
    /**
     * 显式启用确认策略，不替代积分凭证。
     */
    private final boolean yes;

    @Override
    public DreaminaCanvasCommand getCommand() {
        return DreaminaCanvasCommand.NODE_UPSCALE_IMAGE;
    }

    @Override
    public Class<DreaminaCanvasNodeUpscaleImageResult> getDataType() {
        return DreaminaCanvasNodeUpscaleImageResult.class;
    }

    @Override
    protected DreaminaCanvasArguments arguments() {
        DreaminaCanvasArguments args = new DreaminaCanvasArguments();
        args.flag("project-id", projectId);
        args.flag("node-id", nodeId);
        args.flag("submit-id", submitId);
        args.flag("mode", mode);
        args.flag("resolution", resolution);
        args.flag("detail", detail);
        args.flag("title", title);
        args.flag("credit-token", creditToken);
        args.flag("credit-ceiling", creditCeiling);
        args.flag("wait", waitForCompletion);
        args.flag("timeout", timeout);
        args.flag("interval", interval);
        args.flag("dry-run", dryRun);
        args.yes(yes);
        return args;
    }

    public static class DreaminaCanvasNodeUpscaleImageRequestBuilder {
        /**
         * 复用既有分辨率枚举，转换 Canvas 大写 K。
         */
        public DreaminaCanvasNodeUpscaleImageRequestBuilder resolution(DreaminaImageResolutionType value) {
            this.resolution = Objects.isNull(value) ? null : value.getCliValue().replace("k", "K");
            return this;
        }

        public DreaminaCanvasNodeUpscaleImageRequestBuilder resolution(String value) {
            this.resolution = value;
            return this;
        }
    }
}
