package com.takeshi.pojo.bo;

import com.takeshi.config.properties.RateLimitProperties;
import com.takeshi.pojo.basic.AbstractBasicSerializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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
     * 请求超额的路径
     */
    @Schema(description = "请求超额的路径")
    private String path;

    /**
     * IP速率限制
     */
    @Schema(description = "IP速率限制")
    private RateLimitProperties.IpRate ipRate;

    /**
     * IpRate的配置是被RepeatSubmit中的配置覆盖了的
     */
    @Schema(description = "IpRate的配置是被RepeatSubmit中的配置覆盖了的")
    private Boolean overwritten;

    /**
     * 加入黑名单的时间
     */
    @Schema(description = "加入黑名单的时间")
    private Instant instant;

}
