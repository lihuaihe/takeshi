package com.takeshi.config;

import cn.dev33.satoken.config.SaTokenConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.takeshi.jackson.SimpleJavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import javax.sql.DataSource;
import java.util.Locale;

/**
 * Bean配置<br/>
 * {@link EnableCaching} 启用 Spring 的注解驱动缓存管理功能<br/>
 * {@link EnableRetry} 启用 Spring 的重试功能
 *
 * @author 七濑武【Nanase Takeshi】
 */
@AutoConfiguration
@EnableCaching
@EnableRetry
@RequiredArgsConstructor
public class TakeshiConfig {

    private final SaTokenConfig saTokenConfig;

    /**
     * 跨域配置
     *
     * @return FilterRegistrationBean
     */
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addExposedHeader(saTokenConfig.getTokenName());
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 对接口配置跨域设置
        source.registerCorsConfiguration("/**", corsConfiguration);
        // 有多个filter时此处设置改CorsFilter的优先执行顺序
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

    /**
     * HTTP请求的“accept-language”标头中区域设置，国际化默认解析器，默认设置为英语
     *
     * @return LocaleResolver
     */
    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
        localeResolver.setDefaultLocale(Locale.US);
        return localeResolver;
    }

    /**
     * 统一配置，解决前后端交互大数值类型精度丢失的问题
     *
     * @param builder builder
     * @return ObjectMapper
     */
    @Bean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        return builder.createXmlMapper(false).build().registerModule(new SimpleJavaTimeModule());
    }

    /**
     * 配置cache缓存到redis
     *
     * @param factory factory
     * @return CacheManager
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        return TtlRedisCacheManager.defaultInstance(factory);
    }

    /**
     * 编程式事务管理器配置
     *
     * <pre>{@code
     * private final TransactionTemplate transactionTemplate;
     * transactionTemplate.execute(status -> {
     *      //返回true则事务成功，false事务失败
     *      return true;
     * });
     * }</pre>
     *
     * @param dataSource dataSource
     * @return PlatformTransactionManager
     */
    @Bean
    public PlatformTransactionManager platformTransactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

}