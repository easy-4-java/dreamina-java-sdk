package io.github.easy4j.dreamina.cli.opts;

import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;
import io.github.easy4j.dreamina.cli.model.DreaminaCanvasNodePromoteVideoSampleResult;
import lombok.Builder;
import lombok.Getter;

import java.util.Objects;

/**
 * 样片转正片：报价并批准后生成新节点。仅显式参数进入命令，不自动运行或批准积分。
 */
@Getter
@Builder
public final class DreaminaCanvasNodePromoteVideoSampleRequest extends DreaminaCanvasRequest<DreaminaCanvasNodePromoteVideoSampleResult> {
    /**
     * 项目 UUID；省略时读取当前项目；--project-id。
     */
    @DreaminaCanvasParameter(value = "project-id", positional = false)
    private final String projectId;
    /**
     * 来源样片节点 ID；--node-id。
     */
    @DreaminaCanvasParameter(value = "node-id", positional = false)
    private final String nodeId;
    /**
     * 目标分辨率，取 search --detail full 的 draftPromotion；--resolution。
     */
    @DreaminaCanvasParameter(value = "resolution", positional = false)
    private final String resolution;
    /**
     * 提交 UUID；省略时生成；超时后用该 ID 查询；--submit-id。
     */
    @DreaminaCanvasParameter(value = "submit-id", positional = false)
    private final String submitId;
    /**
     * 新正片节点标题；--title。
     */
    @DreaminaCanvasParameter(value = "title", positional = false)
    private final String title;
    /**
     * 本次样片转正专用短期凭证；--credit-token。
     */
    @DreaminaCanvasParameter(value = "credit-token", positional = false)
    private final String creditToken;
    /**
     * 批准的积分上限；不能与 credit-token 同用；--credit-ceiling。
     */
    @DreaminaCanvasParameter(value = "credit-ceiling", positional = false)
    private final Long creditCeiling;
    /**
     * 提交后只读等待正片终态；--wait。
     */
    @DreaminaCanvasParameter(value = "wait", positional = false)
    private final Boolean waitForCompletion;
    /**
     * wait 最长等待时间；--timeout。
     */
    @DreaminaCanvasParameter(value = "timeout", positional = false)
    private final java.time.Duration timeout;
    /**
     * wait 轮询间隔；--interval。
     */
    @DreaminaCanvasParameter(value = "interval", positional = false)
    private final java.time.Duration interval;
    /**
     * 仅校验本地参数，不联网或消费积分；--dry-run。
     */
    @DreaminaCanvasParameter(value = "dry-run", positional = false)
    private final Boolean dryRun;
    /**
     * 显式启用确认策略，不替代积分凭证。
     */
    private final boolean yes;

    @Override
    public DreaminaCanvasCommand getCommand() {
        return DreaminaCanvasCommand.NODE_PROMOTE_VIDEO_SAMPLE;
    }

    @Override
    public Class<DreaminaCanvasNodePromoteVideoSampleResult> getDataType() {
        return DreaminaCanvasNodePromoteVideoSampleResult.class;
    }

    @Override
    protected DreaminaCanvasArguments arguments() {
        DreaminaCanvasArguments args = new DreaminaCanvasArguments();
        args.flag("project-id", projectId);
        args.flag("node-id", nodeId);
        args.flag("resolution", resolution);
        args.flag("submit-id", submitId);
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

    public static class DreaminaCanvasNodePromoteVideoSampleRequestBuilder {
        /**
         * 复用既有分辨率枚举，转换 Canvas 大写 K。
         */
        public DreaminaCanvasNodePromoteVideoSampleRequestBuilder resolution(DreaminaVideoResolutionType value) {
            this.resolution = Objects.isNull(value) ? null : value.getCliValue().replace("k", "K");
            return this;
        }

        public DreaminaCanvasNodePromoteVideoSampleRequestBuilder resolution(String value) {
            this.resolution = value;
            return this;
        }
    }
}
