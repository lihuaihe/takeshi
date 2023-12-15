package com.takeshi.enums;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 登录类型枚举
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Schema(description = "登录类型枚举")
public enum LoginTypeEnum {

    /**
     * 验证码登陆
     */
    Captcha,
    /**
     * 密码登录
     */
    Password,
    /**
     * 微信登录
     */
    WeChat,
    /**
     * 支付宝登录
     */
    Alipay,
    /**
     * 抖音登录
     */
    Tiktok,
    /**
     * 油管登录
     */
    YouTube,
    /**
     * 电报登录
     */
    Telegram,
    /**
     * 苹果登录
     */
    Apple,
    /**
     * 谷歌登录
     */
    Google,
    /**
     * 脸书登录
     */
    Facebook,
    ;

}
