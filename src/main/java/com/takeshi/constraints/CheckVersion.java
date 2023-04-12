package com.takeshi.constraints;


import com.takeshi.constants.SysCode;
import com.takeshi.constraintvalidators.CheckVersionValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 版本号校验
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CheckVersionValidator.class)
@Documented
public @interface CheckVersion {

    /**
     * 提示信息,可以写死,可以填写国际化的key
     *
     * @return msg
     */
    String message() default SysCode.VERSION_EXAMPLE_STR;

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

}
