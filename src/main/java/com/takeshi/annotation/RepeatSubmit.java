package com.takeshi.annotation;

import jakarta.validation.constraints.Min;
import org.redisson.api.RateIntervalUnit;

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
     * 间隔时间，小于此时间视为重复提交<br/>
     * 设置0则不开启重复提交校验
     *
     * @return 时间
     */
    @Min(0)
    int rateInterval() default 0;

    /**
     * 重复提交 速率时间间隔单位
     *
     * @return 时间单位
     */
    RateIntervalUnit rateIntervalUnit() default RateIntervalUnit.SECONDS;

    /**
     * 重复提交 忽略的字段名称
     *
     * @return ignoredFieldNames
     */
    String[] ignoredFieldNames() default {};

    /**
     * 重复提交 提示语信息
     *
     * @return msg
     */
    String msg() default "";

    /**
     * 可覆盖${takeshi.rate.ip.rate}值
     *
     * @return int
     */
    @Min(1)
    int ipRate() default 5;

    /**
     * 可覆盖${takeshi.rate.ip.rateInterval}值
     *
     * @return int
     */
    @Min(0)
    int ipRateInterval() default 0;

    /**
     * 可覆盖${takeshi.rate.ip.rateIntervalUnit}值
     *
     * @return RateIntervalUnit
     */
    RateIntervalUnit ipRateIntervalUnit() default RateIntervalUnit.SECONDS;

    /**
     * 可覆盖${takeshi.rate.ip.openBlacklist}值
     *
     * @return boolean
     */
    boolean ipRateOpenBlacklist() default true;

}
