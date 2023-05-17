package com.takeshi.pojo.vo;

import cn.hutool.core.lang.Assert;
import com.takeshi.pojo.basic.AbstractBasicSerializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * GeoPointVO
 *
 * @author 七濑武【Nanase Takeshi】
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "经纬度对象")
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

    /**
     * 构造函数
     *
     * @param lon 经度
     * @param lat 纬度
     */
    public GeoPointVO(Double lon, Double lat) {
        Assert.isTrue(lat >= -90.0 && lat <= 90.0, "Latitude must be in the range of [-90, 90] degrees");
        Assert.isTrue(lon >= -180.0 && lon <= 180.0, "Longitude must be in the range of [-180, 180] degrees");
        this.lon = lon;
        this.lat = lat;
    }

    /**
     * 设置经度
     *
     * @param lon 经度
     */
    public void setLon(Double lon) {
        Assert.isTrue(lon >= -180.0 && lon <= 180.0, "Longitude must be in the range of [-180, 180] degrees");
        this.lon = lon;
    }

    /**
     * 设置纬度
     *
     * @param lat 纬度
     */
    public void setLat(Double lat) {
        Assert.isTrue(lat >= -90.0 && lat <= 90.0, "Latitude must be in the range of [-90, 90] degrees");
        this.lat = lat;
    }

}
