package com.takeshi.mybatisplus;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
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
    public static final String MAPPING_CIPHERTEXT_TYPE_HANDLER = "typeHandler=com.takeshi.mybatisplus.typehandler.CiphertextTypeHandler";

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
    public static final String MAPPING_ZONED_DATE_TIME_TYPE_HANDLER = "typeHandler=com.takeshi.mybatisplus.typehandler.ZonedDateTimeTypeHandler";

    /**
     * 新的分页插件,一缓和二缓遵循mybatis的规则,需要设置 MybatisConfiguration useDeprecatedExecutor = false 避免缓存出现问题
     *
     * @return MybatisPlusInterceptor
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

}
