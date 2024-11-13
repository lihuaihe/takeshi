package com.takeshi.annotation;

import com.takeshi.config.properties.IpRateLimitProperties;
import com.takeshi.constants.TakeshiCode;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.redisson.api.RateIntervalUnit;
import org.springframework.validation.annotation.Validated;

import java.lang.annotation.*;

/**
 * 自定义注解防止重复提交
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Validated
public @interface RepeatSubmit {

    // ---------begin 此部分可对某个接口进行防重复提交限制---------

    /**
     * <p style="color:yellow;">若要使用防重功能，此值需要设置大于0</p>
     * 间隔时间，小于此时间视为重复提交
     * <br/>
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
    String msg() default TakeshiCode.REPEAT_SUBMIT_STR;

    // ---------begin 此部分可对 takeshi.rate.ip 的yml配置内容进行覆盖，但仅针对该注解修饰的请求路径生效---------

    /**
     * 可覆盖${takeshi.rate.ip.rate}值<br/>
     * 详情请参考{@link IpRateLimitProperties#rate}
     *
     * @return int
     */
    @Positive
    int ipRate() default 10;

    /**
     * 可覆盖${takeshi.rate.ip.rate-interval}值<br/>
     * 默认-1，即不覆盖，0则不校验<br/>
     * ipRateInterval大于0则ipRate，ipRateInterval，ipRateIntervalUnit，ipRateOpenBlacklist都会覆盖takeshi.rate.ip里的值<br/>
     * 详情请参考{@link IpRateLimitProperties#rateInterval}
     *
     * @return int
     */
    @Min(-1)
    int ipRateInterval() default -1;

    /**
     * 可覆盖${takeshi.rate.ip.rate-interval-unit}值<br/>
     * 详情请参考{@link IpRateLimitProperties#rateIntervalUnit}
     *
     * @return RateIntervalUnit
     */
    RateIntervalUnit ipRateIntervalUnit() default RateIntervalUnit.SECONDS;

    /**
     * 可覆盖${takeshi.rate.ip.open-blacklist}值<br/>
     * 详情请参考{@link IpRateLimitProperties#openBlacklist}
     *
     * @return boolean
     */
    boolean ipRateOpenBlacklist() default true;

    // ---------end 此部分可对 takeshi.rate.ip 的yml配置内容进行覆盖，但仅针对该注解修饰的请求路径生效---------

}
