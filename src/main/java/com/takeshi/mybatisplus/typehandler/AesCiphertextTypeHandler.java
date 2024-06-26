package com.takeshi.mybatisplus.typehandler;

import cn.hutool.core.util.StrUtil;
import com.takeshi.config.StaticConfig;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <p>将明文使用AES算法加密存入数据库，从数据库取出来时解密</p>
 * <p>注意！！ 使用typeHandler，必须开启autoResultMap映射注解</p>
 * <p>@TableName(autoResultMap = true)</p>
 * <p>@TableField(typeHandler = AesCiphertextTypeHandler.class)</p>
 *
 * @author 七濑武【Nanase Takeshi】
 */
public class AesCiphertextTypeHandler extends BaseTypeHandler<String> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setString(i, StaticConfig.aes.encryptBase64(parameter));
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return this.decryptStr(rs.getString(columnName));
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return this.decryptStr(rs.getString(columnIndex));
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return this.decryptStr(cs.getString(columnIndex));
    }

    private String decryptStr(String value) {
        return StrUtil.isNotBlank(value) ? StaticConfig.aes.decryptStr(value) : null;
    }

}
