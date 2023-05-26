package com.takeshi.config;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.takeshi.annotation.ApiVersion;
import com.takeshi.constants.TakeshiCode;
import com.takeshi.pojo.bo.RetBO;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;

import java.util.*;

/**
 * OpenApiConfig
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Primary
@AutoConfiguration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class OpenApiConfig {

    private final MessageSource messageSource;
    private final SaTokenConfig saTokenConfig;
    private final ApplicationContext context;

    /**
     * 配置
     *
     * @return GroupedOpenApi
     */
    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group(ApiVersion.Version.DEFAULT.getDisplay())
                .pathsToMatch("/**")
                .addOperationCustomizer((operation, handlerMethod) -> {
                    // 生成通用响应信息
                    ApiResponses apiResponses = operation.getResponses();
                    // 获取启动类所在的包名
                    Map<String, Object> beansWithAnnotation = context.getBeansWithAnnotation(SpringBootApplication.class);
                    Class<?> mainClass = beansWithAnnotation.values().toArray()[0].getClass();
                    String mainPackageName = ClassUtil.getPackage(mainClass);
                    // 查询启动类包名下TakeshiCode子类集合
                    Set<Class<?>> classSet = new HashSet<>(ClassUtil.scanPackageBySuper(mainPackageName, TakeshiCode.class));
                    classSet.add(TakeshiCode.class);
                    classSet.stream()
                            .flatMap(item -> Arrays.stream(ReflectUtil.getFields(item)))
                            .filter(item -> item.getType().isAssignableFrom(RetBO.class))
                            .forEach(item -> {
                                RetBO retBO = (RetBO) ReflectUtil.getStaticFieldValue(item);
                                // swagger文档中的响应状态码信息
                                String message = messageSource.getMessage(StrUtil.strip(retBO.getMessage(), StrUtil.DELIM_START, StrUtil.DELIM_END), null, retBO.getMessage(), Locale.SIMPLIFIED_CHINESE);
                                String name = String.valueOf(retBO.getCode());
                                ApiResponse response = apiResponses.get(name);
                                if (ObjUtil.isNotNull(response)) {
                                    apiResponses.addApiResponse(name, response.description(message));
                                } else {
                                    apiResponses.addApiResponse(name, new ApiResponse().description(message));
                                }
                            });
                    return operation;
                })
                .build();
    }

    /**
     * 自定义配置
     *
     * @return OpenAPI
     */
    @Bean
    public OpenAPI customOpenApi() {
        String tokenName = saTokenConfig.getTokenName();
        Info info = new Info()
                // 设置页面标题
                .title(StaticConfig.applicationName)
                // 设置接口描述
                .description(StaticConfig.applicationName + "通用框架接口")
                // 设置联系方式
                .contact(new Contact().name("七濑武【Nanase Takeshi】").email("takeshi@725.life").url("https://github.com/lihuaihe/takeshi"));
        SecurityScheme securityScheme = new SecurityScheme()
                .name(tokenName)
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER);
        return new OpenAPI()
                .info(info)
                .addSecurityItem(new SecurityRequirement().addList(tokenName))
                .components(new Components().addSecuritySchemes(tokenName, securityScheme));
    }

}
