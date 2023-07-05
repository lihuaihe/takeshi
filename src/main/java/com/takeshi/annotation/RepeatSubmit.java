package com.takeshi.annotation;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
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
    @PositiveOrZero
    int rateInterval() default 0;

    /**
     * 重复提交 速率时间间隔单位
     *
     * @return 时间单位
     */
    RateIntervalUnit rateIntervalUnit() default RateIntervalUnit.SECONDS;

    /**
     * 重复提交 排除的字段名称<br/>
     * 这些参数字段将不会参与重复提交校验，例如："requestTime"
     *
     * @return ignoredFieldNames
     */
    String[] exclusionFieldName() default {};

    /**
     * 重复提交 提示语信息
     *
     * @return msg
     */
    String msg() default "";

    // ---------此以上部分可对某个接口进行防重复提交限制---------

    // ---------此以下部分可对takeshi.rate.ip的配置内容进行覆盖---------

    /**
     * 可覆盖${takeshi.rate.ip.rate}值
     *
     * @return int
     */
    @Positive
    int ipRate() default 5;

    /**
     * 可覆盖${takeshi.rate.ip.rateInterval}值<br/>
     * ipRateInterval大于0则ipRate，ipRateInterval，ipRateIntervalUnit，ipRateOpenBlacklist都会覆盖takeshi.rate.ip里的值
     *
     * @return int
     */
    @PositiveOrZero
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
