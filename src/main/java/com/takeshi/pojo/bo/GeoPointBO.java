package com.takeshi.pojo.bo;

import cn.hutool.core.lang.Assert;
import com.takeshi.pojo.basic.AbstractBasicSerializable;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * GeoPointBO
 *
 * @author 七濑武【Nanase Takeshi】
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "经纬度对象")
@NoArgsConstructor
public class GeoPointBO extends AbstractBasicSerializable {

    /**
     * 经度
     */
    @NotNull
    @Schema(description = "经度")
    private Double lon;

    /**
     * 纬度
     */
    @NotNull
    @Schema(description = "纬度")
    private Double lat;

    /**
     * 构造函数
     *
     * @param lon 经度
     * @param lat 纬度
     */
    public GeoPointBO(@NotNull Double lon, @NotNull Double lat) {
        this.setLon(lon);
        this.setLat(lat);
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
