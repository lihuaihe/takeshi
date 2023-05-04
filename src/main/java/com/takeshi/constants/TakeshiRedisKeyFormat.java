package com.takeshi.constants;

import cn.hutool.core.util.StrUtil;
import com.takeshi.config.StaticConfig;

/**
 * 格式化方法给key加对应前缀
 *
 * @author 七濑武【Nanase Takeshi】
 */
public interface TakeshiRedisKeyFormat {

    /**
     * 获取key值，格式化文本, {} 表示占位符
     * <br/>
     * 示例："this is {} for {}"
     *
     * @return key
     */
    String getKey();

    /**
     * 添加模块名称前缀并格式化
     *
     * @param params 参数值
     * @return 格式化后的文本
     */
    default String moduleKey(Object... params) {
        return StaticConfig.applicationName.concat(StrUtil.addPrefixIfNot(StrUtil.format(this.getKey(), params), StrUtil.COLON));
    }

    /**
     * 添加项目名称前缀并格式化
     *
     * @param params 参数值
     * @return 格式化后的文本
     */
    default String projectKey(Object... params) {
        return StaticConfig.takeshiProperties.getProjectName().concat(StrUtil.addPrefixIfNot(StrUtil.format(this.getKey(), params), StrUtil.COLON));
    }

}
