package com.takeshi.config.properties;

import cn.hutool.core.text.NamingCase;
import cn.hutool.core.util.StrUtil;
import com.amazonaws.services.s3.model.CannedAccessControlList;
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
@AutoConfiguration(value = "AWSSecretsManagerCredentials")
@ConfigurationProperties(prefix = "takeshi.aws-secrets")
public class AWSSecretsManagerCredentials {

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
     * 存储桶名称，默认使用${takeshi.project-name}-bucket
     */
    @Value("${takeshi.aws-secrets.bucket-name:#{T(com.takeshi.config.properties.AWSSecretsManagerCredentials).formatBucketName('${takeshi.project-name:}')}}")
    private String bucketName;

    /**
     * 设置客户端使用的区域（例如：us-west-2）
     */
    private String region;

    /**
     * 文件对象的访问控制列表 (ACL)
     */
    private CannedAccessControlList fileAcl;

    /**
     * 存储在AWS Secrets Manager中的 AWS 访问 s3 的密钥ID名称
     */
    private String accessKeySecrets = "AWS-S3-Access-key-ID";

    /**
     * 存储在AWS Secrets Manager中的 AWS  访问 s3 的密钥名称
     */
    private String secretKeySecrets = "AWS-S3-Secret-access-key";

    /**
     * 格式化存储桶名称
     *
     * @param projectName 项目名称
     * @return 格式化后的存储桶名称
     */
    public static String formatBucketName(String projectName) {
        if (StrUtil.isBlank(projectName)) {
            return null;
        }
        return NamingCase.toKebabCase(projectName).concat("-bucket");
    }

}
