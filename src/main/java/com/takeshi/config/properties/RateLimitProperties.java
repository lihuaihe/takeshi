package com.takeshi.config.properties;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import org.redisson.api.RateIntervalUnit;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * RateLimitProperties
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Data
@AutoConfiguration
@ConfigurationProperties(prefix = "takeshi.rate")
@Validated
public class RateLimitProperties {

    /**
     * 接口header里传递的timestamp最多只能早于或晚于系统当前时间{maxTimeDiff}秒<br/>
     * 设置0则不校验
     */
    @PositiveOrZero
    private int maxTimeDiff = 0;

    /**
     * 接口header里传递的nonce限制
     */
    private NonceRate nonce = new NonceRate();

    /**
     * 同个IP对接口请求的限制，超过限制将会把IP拉入黑名单直至当天结束时间（例如：2023-04-23 23:59:59）
     */
    private IpRate ip = new IpRate();

    /**
     * 接口header里传递的nonce限制，例如：设置一天内nonce只能使用一次
     */
    @Data
    public static class NonceRate {

        /**
         * 率
         */
        @Positive
        private int rate = 1;

        /**
         * 速率时间间隔，设置0则不对nonce限制
         */
        @PositiveOrZero
        private int rateInterval = 0;

        /**
         * 速率时间间隔单位
         */
        private RateIntervalUnit rateIntervalUnit = RateIntervalUnit.DAYS;

    }

    /**
     * 同个IP对接口请求的限制，超过限制将会把IP拉入黑名单直至当天结束时间（例如：2023-04-23 23:59:59）
     */
    @Data
    public static class IpRate {

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

}
