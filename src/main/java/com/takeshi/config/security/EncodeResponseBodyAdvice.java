package com.takeshi.config.security;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.takeshi.annotation.SystemSecurity;
import com.takeshi.config.StaticConfig;
import com.takeshi.pojo.basic.ResponseData;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Objects;

/**
 * 返回结果中的data字段加密
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class EncodeResponseBodyAdvice implements ResponseBodyAdvice<ResponseData<Object>> {

    private final ObjectMapper objectMapper;

    /**
     * Whether this component supports the given controller method return type
     * and the selected {@code HttpMessageConverter} type.
     *
     * @param returnType    the return type
     * @param converterType the selected converter type
     * @return {@code true} if {@link #beforeBodyWrite} should be invoked;
     * {@code false} otherwise
     */
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        SystemSecurity methodAnnotation = returnType.getMethodAnnotation(SystemSecurity.class);
        if (Objects.nonNull(methodAnnotation)) {
            return methodAnnotation.outEncode();
        }
        SystemSecurity classAnnotation = AnnotationUtil.getAnnotation(returnType.getContainingClass(), SystemSecurity.class);
        if (Objects.nonNull(classAnnotation)) {
            return classAnnotation.outEncode();
        }
        return false;
    }

    /**
     * Invoked after an {@code HttpMessageConverter} is selected and just before
     * its write method is invoked.
     *
     * @param body                  the BODY to be written
     * @param returnType            the return type of the controller method
     * @param selectedContentType   the content type selected through content negotiation
     * @param selectedConverterType the converter type selected to write to the response
     * @param request               the current request
     * @param response              the current response
     * @return the BODY that was passed in or a modified (possibly new) instance
     */
    @SneakyThrows
    @Nullable
    @Override
    public ResponseData<Object> beforeBodyWrite(@Nullable ResponseData<Object> body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (ObjUtil.isNotNull(body) && ObjUtil.isNotNull(body.getData())) {
            // 这里使用rsa私钥加密，前端使用rsa公钥解密
            String str = CharSequence.class.isAssignableFrom(body.getData().getClass()) ? (String) body.getData() : objectMapper.writeValueAsString(body.getData());
            String data = StaticConfig.rsa.encryptBase64(str, KeyType.PrivateKey);
            body.setData(data);
        }
        return body;
    }

}
