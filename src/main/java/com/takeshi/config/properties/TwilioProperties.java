package com.takeshi.config.properties;

import lombok.Data;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * TwilioProperties
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Data
@AutoConfiguration(value = "TwilioProperties")
@ConfigurationProperties(prefix = "takeshi.twilio")
public class TwilioProperties {

    /**
     * accountSid
     */
    private String accountSid;

    /**
     * authToken
     */
    private String authToken;

    /**
     * messagingServiceSid
     */
    private String messagingServiceSid;

    /**
     * AWSSecrets里面的key，此优先级高于accountSid
     */
    private String accountSidSecrets;

    /**
     * AWSSecrets里面的key，此优先级高于authToken
     */
    private String authTokenSecrets;

    /**
     * AWSSecrets里面的key，此优先级高于messagingServiceSid
     */
    private String messagingServiceSidSecrets;

}
