package com.takeshi.config.properties;

import lombok.Data;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Mandrill凭证
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Data
@AutoConfiguration(value = "MandrillCredentials")
@ConfigurationProperties(prefix = "takeshi.mandrill")
public class MandrillCredentials {

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

    /**
     * AWSSecrets里面的key，此优先级高于apiKey
     */
    private String apiKeySecrets;

    /**
     * AWSSecrets里面的key，此优先级高于fromEmail
     */
    private String fromEmailSecrets;

    /**
     * AWSSecrets里面的key，此优先级高于fromName
     */
    private String fromNameSecrets;

}
