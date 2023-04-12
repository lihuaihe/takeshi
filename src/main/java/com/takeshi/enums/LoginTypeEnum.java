package com.takeshi.enums;

import com.baomidou.mybatisplus.annotation.IEnum;

/**
 * LoginTypeEnum
 *
 * @author 七濑武【Nanase Takeshi】
 */
public enum LoginTypeEnum implements IEnum<String> {

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
    Facebook;

    /**
     * 枚举数据库存储值
     */
    @Override
    public String getValue() {
        return this.name();
    }

}
