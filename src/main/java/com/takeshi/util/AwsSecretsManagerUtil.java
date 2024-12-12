package com.takeshi.util;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.takeshi.config.properties.AWSSecretsManagerCredentials;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

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
    private static volatile JsonNode SECRET;

    /**
     * 获取密钥信息的JsonNode
     *
     * @return JsonNode
     */
    @SneakyThrows
    public static JsonNode getSecret() {
        if (ObjUtil.isNull(SECRET)) {
            synchronized (AwsSecretsManagerUtil.class) {
                if (ObjUtil.isNull(SECRET)) {
                    String secretId = SpringUtil.getBean(AWSSecretsManagerCredentials.class).getSecretId();
                    GetSecretValueRequest valueRequest =
                            GetSecretValueRequest.builder()
                                                 .secretId(secretId)
                                                 .build();
                    GetSecretValueResponse valueResponse = SpringUtil.getBean(SecretsManagerClient.class).getSecretValue(valueRequest);
                    SECRET = TakeshiUtil.objectMapper.readTree(valueResponse.secretString());
                    log.info("AwsSecretsManagerUtil.getSecret --> AWSSecretsManager Initialization successful");
                }
            }
        }
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
        return TakeshiUtil.objectMapper.convertValue(getSecret(), beanClass);
    }

}
