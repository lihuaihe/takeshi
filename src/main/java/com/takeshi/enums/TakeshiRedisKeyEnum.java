package com.takeshi.enums;

import cn.hutool.core.util.StrUtil;
import com.takeshi.config.StaticConfig;

/**
 * 存储一些redis使用的key，可以调用格式化方法给key加对应前缀
 *
 * @author 七濑武【Nanase Takeshi】
 */
public enum TakeshiRedisKeyEnum {

    /**
     * 私钥
     */
    PRIVATE_KEY_BASE64("privateKeyBase64"),
    /**
     * 公钥
     */
    PUBLIC_KEY_BASE64("publicKeyBase64"),
    /**
     * SaTokenConfig类中查询账号状态的间隔时间
     */
    BREAK_TIME("breakTime:{}"),
    /**
     * 活跃用户记录，当天凌晨失效
     * {活跃日期}{用户ID}
     */
    ACTIVE_USER_RECORD("active:{}:{}");

    TakeshiRedisKeyEnum(String key) {
        this.key = key;
    }

    private final String key;

    /**
     * 添加模块名称前缀并格式化
     *
     * @param params 参数值
     * @return 格式化后的文本
     */
    public String formatModule(Object... params) {
        return StaticConfig.applicationName.concat(StrUtil.addPrefixIfNot(StrUtil.format(this.key, params), StrUtil.COLON));
    }

    /**
     * 添加项目名称前缀并格式化
     *
     * @param params 参数值
     * @return 格式化后的文本
     */
    public String formatProject(Object... params) {
        return StaticConfig.takeshiProperties.getProjectName().concat(StrUtil.addPrefixIfNot(StrUtil.format(this.key, params), StrUtil.COLON));
    }

}
