package io.github.easy4j.dreamina.cli.opts;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Java 字段与官方命令参数的可审计映射。
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DreaminaCanvasParameter {
    String value();

    boolean positional() default false;
}
