package com.takeshi.constants;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.takeshi.config.StaticConfig;

/**
 * 自动根据当前不同环境进行取值
 *
 * @author 七濑武【Nanase Takeshi】
 */
public interface EnvValueFormat {

    /**
     * 获取开发环境的值
     *
     * @return Object
     */
    Object getDevValue();

    /**
     * 获取测试环境的值
     *
     * @return Object
     */
    Object getTestValue();

    /**
     * 获取正式环境的值
     *
     * @return Object
     */
    Object getProdValue();

    /**
     * 获取当前环境的值，如果是String则可以格式化文本
     *
     * @param params params
     * @return Object
     */
    default Object getEnvValue(Object... params) {
        Object envValue = switch (StaticConfig.active) {
            case "dev" -> this.getDevValue();
            case "test" -> this.getTestValue();
            case "prod" -> this.getProdValue();
            default -> null;
        };
        if (envValue instanceof CharSequence template && StrUtil.isNotBlank(template)) {
            return StrUtil.format(template, params);
        } else {
            return envValue;
        }
    }

    /**
     * 根据指定转化的类，获取当前环境的值
     *
     * @param beanClass beanClass
     * @param <T>       T
     * @return T
     */
    default <T> T getEnvValue(Class<T> beanClass) {
        Object envValue = switch (StaticConfig.active) {
            case "dev" -> this.getDevValue();
            case "test" -> this.getTestValue();
            case "prod" -> this.getProdValue();
            default -> null;
        };
        return Convert.convert(beanClass, envValue);
    }

}
