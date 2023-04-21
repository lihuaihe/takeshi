package com.takeshi.annotation;

import java.lang.annotation.*;

/**
 * 自定义注解防止重复提交
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RepeatSubmit {

    /**
     * 间隔时间(ms)，小于此时间视为重复提交
     *
     * @return 时间(ms)
     */
    long interval() default 1000L;

    /**
     * 忽略的字段名称
     *
     * @return ignoredFieldNames
     */
    String[] ignoredFieldNames() default {};

    /**
     * 提示语信息
     *
     * @return msg
     */
    String msg() default "";

}
