package com.takeshi.enums;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 操作状态枚举
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Schema(description = "操作状态枚举")
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
