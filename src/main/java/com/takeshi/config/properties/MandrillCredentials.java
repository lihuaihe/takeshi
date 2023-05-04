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
@AutoConfiguration
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

}
