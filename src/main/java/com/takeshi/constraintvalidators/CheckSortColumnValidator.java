package com.takeshi.constraintvalidators;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlInjectionUtils;
import com.takeshi.constraints.CheckSortColumn;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * CheckSortColumnValidator
 *
 * @author 七濑武【Nanase Takeshi】
 */
public class CheckSortColumnValidator implements ConstraintValidator<CheckSortColumn, String> {

    @Override
    public boolean isValid(String str, ConstraintValidatorContext context) {
        return StrUtil.isBlank(str) || !SqlInjectionUtils.check(str);
    }

}
