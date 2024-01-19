package com.takeshi.mybatisplus;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.extension.handlers.GsonTypeHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.takeshi.mybatisplus.typehandler.TakeshiInstantTypeHandler;
import com.takeshi.util.GsonUtil;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * MybatisPlusConfig
 *
 * @author 七濑武【Nanase Takeshi】
 */
@AutoConfiguration
public class MybatisPlusConfig {

    /**
     * CiphertextTypeHandler的typeHandler路径
     */
    public static final String MAPPING_CIPHERTEXT_TYPE_HANDLER = "typeHandler=com.takeshi.mybatisplus.typehandler.AesCiphertextTypeHandler";

    /**
     * GeoPointTypeHandler的typeHandler路径
     */
    public static final String MAPPING_GEO_POINT_TYPE_HANDLER = "typeHandler=com.takeshi.mybatisplus.typehandler.GeoPointTypeHandler";

    /**
     * Ipv4TypeHandler的typeHandler路径
     */
    public static final String MAPPING_IPV4_TYPE_HANDLER = "typeHandler=com.takeshi.mybatisplus.typehandler.Ipv4TypeHandler";

    /**
     * PasswordTypeHandler的typeHandler路径
     */
    public static final String MAPPING_PASSWORD_TYPE_HANDLER = "typeHandler=com.takeshi.mybatisplus.typehandler.PasswordTypeHandler";

    /**
     * ZonedDateTimeTypeHandler的typeHandler路径
     */
    public static final String MAPPING_ZONED_DATE_TIME_TYPE_HANDLER = "typeHandler=com.takeshi.mybatisplus.typehandler.TakeshiZonedDateTimeTypeHandler";

    /**
     * AmazonS3TypeHandler的typeHandler路径
     */
    public static final String MAPPING_AMAZON_S3_TYPE_HANDLER = "typeHandler=com.takeshi.mybatisplus.typehandler.AmazonS3TypeHandler";

    /**
     * 新的分页插件,一缓和二缓遵循mybatis的规则,需要设置 MybatisConfiguration useDeprecatedExecutor = false 避免缓存出现问题
     *
     * @return MybatisPlusInterceptor
     */
    @Bean
    @ConditionalOnMissingBean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        // 设置GsonTypeHandler中的GSON
        GsonTypeHandler.setGson(GsonUtil.gson());
        return interceptor;
    }

    /**
     * mybatis Plus 配置定制器
     *
     * @return ConfigurationCustomizer
     */
    @Bean
    @ConditionalOnMissingBean
    public ConfigurationCustomizer mybatisPlusConfigurationCustomizer() {
        return configuration -> {
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            typeHandlerRegistry.register(TakeshiInstantTypeHandler.class);
        };
    }
}
