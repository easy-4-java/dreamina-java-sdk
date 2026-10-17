package io.github.easy4j.dreamina.cli.opts;

import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;
import io.github.easy4j.dreamina.cli.model.DreaminaCanvasVoiceListResult;
import lombok.Builder;
import lombok.Getter;

/**
 * 列出可用于 tts 的音色名称。仅显式参数进入命令，不自动运行或批准积分。
 */
@Getter
@Builder
public final class DreaminaCanvasVoiceListRequest extends DreaminaCanvasRequest<DreaminaCanvasVoiceListResult> {
    /**
     * 分页起点；--offset。
     */
    @DreaminaCanvasParameter(value = "offset", positional = false)
    private final Long offset;
    /**
     * 单页条数，范围 1 到 100；--count。
     */
    @DreaminaCanvasParameter(value = "count", positional = false)
    private final Long count;
    /**
     * 可选 canonical model；省略按模型返回当地已接入的全部 TTS 音色，翻页保持同一模型；--model。
     */
    @DreaminaCanvasParameter(value = "model", positional = false)
    private final String model;
    /**
     * 显式启用确认策略，不替代积分凭证。
     */
    private final boolean yes;

    @Override
    public DreaminaCanvasCommand getCommand() {
        return DreaminaCanvasCommand.VOICE_LIST;
    }

    @Override
    public Class<DreaminaCanvasVoiceListResult> getDataType() {
        return DreaminaCanvasVoiceListResult.class;
    }

    @Override
    protected DreaminaCanvasArguments arguments() {
        DreaminaCanvasArguments args = new DreaminaCanvasArguments();
        args.flag("offset", offset);
        args.flag("count", count);
        args.flag("model", model);
        args.yes(yes);
        return args;
    }
}
