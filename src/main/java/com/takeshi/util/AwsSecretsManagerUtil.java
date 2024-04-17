package com.takeshi.util;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.takeshi.config.properties.AWSSecretsManagerCredentials;
import lombok.extern.slf4j.Slf4j;

/**
 * AwsSecretsManagerUtil
 * <pre>{@code
 * implementation 'com.amazonaws:aws-java-sdk-secretsmanager:+'
 * }</pre>
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Slf4j
public final class AwsSecretsManagerUtil {

    /**
     * 获取到的密钥信息
     */
    private static volatile JsonNode jsonNode;

    /**
     * 获取密钥信息的JsonNode
     *
     * @return JsonNode
     */
    public static JsonNode getSecret() {
        if (ObjUtil.isNull(jsonNode)) {
            synchronized (AwsSecretsManagerUtil.class) {
                if (ObjUtil.isNull(jsonNode)) {
                    AWSSecretsManagerCredentials awsSecrets = SpringUtil.getBean(AWSSecretsManagerCredentials.class);
                    try {
                        if (StrUtil.isAllNotBlank(awsSecrets.getSecretKey(), awsSecrets.getSecretKey())) {
                            AWSSecretsManager awsSecretsManager =
                                    AWSSecretsManagerClientBuilder.standard()
                                                                  .withRegion(awsSecrets.getRegion())
                                                                  .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(awsSecrets.getAccessKey(), awsSecrets.getSecretKey())))
                                                                  .build();
                            GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest().withSecretId(awsSecrets.getSecretId());
                            GetSecretValueResult getSecretValueResult = awsSecretsManager.getSecretValue(getSecretValueRequest);
                            String secret = StrUtil.isNotBlank(getSecretValueResult.getSecretString()) ? getSecretValueResult.getSecretString() : new String(java.util.Base64.getDecoder().decode(getSecretValueResult.getSecretBinary()).array());
                            jsonNode = SpringUtil.getBean(ObjectMapper.class).readValue(secret, JsonNode.class);
                            log.info("AwsSecretsManagerUtil.getSecret --> AWSSecretsManager Initialization successful");
                        } else {
                            log.warn("AwsSecretsManagerUtil.getSecret --> When AWSSecretsManager is initialized, accessKey and secretKey are both empty and no initialization is performed.");
                        }
                    } catch (Exception e) {
                        log.error("AwsSecretsManagerUtil.getSecret --> AWSSecretsManager initialization failed, e: ", e);
                    }
                }
            }
        }
        return jsonNode;
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
