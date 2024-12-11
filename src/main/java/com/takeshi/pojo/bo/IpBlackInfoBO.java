package com.takeshi.pojo.bo;

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
     * 请求超额时最后一次请求的路径
     */
    @Schema(description = "请求超额时最后一次请求的路径")
    private String lastPath;

    /**
     * IP速率限制
     */
    @Schema(description = "IP速率限制")
    private IpRate ipRate;

    /**
     * 加入黑名单的时间
     */
    @Schema(description = "加入黑名单的时间")
    private Instant instant;

    /**
     * IP速率限制
     */
    @EqualsAndHashCode(callSuper = true)
    @Data
    @Schema
    @AllArgsConstructor
    @NoArgsConstructor
    public static class IpRate extends AbstractBasicSerializable {

        /**
         * 速率，多少毫秒内允许多少次请求
         */
        private Long rate;

        /**
         * 速率时间间隔，设置0则不对接口IP限制，单位：毫秒
         */
        private Long rateInterval;

    }

}
