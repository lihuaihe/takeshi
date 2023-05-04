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
@AutoConfiguration
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

}
