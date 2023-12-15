package com.takeshi.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * 登录类型枚举
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Getter
@Schema(description = "登录类型", enumAsRef = true)
public enum LoginTypeEnum {

    /**
     * 验证码登陆
     */
    CAPTCHA("captcha"),
    /**
     * 密码登录
     */
    PASSWORD("password"),
    /**
     * 微信登录
     */
    WE_CHAT("we_chat"),
    /**
     * 支付宝登录
     */
    ALIPAY("alipay"),
    /**
     * 抖音登录
     */
    TIKTOK("tiktok"),
    /**
     * 油管登录
     */
    YOU_TUBE("you_tube"),
    /**
     * 电报登录
     */
    TELEGRAM("telegram"),
    /**
     * 苹果登录
     */
    APPLE("apple"),
    /**
     * 谷歌登录
     */
    GOOGLE("google"),
    /**
     * 脸书登录
     */
    FACEBOOK("facebook"),
    ;

    @EnumValue
    @JsonValue
    private final String value;

    LoginTypeEnum(String value) {
        this.value = value;
    }

}
