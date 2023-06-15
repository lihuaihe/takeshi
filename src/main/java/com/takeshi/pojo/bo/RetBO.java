package com.takeshi.pojo.bo;

import com.takeshi.pojo.basic.AbstractBasicSerializable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 结果对象
 *
 * @author 七濑武【Nanase Takeshi】
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@Accessors(chain = true)
@Schema
public class RetBO extends AbstractBasicSerializable {

    /**
     * 状态码
     */
    @Schema(description = "状态码")
    private int code;

    /**
     * 提示信息
     */
    @Schema(description = "提示信息")
    private String message;

}
