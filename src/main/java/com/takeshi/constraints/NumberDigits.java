package com.takeshi.constraints;


import com.takeshi.constants.TakeshiCode;
import com.takeshi.constraintvalidators.NumberDigitsValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 校验数字整数位和小数位的位数，null值也是有效的
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NumberDigitsValidator.class)
@Documented
public @interface NumberDigits {

    /**
     * 提示信息,可以写死,可以填写国际化的key
     *
     * @return msg
     */
    String message() default TakeshiCode.NUMBER_DIGITS_STR;

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
     * @return 可接受的最小整数位数
     */
    int minInteger() default 0;

    /**
     * @return 可接受的最大整数位数
     */
    int maxInteger();

    /**
     * @return 可接受的最小小数位数
     */
    int minFraction() default 0;

    /**
     * @return 可接受的最大小数位数
     */
    int maxFraction() default 0;

}
