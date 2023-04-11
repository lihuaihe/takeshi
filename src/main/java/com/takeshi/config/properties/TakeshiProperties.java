package com.takeshi.config.properties;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.NamingCase;
import cn.hutool.core.util.StrUtil;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.regions.Regions;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 自定义额外属性值
 *
 * @author 七濑武【Nanase Takeshi】
 * @date 2021/08/17 17:39
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "takeshi")
public class TakeshiProperties {

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 是否开启移动端请求工具限制
     */
    private boolean appPlatform;

    /**
     * 是否开启参数签名限制
     */
    private boolean signature;

    /**
     * AWS凭证
     */
    private AWSSecretsManagerCredentials awsCredentials = new AWSSecretsManagerCredentials();

    /**
     * Mandrill凭证
     */
    private MandrillCredentials mandrillCredentials = new MandrillCredentials();

    /**
     * Firebase凭证
     */
    private FirebaseCredentials firebaseCredentials = new FirebaseCredentials();

    public String getProjectName() {
        Assert.isFalse(StrUtil.isBlank(this.projectName), "Properties {takeshi.projectName} is null");
        return this.projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
        if (StrUtil.isNotBlank(projectName) && StrUtil.isBlank(this.getAwsCredentials().bucketName)) {
            this.getAwsCredentials().setBucketName(NamingCase.toKebabCase(projectName) + "-bucket");
        }
    }

    @Data
    public static class AWSSecretsManagerCredentials implements AWSCredentials {
        /**
         * accessKey
         */
        private String accessKey;
        /**
         * secretKey
         */
        private String secretKey;
        /**
         * 存储桶名称，默认使用{takeshi.projectName}-bucket
         */
        private String bucketName;
        /**
         * 设置客户端使用的区域（例如：us-west-1）
         */
        private String region;

        public String getBucketName() {
            Assert.isFalse(StrUtil.isBlank(this.bucketName), "Properties {takeshi.awsCredentials.bucketName} or {takeshi.projectName} is null");
            return this.bucketName;
        }

        public Regions getRegion() {
            return StrUtil.isBlank(this.region) ? Regions.GovCloud : Regions.fromName(this.region);
        }

        @Override
        public String getAWSAccessKeyId() {
            return accessKey;
        }

        @Override
        public String getAWSSecretKey() {
            return secretKey;
        }
    }

    @Data
    public static class MandrillCredentials {
        /**
         * apiKey
         */
        private String apiKey;
        /**
         * 发送人邮箱
         */
        private String fromEmail;
        /**
         * 发送人名称
         */
        private String fromName;
    }

    @Data
    public static class FirebaseCredentials {
        /**
         * jsonFileName
         */
        private String jsonFileName;
        /**
         * databaseUrl
         */
        private String databaseUrl;
    }

}
