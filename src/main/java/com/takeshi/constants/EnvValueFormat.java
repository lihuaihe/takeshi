package com.takeshi.constants;

import cn.hutool.core.convert.Convert;
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
     * 获取当前环境的值
     *
     * @return Object
     */
    default Object getEnvValue() {
        return switch (StaticConfig.active) {
            case "dev" -> this.getDevValue();
            case "test" -> this.getTestValue();
            case "prod" -> this.getProdValue();
            default -> null;
        };
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
