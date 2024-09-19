package com.takeshi.config.properties;

import cn.hutool.core.text.NamingCase;
import cn.hutool.core.util.StrUtil;
import com.takeshi.enums.FileAclEnum;
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
@AutoConfiguration(value = "aWSSecretsManagerCredentials")
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
    private String region = "us-west-2";

    /**
     * 是否阻止所有公开访问
     */
    private boolean blockPublicAccess = true;

    /**
     * 是否启用桶的ACL，必须配置blockPublicAccess=false才会生效
     * <br/>
     * bucketAcl 和 bucketPolicyPublicRead 二选一开启公共读即可
     */
    private boolean bucketAcl;

    /**
     * 是否为指定的存储桶启用公共读访问策略，必须配置blockPublicAccess=false才会生效
     * <br/>
     * bucketAcl 和 bucketPolicyPublicRead 二选一开启公共读即可
     */
    private boolean bucketPolicyPublicRead;

    /**
     * 是否为指定的存储桶启用传输加速，非必要可以不启用这个，启用了会浪费带宽，但是如果是非同个地区的访问启用了则会提升访问速度
     */
    private boolean bucketAccelerate;

    /**
     * 文件对象的访问控制列表 (ACL)，如果配置了bucketPolicyPublicRead，无需额外配置文件ACL也可公共读
     */
    private FileAclEnum fileAcl = FileAclEnum.PRIVATE;

    /**
     * 存储在AWS Secrets Manager中的 AWS 访问 s3 的密钥ID名称
     */
    private String s3AccessKeySecrets;

    /**
     * 存储在AWS Secrets Manager中的 AWS  访问 s3 的密钥名称
     */
    private String s3SecretKeySecrets;

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
