package com.takeshi.config;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.cfg.PackageVersion;
import com.fasterxml.jackson.databind.deser.std.NumberDeserializers;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.takeshi.config.satoken.TakeshiInterceptor;
import com.takeshi.config.satoken.TakeshiSaTokenConfig;
import lombok.RequiredArgsConstructor;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.Ordered;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * Bean配置<br/>
 * {@link org.springframework.cache.annotation.EnableCaching} 启用 Spring 的注解驱动缓存管理功能<br/>
 * {@link EnableRetry} 启用 Spring 的重试功能<br/>
 * {@link EnableScheduling} 启用定时任务功能
 *
 * @author 七濑武【Nanase Takeshi】
 */
@AutoConfiguration(value = "takeshiConfig")
@EnableCaching
@EnableRetry
@EnableScheduling
@RequiredArgsConstructor
public class TakeshiConfig {

    private final SaTokenConfig saTokenConfig;

    /**
     * 跨域配置
     *
     * @return FilterRegistrationBean
     */
    @Bean
    @ConditionalOnMissingBean
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
    @ConditionalOnMissingBean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
        localeResolver.setDefaultLocale(Locale.US);
        return localeResolver;
    }

    /**
     * 消息的参数化和国际化
     *
     * @param basename basename
     * @return MessageSource
     */
    @Bean
    @ConditionalOnMissingBean
    public MessageSource messageSource(@Value("${spring.messages.basename:}") String basename) {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        if (StrUtil.isNotBlank(basename)) {
            messageSource.addBasenames(StrUtil.splitToArray(basename, StrUtil.C_COMMA));
        }
        messageSource.addBasenames("takeshi-i18n/messages");
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        return messageSource;
    }

    /**
     * 统一配置，解决前后端交互大数值类型精度丢失的问题
     *
     * @param builder builder
     * @return ObjectMapper
     */
    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        SimpleModule simpleModule = new SimpleModule("NumberToString", PackageVersion.VERSION)
                .addSerializer(Long.class, ToStringSerializer.instance)
                .addSerializer(Long.TYPE, ToStringSerializer.instance)
                .addSerializer(Double.class, ToStringSerializer.instance)
                .addSerializer(Double.TYPE, ToStringSerializer.instance)
                .addSerializer(Float.class, ToStringSerializer.instance)
                .addSerializer(Float.TYPE, ToStringSerializer.instance)
                .addSerializer(BigInteger.class, ToStringSerializer.instance)
                .addSerializer(BigDecimal.class, ToStringSerializer.instance)
                .addDeserializer(Long.class, new NumberDeserializers.LongDeserializer(Long.class, null))
                .addDeserializer(Long.TYPE, new NumberDeserializers.LongDeserializer(Long.class, null))
                .addDeserializer(Double.class, new NumberDeserializers.DoubleDeserializer(Double.class, null))
                .addDeserializer(Double.TYPE, new NumberDeserializers.DoubleDeserializer(Double.class, null))
                .addDeserializer(Float.class, new NumberDeserializers.FloatDeserializer(Float.class, null))
                .addDeserializer(Float.TYPE, new NumberDeserializers.FloatDeserializer(Float.class, null))
                .addDeserializer(BigDecimal.class, NumberDeserializers.BigDecimalDeserializer.instance)
                .addDeserializer(BigInteger.class, NumberDeserializers.BigIntegerDeserializer.instance);
        return builder.createXmlMapper(false)
                      .build()
                      .findAndRegisterModules()
                      .registerModule(simpleModule)
                      .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    /**
     * 配置一个默认的不用校验登陆的Sa-Token配置
     *
     * @return TakeshiSaTokenConfig
     */
    @Bean
    @ConditionalOnMissingBean
    public TakeshiSaTokenConfig takeshiSaTokenConfig() {
        return TakeshiInterceptor::newInstance;
    }

    /**
     * 配置redisson客户端
     *
     * @param redisProperties redisProperties
     * @param objectMapper    objectMapper
     * @return RedissonClient
     */
    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    public RedissonClient redissonClient(RedisProperties redisProperties, ObjectMapper objectMapper) {
        Config config = new Config();
        config.setCodec(new JsonJacksonCodec(objectMapper));
        config.useSingleServer()
              .setClientName(redisProperties.getClientName())
              .setAddress(redisProperties.getUrl())
              .setDatabase(redisProperties.getDatabase());
        return Redisson.create(config);
    }

    /**
     * 配置cache缓存到redis
     *
     * @param objectMapper objectMapper
     * @param factory      factory
     * @return CacheManager
     */
    @Bean
    @ConditionalOnMissingBean
    public CacheManager cacheManager(ObjectMapper objectMapper, RedisConnectionFactory factory) {
        return TtlRedisCacheManager.defaultInstance(objectMapper, factory);
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
    @ConditionalOnMissingBean
    public PlatformTransactionManager platformTransactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

}