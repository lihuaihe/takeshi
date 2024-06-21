package com.takeshi.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.takeshi.config.properties.AWSSecretsManagerCredentials;
import com.takeshi.util.AwsSecretsManagerUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

/**
 * AwsSecretsManagerConfig
 *
 * @author 七濑武【Nanase Takeshi】
 */
@AutoConfiguration(value = "awsSecretsManagerConfig")
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnClass(SecretsManagerClient.class)
@ConditionalOnProperty(prefix = "takeshi.aws-secrets", name = {"access-key", "secret-key", "secret-id"})
@RequiredArgsConstructor
public class AwsSecretsManagerConfig {

    private final AWSSecretsManagerCredentials awsSecretsManagerCredentials;

    /**
     * 密钥管理客户端
     *
     * @param objectMapper objectMapper
     * @return SecretsManagerClient
     * @throws JsonProcessingException JsonProcessingException
     */
    @Bean
    @ConditionalOnMissingBean
    public SecretsManagerClient secretsManagerClient(ObjectMapper objectMapper) throws JsonProcessingException {
        SecretsManagerClient secretsManagerClient =
                SecretsManagerClient.builder()
                                    .region(Region.of(awsSecretsManagerCredentials.getRegion()))
                                    .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(awsSecretsManagerCredentials.getAccessKey(), awsSecretsManagerCredentials.getSecretKey())))
                                    .build();
        GetSecretValueRequest valueRequest =
                GetSecretValueRequest.builder()
                                     .secretId(awsSecretsManagerCredentials.getSecretId())
                                     .build();
        GetSecretValueResponse valueResponse = secretsManagerClient.getSecretValue(valueRequest);
        AwsSecretsManagerUtil.SECRET = objectMapper.readTree(valueResponse.secretString());
        return secretsManagerClient;
    }

}
