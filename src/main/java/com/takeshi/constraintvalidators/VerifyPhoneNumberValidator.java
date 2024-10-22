package com.takeshi.constraintvalidators;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.takeshi.constraints.VerifyPhoneNumber;
import com.takeshi.util.GlobalPhoneNumberUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 校验全球号码
 *
 * @author 七濑武【Nanase Takeshi】
 */
public class VerifyPhoneNumberValidator implements ConstraintValidator<VerifyPhoneNumber, String> {

    /**
     * 默认国家代码，例如：CN
     */
    private String defaultRegion;

    @Override
    public void initialize(VerifyPhoneNumber constraintAnnotation) {
        defaultRegion = StrUtil.blankToDefault(constraintAnnotation.defaultRegion(), null);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (ObjUtil.isNull(value)) {
            return true;
        }
        try {
            return GlobalPhoneNumberUtil.isValidNumberWithRegion(value, defaultRegion);
        } catch (Exception e) {
            return false;
        }
    }

}
