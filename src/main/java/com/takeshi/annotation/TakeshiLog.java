package com.takeshi.annotation;

import com.takeshi.enums.LogTypeEnum;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * AOP记录接口日志到数据库中
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TakeshiLog {

    /**
     * logType的别名
     * 如果不需要其他属性，则允许更简洁的注释声明 - 例如， @TakeshiLog(LogTypeEnum.LOGIN)而不是@TakeshiLog(logType=LogTypeEnum.LOGIN)
     *
     * @return LogTypeEnum
     */
    @AliasFor("logType")
    LogTypeEnum value();

    /**
     * 日志类型
     * value是此属性的别名（且互斥）
     *
     * @return LogTypeEnum
     */
    @AliasFor("value")
    LogTypeEnum logType();

    /**
     * 排除指定的请求参数
     *
     * @return String[]
     */
    String[] excludeParamNames() default {};

}
