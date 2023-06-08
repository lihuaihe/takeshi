package com.takeshi.constants;

import java.io.Serializable;

/**
 * TakeshiEnum
 *
 * @author 七濑武【Nanase Takeshi】
 */
public interface TakeshiEnum<T extends Serializable> {

    /**
     * 实际使用的值
     *
     * @return T
     */
    T getValue();

}
