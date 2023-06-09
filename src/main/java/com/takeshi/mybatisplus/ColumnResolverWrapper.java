package com.takeshi.mybatisplus;

import com.baomidou.mybatisplus.core.conditions.AbstractLambdaWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;

/**
 * 通过lambda获取字段名
 *
 * @author 七濑武【Nanase Takeshi】
 */
public class ColumnResolverWrapper<T> extends AbstractLambdaWrapper<T, ColumnResolverWrapper<T>> {

    /**
     * 构造函数
     *
     * @param entityClass entityClass
     */
    public ColumnResolverWrapper(Class<T> entityClass) {
        super.setEntityClass(entityClass);
        super.initNeed();
    }

    /**
     * 子类返回一个自己的新对象
     */
    @Override
    protected ColumnResolverWrapper<T> instance() {
        return new ColumnResolverWrapper<>(getEntityClass());
    }

    /**
     * 通过lambda获取字段名
     *
     * @param column 字段名
     * @return 属性名
     */
    @Override
    public String columnToString(SFunction<T, ?> column) {
        return super.columnToString(column);
    }

}

