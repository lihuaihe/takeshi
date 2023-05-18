package com.takeshi.config.properties;

import lombok.Data;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Firebase凭证
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Data
@AutoConfiguration
@ConfigurationProperties(prefix = "takeshi.firebase")
public class FirebaseCredentials {

    /**
     * jsonFileName
     */
    private String jsonFileName = "firebase.json";

    /**
     * databaseUrl
     */
    private String databaseUrl;

    /**
     * AWSSecrets里面的key，此优先级高于databaseUrl
     */
    private String databaseUrlSecrets;

}
