package com.takeshi.mybatisplus.typehandler;

import cn.hutool.core.util.StrUtil;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

/**
 * <p>LocaleTypeHandler</p>
 * <p>注意！！ 使用typeHandler，必须开启autoResultMap映射注解</p>
 * <p>@TableName(autoResultMap = true)</p>
 * <p>@TableField(typeHandler = LocaleTypeHandler.class)</p>
 * <p>转换{@link java.util.Locale}，存入数据库使用zh_CN存入，而不是zh-CN存入</p>
 *
 * @author 七濑武【Nanase Takeshi】
 */
public class LocaleTypeHandler extends BaseTypeHandler<Locale> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Locale parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setString(i, parameter.toString());
    }

    @Override
    public Locale getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return this.strToLocale(rs.getString(columnName));
    }

    @Override
    public Locale getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return this.strToLocale(rs.getString(columnIndex));
    }

    @Override
    public Locale getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return this.strToLocale(cs.getString(columnIndex));
    }

    private Locale strToLocale(String str) {
        if (StrUtil.isBlankIfStr(str)) {
            return null;
        }
        return Locale.forLanguageTag(str.replace(StrUtil.UNDERLINE, StrUtil.DASHED));
    }

}
