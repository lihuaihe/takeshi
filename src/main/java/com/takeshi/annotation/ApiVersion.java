package com.takeshi.annotation;

import java.lang.annotation.*;

/**
 * //@ApiVersion(ApiVersion.Version.v_1_1_0)
 * 自定义swagger接口上的版本分组注解
 * 需要新分组时在下面的Version枚举类中新增一个常量即可
 *
 * @author 七濑武【Nanase Takeshi】
 * @date 2021/4/16 16:50
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiVersion {

    Version[] value();

    enum Version {
        /**
         * 分组名称
         */
        //        v_1_1_0("1.1.0"),
        DEFAULT();

        private final String display;

        Version() {
            this.display = "default";
        }

        Version(String display) {
            this.display = display;
        }

        public String getDisplay() {
            return display;
        }
    }
}
