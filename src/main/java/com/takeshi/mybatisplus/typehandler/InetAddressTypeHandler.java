package com.takeshi.mybatisplus.typehandler;

import cn.hutool.core.util.ArrayUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <p>InetAddressTypeHandler</p>
 * <p>注意！！ 使用typeHandler，必须开启autoResultMap映射注解</p>
 * <p>@TableName(autoResultMap = true)</p>
 * <p>@TableField(typeHandler = InetAddressTypeHandler.class)</p>
 * <p>根据ipv4/ipv6地址得出字节数组</p>
 * <p>存入数据库，从数据库中获取的时候也转成ipv4/ipv6地址返回到实体类中</p>
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Slf4j
public class InetAddressTypeHandler extends BaseTypeHandler<String> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType)
            throws SQLException {
        try {
            InetAddress inetAddress = InetAddress.getByName(parameter);
            ps.setBytes(i, inetAddress.getAddress());
        } catch (UnknownHostException e) {
            log.error("Ipv4TypeHandler.setNonNullParameter --> e: ", e);
        }
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return this.getIpAddress(rs.getBytes(columnName));
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return this.getIpAddress(rs.getBytes(columnIndex));
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return this.getIpAddress(cs.getBytes(columnIndex));
    }

    private String getIpAddress(byte[] value) {
        if (ArrayUtil.isNotEmpty(value)) {
            try {
                return InetAddress.getByAddress(value).getHostAddress();
            } catch (UnknownHostException e) {
                log.error("Ipv4TypeHandler.getIpAddress --> e: ", e);
            }
        }
        return null;
    }

}
