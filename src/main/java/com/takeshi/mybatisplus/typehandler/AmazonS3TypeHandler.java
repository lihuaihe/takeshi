package com.takeshi.mybatisplus.typehandler;

import cn.hutool.core.net.url.UrlQuery;
import cn.hutool.core.util.StrUtil;
import com.takeshi.component.RedisComponent;
import com.takeshi.config.StaticConfig;
import com.takeshi.enums.TakeshiRedisKeyEnum;
import com.takeshi.util.AmazonS3Util;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.redisson.api.RLock;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * <p>AmazonS3TypeHandler</p>
 * <p>注意！！ 使用typeHandler，必须开启autoResultMap映射注解</p>
 * <p>@TableName(autoResultMap = true)</p>
 * <p>@TableField(typeHandler = AmazonS3TypeHandler.class)</p>
 * <p>S3的key存入数据库，不处理，从数据库取出来时通过key获取临时URL返回</p>
 *
 * @author 七濑武【Nanase Takeshi】
 */
public class AmazonS3TypeHandler extends BaseTypeHandler<String> {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmssX");

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType)
            throws SQLException {
        ps.setString(i, parameter);
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return this.getUrl(rs.getString(columnName));
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return this.getUrl(rs.getString(columnIndex));
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return this.getUrl(cs.getString(columnIndex));
    }

    /**
     * 通过S3对象的键获取预签名URL
     *
     * @param key S3对象的键
     * @return 临时URL
     */
    public String getUrl(String key) {
        RedisComponent redisComponent = StaticConfig.redisComponent;
        String redisKey = TakeshiRedisKeyEnum.S3_PRESIGNED_URL.projectKey(key);
        String url = redisComponent.get(redisKey);
        if (StrUtil.isBlank(url)) {
            RLock lock = redisComponent.getLock(TakeshiRedisKeyEnum.LOCK_S3_PRESIGNED_URL.projectKey(key));
            try {
                if (lock.tryLock(10, 30, TimeUnit.SECONDS)) {
                    url = redisComponent.get(redisKey);
                    if (StrUtil.isBlank(url)) {
                        URL presignedUrl = AmazonS3Util.getPresignedUrl(key);
                        url = presignedUrl.toString();
                        CharSequence date = UrlQuery.of(presignedUrl.getQuery(), StandardCharsets.UTF_8).get("X-Amz-Date");
                        redisComponent.saveToDateTime(redisKey, url, Instant.from(formatter.parse(date)).toEpochMilli());
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
        }
        return url;
    }

}
