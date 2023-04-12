package com.takeshi.constraintvalidators;

import cn.hutool.core.util.ReUtil;
import com.takeshi.constraints.CheckVersion;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * CheckSortColumnValidator
 *
 * @author 七濑武【Nanase Takeshi】
 */
public class CheckVersionValidator implements ConstraintValidator<CheckVersion, String> {

    @Override
    public boolean isValid(String str, ConstraintValidatorContext context) {
        return ReUtil.isMatch("^\\d+(?:\\.\\d+){2}$", str);
    }

}
