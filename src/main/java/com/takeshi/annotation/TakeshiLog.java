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
     * 排除的字段名称<br/>
     * 这些参数字段将不会保存到数据库，例如："password"<br/>
     * 自动排除 ["password", "oldPassword", "newPassword", "confirmPassword"]
     *
     * @return String[]
     */
    String[] exclusionFieldName() default {};

}
