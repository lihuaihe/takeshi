package com.takeshi.annotation;

import java.lang.annotation.*;

/**
 * ApiSupport
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiSupport {

    /**
     * 作者名
     *
     * @return String
     */
    String author() default "";

    /**
     * 作者名数组
     *
     * @return String[]
     */
    String[] authors() default {};

}
