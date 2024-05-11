package com.takeshi.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * 操作状态枚举
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Getter
@Schema(description = "操作状态", enumAsRef = true)
public enum BusinessStatusEnum {

    /**
     * 失败
     */
    FAIL,
    /**
     * 成功
     */
    SUCCESS,
    ;

}
