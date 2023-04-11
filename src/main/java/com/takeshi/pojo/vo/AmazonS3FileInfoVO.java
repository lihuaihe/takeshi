package com.takeshi.pojo.vo;

import com.takeshi.pojo.basic.AbstractBasicSerializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * AmazonS3FileInfoVO
 *
 * @author 七濑武【Nanase Takeshi】
 * @since 2023/1/31 16:30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@Schema(name = "AmazonS3文件对象")
@AllArgsConstructor
@NoArgsConstructor
public class AmazonS3FileInfoVO extends AbstractBasicSerializable {
    @Schema(description = "文件URL")
    private String url;
    @Schema(description = "文件名称")
    private String fileName;
    @Schema(description = "文件类型")
    private String contentType;
    @Schema(description = "文件扩展名")
    private String extensionName;
    @Schema(description = "文件创建时间")
    private String createTime;
    @Schema(description = "文件大小（单位：字节）")
    private Long size;
}
