package com.takeshi.config.security;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import com.takeshi.annotation.SystemSecurity;
import com.takeshi.config.StaticConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * 请求的数据解密，@RequestBody 注解的参数生效
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Slf4j
@RestControllerAdvice
public class DecodeRequestBodyAdvice extends RequestBodyAdviceAdapter {

    @Override
    public boolean supports(MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        SystemSecurity methodAnnotation = methodParameter.getMethodAnnotation(SystemSecurity.class);
        if (Objects.nonNull(methodAnnotation)) {
            return methodAnnotation.inDecode();
        }
        SystemSecurity classAnnotation = AnnotationUtil.getAnnotation(methodParameter.getContainingClass(), SystemSecurity.class);
        if (Objects.nonNull(classAnnotation)) {
            return classAnnotation.inDecode();
        }
        return false;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage httpInputMessage, MethodParameter methodParameter,
                                           Type type,
                                           Class<? extends HttpMessageConverter<?>> aClass) throws IOException {
        // 前端使用rsa公钥加密，这里使用rsa私钥解密
        byte[] body = StaticConfig.rsa.decrypt(IoUtil.readUtf8(httpInputMessage.getBody()), KeyType.PrivateKey);
        return new HttpInputMessage() {
            @Override
            public InputStream getBody() throws IOException {
                return IoUtil.toStream(body);
            }

            @Override
            public HttpHeaders getHeaders() {
                return httpInputMessage.getHeaders();
            }
        };
    }

}
