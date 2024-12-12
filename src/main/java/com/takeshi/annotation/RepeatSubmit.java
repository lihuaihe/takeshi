package com.takeshi.annotation;

import com.takeshi.constants.TakeshiCode;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.validation.annotation.Validated;

import java.lang.annotation.*;

/**
 * 自定义注解防止重复提交
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Validated
public @interface RepeatSubmit {

    // ---------begin 此部分可对某个接口进行防重复提交限制，密等判断---------

    /**
     * <p style="color:yellow;">若要使用防重功能，此值需要设置大于0</p>
     * 间隔时间，小于此时间视为重复提交，单位：毫秒
     * <br/>
     * 设置0则不开启重复提交校验
     *
     * @return 时间
     */
    @PositiveOrZero
    long rateInterval() default 0;

    /**
     * 重复提交 排除的字段名称<br/>
     * 这些参数字段将不会参与重复提交校验，例如："requestTime"
     *
     * @return ignoredFieldNames
     */
    String[] exclusionFieldName() default {};

    /**
     * 重复提交 提示语信息
     *
     * @return msg
     */
    String msg() default TakeshiCode.REPEAT_SUBMIT_STR;

    // ---------end 此部分可对某个接口进行防重复提交限制，密等判断---------

    // ---------begin 此部分可对某个接口进行请求速率限制---------

    /**
     * 速率，多少毫秒内允许多少次请求
     *
     * @return int
     */
    @Positive
    long ipRate() default 10;

    /**
     * 速率时间间隔，设置0则不对接口IP限制，单位：毫秒
     *
     * @return int
     */
    @PositiveOrZero
    long ipRateInterval() default 0;

    // ---------end 此部分可对某个接口进行请求速率限制---------

}
