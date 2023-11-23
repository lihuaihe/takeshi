package com.takeshi.config.satoken;

import cn.hutool.core.util.ArrayUtil;
import com.takeshi.config.StaticConfig;
import com.takeshi.constants.TakeshiConstants;
import com.takeshi.jackson.BigDecimalFormatAnnotationFormatterFactory;
import com.takeshi.jackson.CurrencyConversionAnnotationFormatterFactory;
import com.takeshi.jackson.NumZeroFormatAnnotationFormatterFactory;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * TakeshiSaTokenConfigurer
 *
 * @author 七濑武【Nanase Takeshi】
 */
public interface TakeshiSaTokenConfig extends WebMvcConfigurer {

    /**
     * 基于路由的拦截式鉴权
     *
     * @return TakeshiInterceptor
     */
    default TakeshiInterceptor saRouteBuild() {
        return TakeshiInterceptor.newInstance();
    }

    /**
     * 注册Sa-Token的注解拦截器，打开注解式鉴权功能
     *
     * @param registry registry
     */
    @Override
    default void addInterceptors(InterceptorRegistry registry) {
        // 注册注解拦截器
        InterceptorRegistration interceptorRegistration = registry.addInterceptor(this.saRouteBuild());
        interceptorRegistration.addPathPatterns("/**").excludePathPatterns(TakeshiConstants.EXCLUDE_URL);
        String[] excludeUrl = StaticConfig.takeshiProperties.getExcludeUrl();
        if (ArrayUtil.isNotEmpty(excludeUrl)) {
            interceptorRegistration.excludePathPatterns(excludeUrl);
        }
    }

    /**
     * 除了默认注册的转换器和格式化程序之外，还添加Converters和Formatters程序。
     *
     * @param registry registry
     */
    @Override
    default void addFormatters(FormatterRegistry registry) {
        registry.addFormatterForFieldAnnotation(new NumZeroFormatAnnotationFormatterFactory());
        registry.addFormatterForFieldAnnotation(new BigDecimalFormatAnnotationFormatterFactory());
        registry.addFormatterForFieldAnnotation(new CurrencyConversionAnnotationFormatterFactory());
    }

}
