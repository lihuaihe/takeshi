package com.takeshi.config.properties;

import com.takeshi.constants.TakeshiConstants;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * 自定义额外属性值
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Data
@AutoConfiguration(value = "takeshiProperties")
@ConfigurationProperties(prefix = "takeshi")
@Validated
public class TakeshiProperties {

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 是否开启移动端请求工具限制
     */
    private boolean appPlatform = false;

    /**
     * 是否Controller方法参数绑定错误时错误信息包含字段名
     */
    private boolean includeErrorFieldName = true;

    /**
     * 优雅关闭时，定时任务关闭的最大超时时间（单位：秒）
     */
    @Positive
    private long maxExecutorCloseTimeout = 30;

    /**
     * 默认会排除{@link TakeshiConstants#EXCLUDE_URL}<br/>
     * 需要额外排除的URL，排除的URL将不会进入TakeshiFilter和TakeshiInterceptor和TakeshiSaTokenConfig逻辑
     */
    private String[] excludeUrl;

    /**
     * AES加密使用的key，长度必须为16位，默认的aesKey对项目名称+环境进行加密后截取前16位得到新的aesKey
     */
    @Size(min = 16, max = 16)
    private String aesKey;

    /**
     * 是否开启IP黑名单，超过请求次数则将IP加入黑名单内24小时
     */
    private boolean openIpBlacklist = false;

    /**
     * 是否开启打印请求参数日志
     */
    private boolean enableRequestParamLog = true;

    /**
     * 是否开启打印响应数据日志
     */
    private boolean enableResponseDataLog = true;

    /**
     * redisson缓存配置，例如：使用classpath:redisson-cache.yaml，也可以使用json文件的路径，内容：{@link org.redisson.spring.cache.CacheConfig}
     */
    private String redissonCacheFilePath;

}
