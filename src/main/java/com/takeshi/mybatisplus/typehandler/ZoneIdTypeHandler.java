package com.takeshi.mybatisplus.typehandler;

import cn.hutool.core.util.ObjUtil;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;

/**
 * <p>ZoneIdTypeHandler</p>
 * <p>无需在配置typeHandler = ZoneIdTypeHandler.class，已注册到mybatis中，自动生效</p>
 *
 * @author 七濑武【Nanase Takeshi】
 */
public class ZoneIdTypeHandler extends BaseTypeHandler<ZoneId> {

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, ZoneId zoneId, JdbcType jdbcType) throws SQLException {
        preparedStatement.setString(i, ObjUtil.isNull(zoneId) ? null : zoneId.toString());
    }

    @Override
    public ZoneId getNullableResult(ResultSet resultSet, String s) throws SQLException {
        return ZoneId.of(resultSet.getString(s));
    }

    @Override
    public ZoneId getNullableResult(ResultSet resultSet, int i) throws SQLException {
        return ZoneId.of(resultSet.getString(i));
    }

    @Override
    public ZoneId getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        return ZoneId.of(callableStatement.getString(i));
    }

}
