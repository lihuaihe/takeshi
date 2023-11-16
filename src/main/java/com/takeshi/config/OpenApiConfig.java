package com.takeshi.config;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.takeshi.annotation.ApiGroup;
import com.takeshi.constants.TakeshiCode;
import com.takeshi.pojo.bo.RetBO;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springdoc.core.customizers.GlobalOperationCustomizer;
import org.springdoc.core.customizers.SpringDocCustomizers;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.providers.SpringDocProviders;
import org.springdoc.core.providers.SpringWebProvider;
import org.springdoc.core.service.AbstractRequestService;
import org.springdoc.core.service.GenericResponseService;
import org.springdoc.core.service.OpenAPIService;
import org.springdoc.core.service.OperationService;
import org.springdoc.webmvc.api.MultipleOpenApiWebMvcResource;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.util.*;

/**
 * OpenApiConfig
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Primary
@AutoConfiguration
@RequiredArgsConstructor
public class OpenApiConfig {

    private final MessageSource messageSource;
    private final SaTokenConfig saTokenConfig;
    private final ApplicationContext applicationContext;

    @Value("${spring.application.name}")
    private String applicationName;

    /**
     * GroupedOpenApi
     *
     * @param springWebProvider springWebProvider
     * @return GroupedOpenApi
     */
    @SuppressWarnings("unchecked")
    @Bean
    public List<GroupedOpenApi> groupedOpenApis(SpringWebProvider springWebProvider) {
        Map<String, LinkedHashSet<String>> groupMap = new LinkedHashMap<>();
        groupMap.put("default", new LinkedHashSet<>(Collections.singleton("/**")));
        Map<RequestMappingInfo, HandlerMethod> map = springWebProvider.getHandlerMethods();
        map.forEach((requestMappingInfo, handlerMethod) -> {
            ApiGroup apiGroup = handlerMethod.getMethodAnnotation(ApiGroup.class);
            if (ObjUtil.isNotNull(requestMappingInfo.getPatternsCondition()) && ObjUtil.isNotNull(apiGroup)) {
                String path = requestMappingInfo.getPatternsCondition().getPatterns().iterator().next();
                for (String group : apiGroup.value()) {
                    groupMap.compute(group, (k, v) -> {
                        if (CollUtil.isEmpty(v)) {
                            return new LinkedHashSet<>(Collections.singleton(path));
                        } else {
                            v.add(path);
                            return v;
                        }
                    });
                }
            }
        });
        return groupMap.entrySet()
                .stream()
                .map(item -> {
                    return GroupedOpenApi.builder().group(item.getKey()).pathsToMatch(item.getValue().toArray(String[]::new)).build();
                }).toList();
    }

    /**
     * multipleOpenApiResource
     *
     * @param groupedOpenApis           groupedOpenApis
     * @param defaultOpenAPIBuilder     defaultOpenAPIBuilder
     * @param requestBuilder            requestBuilder
     * @param responseBuilder           responseBuilder
     * @param operationParser           operationParser
     * @param springDocConfigProperties springDocConfigProperties
     * @param springDocProviders        springDocProviders
     * @param springDocCustomizers      springDocCustomizers
     * @return MultipleOpenApiWebMvcResource
     */
    @Bean
    @Lazy(false)
    public MultipleOpenApiWebMvcResource multipleOpenApiResource(List<GroupedOpenApi> groupedOpenApis,
                                                                 ObjectFactory<OpenAPIService> defaultOpenAPIBuilder,
                                                                 AbstractRequestService requestBuilder,
                                                                 GenericResponseService responseBuilder,
                                                                 OperationService operationParser,
                                                                 SpringDocConfigProperties springDocConfigProperties,
                                                                 SpringDocProviders springDocProviders,
                                                                 SpringDocCustomizers springDocCustomizers) {
        return new MultipleOpenApiWebMvcResource(groupedOpenApis,
                defaultOpenAPIBuilder, requestBuilder,
                responseBuilder, operationParser,
                springDocConfigProperties,
                springDocProviders, springDocCustomizers);
    }

    /**
     * 自定义
     *
     * @return GlobalOpenApiCustomizer
     */
    @SuppressWarnings("unchecked")
    @Bean
    public GlobalOpenApiCustomizer globalOpenApiCustomizer() {
        return openApi -> {
            if (CollUtil.isNotEmpty(openApi.getComponents().getSchemas())) {
                openApi.getComponents()
                        .getSchemas()
                        .forEach((name, schema) -> {
                            Map<String, Schema<?>> properties = schema.getProperties();
                            if (CollUtil.isNotEmpty(properties)) {
                                properties.forEach((k, v) -> {
                                    String type = v.getType();
                                    String format = v.getFormat();
                                    // Long，BigInteger，BigDecimal
                                    if ((StrUtil.equals(type, "integer") && StrUtil.equals(format, "int64"))
                                            || (StrUtil.equals(type, "integer") && StrUtil.isBlank(format))
                                            || (StrUtil.equals(type, "number") && StrUtil.isBlank(format))) {
                                        v.setType("string");
                                        v.setFormat(null);
                                    }
                                });
                            }
                        });
            }
        };
    }

    /**
     * 自定义
     *
     * @return GlobalOperationCustomizer
     */
    @Bean
    public GlobalOperationCustomizer globalOperationCustomizer() {
        // 查询TakeshiCode子类集合
        Set<Class<?>> classSet = new HashSet<>();
        classSet.add(TakeshiCode.class);
        // 获取主启动类所在的包名
        String packageName = applicationContext.getBeansWithAnnotation(SpringBootApplication.class).values().toArray()[0].getClass().getPackageName();
        classSet.addAll(ClassUtil.scanPackageBySuper(packageName, TakeshiCode.class));
        // 全局通用响应信息列表
        List<RetBO> retBOList = classSet.stream()
                .flatMap(item -> Arrays.stream(ReflectUtil.getFields(item, f -> f.getType().isAssignableFrom(RetBO.class))))
                .map(item -> (RetBO) ReflectUtil.getStaticFieldValue(item))
                .sorted(Comparator.comparing(RetBO::getCode))
                .map(retBO -> {
                    // swagger文档中的响应状态码信息
                    String message = messageSource.getMessage(StrUtil.strip(retBO.getMessage(), StrUtil.DELIM_START, StrUtil.DELIM_END), null, retBO.getMessage(), Locale.SIMPLIFIED_CHINESE);
                    return new RetBO(retBO.getCode(), message);
                })
                .toList();
        return (operation, handlerMethod) -> {
            // 生成通用响应信息
            ApiResponses apiResponses = operation.getResponses();
            retBOList.forEach(retBO -> {
                apiResponses.compute(String.valueOf(retBO.getCode()), (k, v) -> ObjUtil.defaultIfNull(v, new ApiResponse()).description(retBO.getMessage()));
            });
            return operation;
        };
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
                .title(applicationName)
                // 设置接口描述
                .description(applicationName + "通用框架接口")
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
