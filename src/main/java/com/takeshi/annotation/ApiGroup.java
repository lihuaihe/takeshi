package com.takeshi.annotation;

import java.lang.annotation.*;

/**
 * <pre>{@code
 * // 自定义swagger接口上的接口分组注解，全部接口都归于自动生成的default组，可以使用ApiGroup添加新组
 *  @ApiGroup({"1.0", "1.1"})
 * }</pre>
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiGroup {

    /**
     * 接口分组名称数组
     *
     * @return 分组名称数组
     */
    String[] value();

}
