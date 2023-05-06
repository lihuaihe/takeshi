package com.takeshi.constraintvalidators;

import cn.hutool.core.util.ReUtil;
import com.takeshi.constraints.VerifyVersion;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * VerifySortColumnValidator
 *
 * @author 七濑武【Nanase Takeshi】
 */
public class VerifyVersionValidator implements ConstraintValidator<VerifyVersion, String> {

    @Override
    public boolean isValid(String str, ConstraintValidatorContext context) {
        return ReUtil.isMatch("^\\d+(?:\\.\\d+){2}$", str);
    }

}
