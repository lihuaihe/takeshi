package com.takeshi.pojo.bo;

import com.takeshi.config.properties.RateLimitProperties;
import com.takeshi.pojo.basic.AbstractBasicSerializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.redisson.api.RateIntervalUnit;

import java.time.Instant;

/**
 * IpBlackInfoBO
 *
 * @author 七濑武【Nanase Takeshi】
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema
@AllArgsConstructor
@NoArgsConstructor
public class IpBlackInfoBO extends AbstractBasicSerializable {

    /**
     * 请求超额的IP
     */
    @Schema(description = "请求超额的IP")
    private String clientIp;

    /**
     * 请求方式
     */
    @Schema(description = "请求方式")
    private String httpMethod;

    /**
     * 请求超额的路径
     */
    @Schema(description = "请求超额的路径")
    private String path;

    /**
     * IP速率限制
     */
    @Schema(description = "IP速率限制")
    private IpRate ipRate;

    /**
     * 加入黑名单的时间
     */
    @Schema(description = "加入黑名单的时间", example = "2023-12-07T03:08:09.000Z")
    private Instant instant;

    /**
     * 详情参考{@link RateLimitProperties.IpRate}
     */
    @EqualsAndHashCode(callSuper = true)
    @Data
    @Schema
    public static class IpRate extends RateLimitProperties.IpRate {

        /**
         * IpRate的配置是被RepeatSubmit中的配置覆盖了的
         */
        @Schema(description = "IpRate的配置是被RepeatSubmit中的配置覆盖了的")
        private boolean ipOverwritten;

        /**
         * 构造函数
         *
         * @param rate             rate
         * @param rateInterval     rateInterval
         * @param rateIntervalUnit rateIntervalUnit
         * @param openBlacklist    openBlacklist
         * @param ipOverwritten    ipOverwritten
         */
        public IpRate(int rate, int rateInterval, RateIntervalUnit rateIntervalUnit, boolean openBlacklist, boolean ipOverwritten) {
            this.setRate(rate);
            this.setRateInterval(rateInterval);
            this.setRateIntervalUnit(rateIntervalUnit);
            this.setOpenBlacklist(openBlacklist);
            this.ipOverwritten = ipOverwritten;
        }

    }

}
