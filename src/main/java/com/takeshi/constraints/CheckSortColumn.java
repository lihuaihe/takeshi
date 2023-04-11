package com.takeshi.constraints;

import com.takeshi.constants.SysCode;
import com.takeshi.constraintvalidators.CheckSortColumnValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 数据库排序字段校验
 *
 * @author 七濑武【Nanase Takeshi】
 * @date 2021/09/02 14:01
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CheckSortColumnValidator.class)
@Documented
public @interface CheckSortColumn {

    /**
     * 提示信息,可以写死,可以填写国际化的key
     */
    String message() default SysCode.PARAM_ERROR;

    /**
     * 下面这两个属性必须添加
     */
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
