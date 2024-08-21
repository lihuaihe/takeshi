package com.takeshi.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 设备平台枚举
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Getter
public enum PlatformEnum {

    /**
     * iPhone
     */
    IPHONE("iPhone"),
    /**
     * iPad
     */
    IPAD("iPad"),
    /**
     * Android
     */
    ANDROID("Android"),
    /**
     * AndroidTablet
     */
    ANDROID_TABLET("AndroidTablet"),
    /**
     * GoogleTv
     */
    GOOGLE_TV("GoogleTv"),
    /**
     * Windows
     */
    WINDOWS("Windows"),
    /**
     * Mac
     */
    MAC("Mac"),
    /**
     * Linux
     */
    LINUX("Linux"),
    ;

    @EnumValue
    @JsonValue
    private final String value;

    private static final Map<String, PlatformEnum> ENUM_MAP = new HashMap<>();

    static {
        for (PlatformEnum platformEnum : values()) {
            ENUM_MAP.put(platformEnum.value, platformEnum);
        }
    }

    PlatformEnum(String value) {
        this.value = value;
    }

    /**
     * 根据value获取枚举
     *
     * @param value value
     * @return PlatformEnum
     */
    public static PlatformEnum fromValue(String value) {
        return ENUM_MAP.get(value);
    }

}
