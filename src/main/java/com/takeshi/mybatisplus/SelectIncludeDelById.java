package com.takeshi.mybatisplus;

import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.scripting.defaults.RawSqlSource;

/**
 * 创建自定义的sql注入器需要的sql
 * SelectIncludeDelById
 *
 * @author 七濑武【Nanase Takeshi】
 * @date 2021/09/15 16:01
 */
public class SelectIncludeDelById extends AbstractMethod {

    /**
     * @date 3.5.0
     * @see AbstractMethod#AbstractMethod(String)
     */
    public SelectIncludeDelById() {
        super("selectIncludeDelById");
    }

    /**
     * @param methodName 方法名
     * @date 3.5.0
     */
    public SelectIncludeDelById(String methodName) {
        super(methodName);
    }

    /**
     * 注入自定义 MappedStatement
     *
     * @param mapperClass mapper 接口
     * @param modelClass  mapper 泛型
     * @param tableInfo   数据库表反射信息
     * @return MappedStatement
     */
    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        SqlMethod sqlMethod = SqlMethod.SELECT_BY_ID;
        SqlSource sqlSource = new RawSqlSource(configuration, String.format(sqlMethod.getSql(),
                sqlSelectColumns(tableInfo, false),
                tableInfo.getTableName(), tableInfo.getKeyColumn(), tableInfo.getKeyProperty(),
                EMPTY), Object.class);
        return this.addSelectMappedStatementForTable(mapperClass, methodName, sqlSource, tableInfo);
    }

}
