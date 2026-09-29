package io.github.easy4j.dreamina.cli.opts;

import io.github.easy4j.dreamina.cli.DreaminaCanvasCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 具体命令请求的公共协议；响应类型由请求绑定，调用者无需选择反序列化类型。
 */
public abstract class DreaminaCanvasRequest<T> implements DreaminaCliArgumentProvider {
    /**
     * 返回请求对应的官方指令。
     */
    public abstract DreaminaCanvasCommand getCommand();

    /**
     * 返回官方业务数据类型。
     */
    public abstract Class<T> getDataType();

    protected abstract DreaminaCanvasArguments arguments();

    /**
     * 执行前校验并生成原样 argv，不经过 shell。
     */
    @Override
    public final List<String> toCliArgs() {
        DreaminaCanvasArguments args = arguments();
        DreaminaCanvasContract.validate(getCommand(), args);
        return Collections.unmodifiableList(args.render());
    }

    /**
     * 返回完整命令路径及参数。
     */
    public final List<String> toArguments() {
        List<String> result = new ArrayList<>(Arrays.asList(getCommand().getPath().split(" ")));
        result.addAll(toCliArgs());
        return Collections.unmodifiableList(result);
    }
}
