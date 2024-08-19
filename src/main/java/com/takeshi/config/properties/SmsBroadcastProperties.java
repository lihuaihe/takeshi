package com.takeshi.config.properties;

import lombok.Data;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * SmsBroadcastProperties
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Data
@AutoConfiguration(value = "smsBroadcastProperties")
@ConfigurationProperties(prefix = "takeshi.sms-broadcast")
public class SmsBroadcastProperties {

    /**
     * userName
     */
    private String userName;

    /**
     * password
     */
    private String password;

    /**
     * 发送人名称
     */
    private String from;

    /**
     * AWSSecrets里面的key，此优先级高于userName
     */
    private String userNameSecrets;

    /**
     * AWSSecrets里面的key，此优先级高于password
     */
    private String passwordSecrets;

    /**
     * AWSSecrets里面的key，此优先级高于fromSecrets
     */
    private String fromSecrets;

}
