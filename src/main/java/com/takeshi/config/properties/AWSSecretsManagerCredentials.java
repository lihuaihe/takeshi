package com.takeshi.config.properties;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.regions.Regions;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * AWS密钥管理凭证
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Data
@AutoConfiguration
@ConfigurationProperties(prefix = "takeshi.aws-secrets")
public class AWSSecretsManagerCredentials implements AWSCredentials {

    /**
     * accessKey
     */
    private String accessKey;

    /**
     * secretKey
     */
    private String secretKey;

    /**
     * 密钥名称
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
    private Regions region = Regions.DEFAULT_REGION;

    /**
     * 存储在AWS Secrets Manager中的 AWS s3 密钥ID名称
     */
    private String accessKeyName = "AWS-S3-Access-key-ID";

    /**
     * 存储在AWS Secrets Manager中的 AWS s3 密钥名称
     */
    private String secretKeyName = "AWS-S3-Secret-access-key";

    @Override
    public String getAWSAccessKeyId() {
        return accessKey;
    }

    @Override
    public String getAWSSecretKey() {
        return secretKey;
    }

}
