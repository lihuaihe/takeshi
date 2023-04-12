package com.takeshi.mybatisplus.typehandler;

import cn.hutool.core.net.Ipv4Util;
import cn.hutool.core.util.ObjUtil;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <p>Ipv4TypeHandler</p>
 * <p>注意！！ 使用typeHandler，必须开启autoResultMap映射注解</p>
 * <p>@TableName(autoResultMap = true)</p>
 * <p>@TableField(typeHandler = Ipv4TypeHandler.class)</p>
 * <p>根据ip地址(xxx.xxx.xxx.xxx)计算出long型的数据</p>
 * <p>存入数据库，从数据库中获取的时候也转成ip v4地址：xx.xx.xx.xx返回到实体类中</p>
 *
 * @author 七濑武【Nanase Takeshi】
 */
public class Ipv4TypeHandler extends BaseTypeHandler<String> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setLong(i, Ipv4Util.ipv4ToLong(parameter));
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return this.longToIpv4(rs.getLong(columnName));
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return this.longToIpv4(rs.getLong(columnIndex));
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return this.longToIpv4(cs.getLong(columnIndex));
    }

    private String longToIpv4(long value) {
        return ObjUtil.isNotNull(value) ? Ipv4Util.longToIpv4(value) : null;
    }

}
