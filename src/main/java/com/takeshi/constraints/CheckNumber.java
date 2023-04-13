package com.takeshi.constraints;


import com.takeshi.constants.SysCode;
import com.takeshi.constraintvalidators.CheckNumberValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 接口参数校验固定值，null值也是有效的
 * <p>
 * 参数上使用注解,校验参数值是否在当前数组中 --- @CheckNumber({1, 2})
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CheckNumberValidator.class)
@Documented
public @interface CheckNumber {

    /**
     * 提示信息,可以写死,可以填写国际化的key
     *
     * @return msg
     */
    String message() default SysCode.VALIDATION_LIST;

    /**
     * 下面这两个属性必须添加
     *
     * @return groups
     */
    Class<?>[] groups() default {};

    /**
     * payload
     *
     * @return payload
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * 要校验的值是否在该数组中
     *
     * @return 数组
     */
    long[] value() default {1, 2};

}
