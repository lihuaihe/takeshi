package com.takeshi.annotation;

import java.math.BigDecimal;

/**
 * 对BigDecimal操作的函数式接口
 *
 * @author 七濑武【Nanase Takeshi】
 * @date 2020/12/31 16:26
 */
@FunctionalInterface
public interface ToBigDecimalFunction<T> {

    /**
     * 对BigDecimal操作的函数式接口
     *
     * @param value
     * @return
     */
    BigDecimal applyAsBigDecimal(T value);

}
