package com.takeshi.annotation;

import com.takeshi.enums.LogTypeEnum;

import java.lang.annotation.*;

/**
 * AOP记录接口日志到数据库中
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TakeshiLog {

    /**
     * 日志类型
     *
     * @return LogTypeEnum
     */
    LogTypeEnum logType();

    /**
     * 排除指定的请求参数
     *
     * @return String[]
     */
    String[] excludeParamNames() default {};

}
