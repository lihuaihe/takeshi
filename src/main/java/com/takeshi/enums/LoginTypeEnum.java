package com.takeshi.enums;

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
    CAPTCHA,
    /**
     * 密码登录
     */
    PASSWORD,
    /**
     * 微信登录
     */
    WE_CHAT,
    /**
     * 支付宝登录
     */
    ALIPAY,
    /**
     * 抖音登录
     */
    TIKTOK,
    /**
     * 油管登录
     */
    YOU_TUBE,
    /**
     * 电报登录
     */
    TELEGRAM,
    /**
     * 苹果登录
     */
    APPLE,
    /**
     * 谷歌登录
     */
    GOOGLE,
    /**
     * 脸书登录
     */
    FACEBOOK,
    /**
     * 注册后自动登录
     */
    REGISTER_AFTER_LOGIN,
    /**
     * 其它
     */
    OTHER,
    ;

}
