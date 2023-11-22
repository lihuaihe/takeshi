package com.takeshi.config.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * AWS密钥管理凭证
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Data
@AutoConfiguration
@ConfigurationProperties(prefix = "takeshi.aws-secrets")
public class AWSSecretsManagerCredentials {

    /**
     * 是否启用AWS S3文件存储功能
     */
    private boolean enabled = false;

    /**
     * SecretsManager 使用的 accessKey
     */
    private String accessKey;

    /**
     * SecretsManager 使用的 secretKey
     */
    private String secretKey;

    /**
     * SecretsManager 使用的 密钥名称
     */
    private String secretId;

    /**
     * 存储桶名称，默认使用{takeshi.projectName}-bucket
     */
    @Value("${takeshi.aws-secrets.bucket-name:#{T(cn.hutool.core.text.NamingCase).toKebabCase('${takeshi.project-name}').concat('-bucket')}}")
    private String bucketName;

    /**
     * 设置客户端使用的区域（例如：us-west-2）
     */
    private String region = "us-west-2";

    /**
     * S3临时URL的有效时间，默认7天
     */
    private Duration expirationTime = Duration.ofDays(7);

    /**
     * 存储在AWS Secrets Manager中的 AWS s3 密钥ID名称
     */
    private String accessKeySecrets = "AWS-S3-Access-key-ID";

    /**
     * 存储在AWS Secrets Manager中的 AWS s3 密钥名称
     */
    private String secretKeySecrets = "AWS-S3-Secret-access-key";

}
