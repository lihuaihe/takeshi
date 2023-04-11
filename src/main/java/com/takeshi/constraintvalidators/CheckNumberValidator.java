package com.takeshi.constraintvalidators;

import cn.hutool.core.util.ObjUtil;
import com.takeshi.constraints.CheckNumber;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.HashSet;
import java.util.Set;

/**
 * 接口参数校验固定值
 *
 * @author 七濑武【Nanase Takeshi】
 */
public class CheckNumberValidator implements ConstraintValidator<CheckNumber, Number> {

    private final Set<Number> values = new HashSet<>();

    @Override
    public void initialize(CheckNumber constraintAnnotation) {
        values.clear();
        for (long value : constraintAnnotation.value()) {
            values.add(value);
        }
    }

    @Override
    public boolean isValid(Number value, ConstraintValidatorContext context) {
        return ObjUtil.isNull(value) || values.contains(value.longValue());
    }

}
