package com.takeshi.config;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.JavaType;
import com.takeshi.annotation.ApiGroup;
import com.takeshi.annotation.SystemSecurity;
import com.takeshi.constants.TakeshiCode;
import com.takeshi.pojo.bo.RetBO;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverterContext;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.DateSchema;
import io.swagger.v3.oas.models.media.DateTimeSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.customizers.GlobalOperationCustomizer;
import org.springdoc.core.customizers.SpringDocCustomizers;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.providers.ObjectMapperProvider;
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
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.util.*;

/**
 * OpenApiConfig
 *
 * @author 七濑武【Nanase Takeshi】
 */
@AutoConfiguration(value = "openApiConfig")
@RequiredArgsConstructor
public class OpenApiConfig {

    private final MessageSource messageSource;

    private final SaTokenConfig saTokenConfig;

    private final ApplicationContext applicationContext;

    @Value("${spring.application.name:}")
    private String applicationName;

    /**
     * 自定义ModelConverter
     */
    @Component
    @RequiredArgsConstructor
    public static class CustomModelConverter implements ModelConverter {

        private final ObjectMapperProvider objectMapperProvider;

        @Nullable
        @Override
        public Schema<?> resolve(AnnotatedType type, ModelConverterContext context, Iterator<ModelConverter> chain) {
            JavaType javaType = objectMapperProvider.jsonMapper().constructType(type.getType());
            Class<?> clazz = javaType.getRawClass();
            if (clazz == Long.class || clazz == Double.class || clazz == Float.class || clazz == BigDecimal.class || clazz == BigInteger.class) {
                type.setType(String.class);
                return chain.next().resolve(type, context, chain);
            } else if (clazz == LocalTime.class) {
                type.setType(String.class);
                Schema<?> schema = chain.next().resolve(type, context, chain);
                if (!schema.getExampleSetFlag()) {
                    schema.setFormat("time");
                    schema.setExample("01:02:03");
                }
                return schema;
            } else if (clazz == Year.class) {
                Schema<?> schema = chain.next().resolve(type, context, chain);
                if (!schema.getExampleSetFlag()) {
                    schema.setExample("2024");
                }
                return schema;
            } else if (clazz == YearMonth.class) {
                Schema<?> schema = chain.next().resolve(type, context, chain);
                if (!schema.getExampleSetFlag()) {
                    schema.setExample("2024-01");
                }
                return schema;
            } else if (clazz == MonthDay.class) {
                Schema<?> schema = chain.next().resolve(type, context, chain);
                if (!schema.getExampleSetFlag()) {
                    schema.setExample("--01-01");
                }
                return schema;
            } else if (clazz == OffsetTime.class) {
                Schema<?> schema = chain.next().resolve(type, context, chain);
                if (!schema.getExampleSetFlag()) {
                    schema.setExample("10:15:30+01:00");
                }
                return schema;
            }
            if (chain.hasNext()) {
                Schema<?> schema = chain.next().resolve(type, context, chain);
                if (schema instanceof DateSchema) {
                    if (!schema.getExampleSetFlag()) {
                        schema.setExample("2024-01-01");
                    }
                } else if (schema instanceof DateTimeSchema) {
                    if (!schema.getExampleSetFlag()) {
                        schema.setExample("2024-01-01T00:00:00.000Z");
                    }
                }
                return schema;
            }
            return null;
        }

    }

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
                       .map(item -> GroupedOpenApi.builder().group(item.getKey()).pathsToMatch(item.getValue().toArray(String[]::new)).build()).toList();
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
            retBOList.forEach(retBO -> apiResponses.compute(String.valueOf(retBO.getCode()), (k, v) -> ObjUtil.defaultIfNull(v, new ApiResponse()).description(retBO.getMessage())));
            boolean passToken = Optional.ofNullable(handlerMethod.getMethodAnnotation(SystemSecurity.class))
                                        .or(() -> Optional.ofNullable(handlerMethod.getBeanType().getAnnotation(SystemSecurity.class)))
                                        .map(annotation -> annotation.all() || annotation.token())
                                        .orElse(false);
            if (!passToken) {
                // Knife4j需要的全局添加鉴权参数
                operation.addSecurityItem(new SecurityRequirement().addList(saTokenConfig.getTokenName()));
            }
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
                .description("七濑武【Nanase Takeshi】通用框架接口")
                .contact(new Contact().name("七濑武【Nanase Takeshi】").email("takeshi@725.life").url("https://github.com/lihuaihe/takeshi"));
        if (StrUtil.isNotBlank(applicationName)) {
            info.title(applicationName)
                .description(applicationName + "通用框架接口");
        }
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
