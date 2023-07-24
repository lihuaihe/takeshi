package com.takeshi.config.satoken;

import cn.dev33.satoken.interceptor.SaInterceptor;
import com.takeshi.constants.TakeshiConstants;
import com.takeshi.jackson.BigDecimalFormatAnnotationFormatterFactory;
import com.takeshi.jackson.NumZeroFormatAnnotationFormatterFactory;
import org.springframework.format.FormatterRegistry;
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
        new SaInterceptor();
        // 注册注解拦截器
        registry.addInterceptor(this.saRouteBuild()).addPathPatterns("/**").excludePathPatterns(TakeshiConstants.EXCLUDE_URL);
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
    }

}
