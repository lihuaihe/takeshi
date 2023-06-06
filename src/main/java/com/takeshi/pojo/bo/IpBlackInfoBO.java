package com.takeshi.pojo.bo;

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
     * 请求超额的路径
     */
    @Schema(description = "请求超额的路径")
    private String path;

    /**
     * 加入黑名单的时间
     */
    @Schema(description = "加入黑名单的时间")
    private Instant instant;

}
