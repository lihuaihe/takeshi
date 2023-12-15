package com.takeshi.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
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
    FAIL("fail"),
    /**
     * 成功
     */
    SUCCESS("success"),
    ;

    @EnumValue
    @JsonValue
    private final String value;

    BusinessStatusEnum(String value) {
        this.value = value;
    }

}
