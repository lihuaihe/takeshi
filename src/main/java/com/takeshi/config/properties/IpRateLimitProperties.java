package com.takeshi.config.properties;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import org.redisson.api.RateIntervalUnit;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * 同个IP对接口请求的限制，超过限制将会把IP拉入黑名单直至当天结束时间（例如：2023-04-23 23:59:59）
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Data
@AutoConfiguration(value = "rateLimitProperties")
@ConfigurationProperties(prefix = "takeshi.ip-rate")
@Validated
public class IpRateLimitProperties {

    /**
     * 率
     */
    @Positive
    private int rate = 10;

    /**
     * 速率时间间隔，设置0则不对接口IP限制
     */
    @PositiveOrZero
    private int rateInterval = 1;

    /**
     * 速率时间间隔单位
     */
    private RateIntervalUnit rateIntervalUnit = RateIntervalUnit.SECONDS;

    /**
     * 是否开启IP黑名单
     */
    private boolean openBlacklist = true;

}
