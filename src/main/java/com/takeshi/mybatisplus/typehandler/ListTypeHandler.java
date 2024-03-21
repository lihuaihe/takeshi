package com.takeshi.mybatisplus.typehandler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * <p>ListTypeHandler</p>
 * <p>注意！！ 使用typeHandler，必须开启autoResultMap映射注解</p>
 * <p>@TableName(autoResultMap = true)</p>
 * <p>@TableField(typeHandler = ListTypeHandler.class)</p>
 * <p>将List转成英文逗号分隔的字符串存入数据库，并且从数据库取出时，将英文逗号分隔的字符串转成List</p>
 *
 * @author 七濑武【Nanase Takeshi】
 */
public abstract class ListTypeHandler<T> extends BaseTypeHandler<List<T>> {

    /**
     * 具体类型，由子类提供
     *
     * @return 具体类型
     */
    protected abstract Class<T> specificType();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<T> parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, CollUtil.join(parameter, StrUtil.COMMA));
    }

    @Override
    public List<T> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return this.stringToList(rs.getString(columnName));
    }

    @Override
    public List<T> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return this.stringToList(rs.getString(columnIndex));
    }

    @Override
    public List<T> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return this.stringToList(cs.getString(columnIndex));
    }

    private List<T> stringToList(String str) {
        return StrUtil.split(str, StrUtil.C_COMMA, 0, false, string -> Convert.convert(this.specificType(), string));
    }

}
