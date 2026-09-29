package io.github.easy4j.dreamina.cli.opts;

import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;
import io.github.easy4j.dreamina.cli.model.DreaminaCanvasNodeEditAudioResult;
import lombok.Builder;
import lombok.Getter;

/**
 * 编辑音频节点；--run 时保存、报价、按服务端要求批准并运行。仅显式参数进入命令，不自动运行或批准积分。
 */
@Getter
@Builder
public final class DreaminaCanvasNodeEditAudioRequest extends DreaminaCanvasRequest<DreaminaCanvasNodeEditAudioResult> {
    /**
     * 项目 UUID；省略时使用当前项目；--project-id。
     */
    @DreaminaCanvasParameter(value = "project-id", positional = false)
    private final String projectId;
    /**
     * 要编辑的节点 node_id；必填；--node-id。
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
     * 清空 generationDraft；--clear-generation。
     */
    @DreaminaCanvasParameter(value = "clear-generation", positional = false)
    private final Boolean clearGeneration;
    /**
     * 音频生成模式：tts（text-to-speech，文本转语音）或 music（音乐生成）；--mode。
     */
    @DreaminaCanvasParameter(value = "mode", positional = false)
    private final String mode;
    /**
     * TTS 文本或音乐描述；--prompt。
     */
    @DreaminaCanvasParameter(value = "prompt", positional = false)
    private final String prompt;
    /**
     * music 模式必填；从当前环境 model list --type audio 返回的 mode=music 结果选择；--model。
     */
    @DreaminaCanvasParameter(value = "model", positional = false)
    private final String model;
    /**
     * music 模式时长（秒）；--duration。
     */
    @DreaminaCanvasParameter(value = "duration", positional = false)
    private final java.math.BigDecimal duration;
    /**
     * tts 音色名称；从 voice list 选择，由服务端解析成权威身份；--voice-name。
     */
    @DreaminaCanvasParameter(value = "voice-name", positional = false)
    private final String voiceName;
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
        return DreaminaCanvasCommand.NODE_EDIT_AUDIO;
    }

    @Override
    public Class<DreaminaCanvasNodeEditAudioResult> getDataType() {
        return DreaminaCanvasNodeEditAudioResult.class;
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
        args.flag("clear-generation", clearGeneration);
        args.flag("mode", mode);
        args.flag("prompt", prompt);
        args.flag("model", model);
        args.flag("duration", duration);
        args.flag("voice-name", voiceName);
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
}
