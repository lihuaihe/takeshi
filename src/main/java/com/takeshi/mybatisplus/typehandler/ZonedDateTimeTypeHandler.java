package com.takeshi.mybatisplus.typehandler;

import cn.hutool.core.util.StrUtil;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;

/**
 * <p>ZonedDateTimeTypeHandler</p>
 * <p>注意！！ 使用typeHandler，必须开启autoResultMap映射注解</p>
 * <p>@TableName(autoResultMap = true)</p>
 * <p>@TableField(typeHandler = ZonedDateTimeTypeHandler.class)</p>
 * <p>将java中的ZonedDateTime转成字符串形式</p>
 * <p>2022-04-18T18:01:17.429+08:00[Asia/Shanghai]</p>
 * <p>存入数据库，从数据库中获取的时候也转成ZonedDateTime返回到实体类中</p>
 *
 * @author 七濑武【Nanase Takeshi】
 */
public class ZonedDateTimeTypeHandler extends BaseTypeHandler<ZonedDateTime> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, ZonedDateTime parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setString(i, parameter.toString());
    }

    @Override
    public ZonedDateTime getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return this.parse(rs.getString(columnName));
    }

    @Override
    public ZonedDateTime getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return this.parse(rs.getString(columnIndex));
    }

    @Override
    public ZonedDateTime getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return this.parse(cs.getString(columnIndex));
    }

    private ZonedDateTime parse(String value) {
        return StrUtil.isNotBlank(value) ? ZonedDateTime.parse(value) : null;
    }

}
