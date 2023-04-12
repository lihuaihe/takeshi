package com.takeshi.pojo.vo;

import com.takeshi.pojo.basic.AbstractBasicSerializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * GeoPointVO
 *
 * @author 七濑武【Nanase Takeshi】
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "经纬度对象")
public class GeoPointVO extends AbstractBasicSerializable {
    /**
     * 经度
     */
    @Schema(description = "经度")
    private Double lon;
    /**
     * 纬度
     */
    @Schema(description = "纬度")
    private Double lat;
}
