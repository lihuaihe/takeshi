package com.takeshi.constraintvalidators;

import cn.hutool.core.util.StrUtil;
import com.takeshi.constraints.CheckString;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 接口参数校验固定值
 *
 * @author 七濑武【Nanase Takeshi】
 */
public class CheckStringValidator implements ConstraintValidator<CheckString, String> {

    private final Set<String> values = new HashSet<>();

    @Override
    public void initialize(CheckString constraintAnnotation) {
        values.clear();
        values.addAll(Arrays.asList(constraintAnnotation.value()));
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return StrUtil.isBlank(value) || values.stream().anyMatch(item -> StrUtil.equalsIgnoreCase(item, value));
    }

}
