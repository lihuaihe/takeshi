package com.takeshi.util;

import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * AwsSecretsManagerUtil
 * <pre>{@code
 * implementation 'software.amazon.awssdk:secretsmanager:+'
 * }</pre>
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Slf4j
public final class AwsSecretsManagerUtil {

    /**
     * 获取到的密钥信息
     */
    public static volatile JsonNode SECRET;

    /**
     * 获取密钥信息的JsonNode
     *
     * @return JsonNode
     */
    public static JsonNode getSecret() {
        return SECRET;
    }

    /**
     * 根据指定转化的类，获取密钥信息
     *
     * @param beanClass beanClass
     * @param <T>       T
     * @return T
     */
    public static <T> T getSecret(Class<T> beanClass) {
        return SpringUtil.getBean(ObjectMapper.class).convertValue(getSecret(), beanClass);
    }

}
