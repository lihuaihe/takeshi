package com.takeshi.constraints;


import com.takeshi.constants.TakeshiCode;
import com.takeshi.constraintvalidators.VerifyVersionValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 版本号格式校验
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = VerifyVersionValidator.class)
@Documented
public @interface VerifyVersion {

    /**
     * 提示信息,可以写死,可以填写国际化的key
     *
     * @return msg
     */
    String message() default TakeshiCode.VERSION_EXAMPLE_STR;

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
