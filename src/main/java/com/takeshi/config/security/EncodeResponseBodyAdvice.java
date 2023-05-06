package com.takeshi.config.security;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import com.takeshi.annotation.SystemSecurity;
import com.takeshi.config.StaticConfig;
import com.takeshi.pojo.basic.ResponseData;
import com.takeshi.util.GsonUtil;
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
public class EncodeResponseBodyAdvice implements ResponseBodyAdvice<ResponseData<Object>> {

    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        SystemSecurity methodAnnotation = methodParameter.getMethodAnnotation(SystemSecurity.class);
        if (Objects.nonNull(methodAnnotation)) {
            return methodAnnotation.outEncode();
        }
        SystemSecurity classAnnotation = AnnotationUtil.getAnnotation(methodParameter.getContainingClass(), SystemSecurity.class);
        if (Objects.nonNull(classAnnotation)) {
            return classAnnotation.outEncode();
        }
        return false;
    }

    @Override
    public ResponseData<Object> beforeBodyWrite(@Nullable ResponseData<Object> body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (ObjUtil.isNotNull(body) && ObjUtil.isNotNull(body.getData())) {
            // 这里使用rsa私钥加密，前端使用rsa公钥解密
            String str = CharSequence.class.isAssignableFrom(body.getData().getClass()) ? (String) body.getData() : GsonUtil.toJson(body.getData());
            String data = StaticConfig.rsa.encryptBase64(str, KeyType.PrivateKey);
            body.setData(data);
        }
        return body;
    }
}
