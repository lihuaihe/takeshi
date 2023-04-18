package com.takeshi.config;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ReflectUtil;
import com.takeshi.annotation.ApiVersion;
import com.takeshi.constants.SysCode;
import com.takeshi.pojo.vo.ResponseDataVO;
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
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.Arrays;
import java.util.Locale;

/**
 * OpenApiConfig
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Primary
@Configuration
@RequiredArgsConstructor
public class OpenApiConfig {

    private final MessageSource messageSource;

    private final SaTokenConfig saTokenConfig;

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
                    ApiResponses apiResponses = operation.getResponses();
                    // 生成通用响应信息
                    Arrays.stream(ReflectUtil.getFields(SysCode.class))
                            .filter(item -> item.getType().isAssignableFrom(ResponseDataVO.ResBean.class))
                            .forEach(item -> {
                                ResponseDataVO.ResBean resBean = (ResponseDataVO.ResBean) ReflectUtil.getStaticFieldValue(item);
                                // swagger文档中的响应状态码信息
                                String message = messageSource.getMessage(resBean.getInfo(), null, resBean.getInfo(), Locale.SIMPLIFIED_CHINESE);
                                String name = String.valueOf(resBean.getCode());
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
        SecurityScheme securityScheme = new SecurityScheme()
                .name(tokenName)
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER);
        return new OpenAPI().info(this.info())
                .addSecurityItem(new SecurityRequirement().addList(tokenName))
                .components(new Components().addSecuritySchemes(tokenName, securityScheme));
    }

    private Info info() {
        return new Info()
                // 设置页面标题
                .title(StaticConfig.applicationName)
                // 设置接口描述
                .description(StaticConfig.applicationName + "通用框架接口")
                // 设置联系方式
                .contact(new Contact().name("七濑武【Nanase Takeshi】"));
    }

}
