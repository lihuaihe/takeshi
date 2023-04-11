package com.takeshi.constraintvalidators;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjUtil;
import com.takeshi.constraints.NumberDigits;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.math.BigDecimal;

/**
 * 校验数字整数位和小数位的位数
 *
 * @author 七濑武【Nanase Takeshi】
 */
public class NumberDigitsValidator implements ConstraintValidator<NumberDigits, Number> {

    private int maxInteger = 0;
    private int minInteger = 0;
    private int maxFraction = 0;
    private int minFraction = 0;

    @Override
    public void initialize(NumberDigits constraintAnnotation) {
        maxInteger = constraintAnnotation.maxInteger();
        minInteger = constraintAnnotation.minInteger();
        maxFraction = constraintAnnotation.maxFraction();
        minFraction = constraintAnnotation.minFraction();
    }

    @Override
    public boolean isValid(Number value, ConstraintValidatorContext context) {
        if (ObjUtil.isNull(value)) {
            return true;
        }
        BigDecimal bigDecimal = NumberUtil.toBigDecimal(value);
        int integerPartLength = bigDecimal.precision() - bigDecimal.scale();
        int fractionPartLength = Math.max(bigDecimal.scale(), 0);
        boolean integerValid = minInteger <= integerPartLength && integerPartLength <= maxInteger;
        boolean fractionValid = minFraction <= fractionPartLength && fractionPartLength <= maxFraction;
        return integerValid && fractionValid;
    }

}
