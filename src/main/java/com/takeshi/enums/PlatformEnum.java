package com.takeshi.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

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
     * MacOS
     */
    MACOS("MacOS"),
    /**
     * Linux
     */
    LINUX("Linux"),
    ;

    @EnumValue
    @JsonValue
    private final String name;

    PlatformEnum(String name) {
        this.name = name;
    }

}
