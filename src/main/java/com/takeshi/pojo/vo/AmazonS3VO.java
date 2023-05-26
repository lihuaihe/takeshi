package com.takeshi.pojo.vo;

import com.takeshi.pojo.basic.AbstractBasicSerializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.net.URL;

/**
 * AmazonS3VO
 *
 * @author 七濑武【Nanase Takeshi】
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "S3回显信息")
@AllArgsConstructor
@NoArgsConstructor
public class AmazonS3VO extends AbstractBasicSerializable {

    /**
     * S3中的key
     */
    @Schema(description = "S3中的key，传给后端要用的")
    private String key;

    /**
     * 前端访问的预签名URL
     */
    @Schema(description = "前端访问的预签名URL，不用传给后端")
    private URL url;

}
