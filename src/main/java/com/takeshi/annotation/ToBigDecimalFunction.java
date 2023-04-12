package com.takeshi.annotation;

import java.math.BigDecimal;

/**
 * 对BigDecimal操作的函数式接口
 *
 * @author 七濑武【Nanase Takeshi】
 */
@FunctionalInterface
public interface ToBigDecimalFunction<T> {

    /**
     * 对BigDecimal操作的函数式接口
     *
     * @param value value
     * @return BigDecimal
     */
    BigDecimal applyAsBigDecimal(T value);

}
