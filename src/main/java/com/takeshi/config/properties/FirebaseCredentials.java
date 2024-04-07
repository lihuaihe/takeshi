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
@AutoConfiguration(value = "FirebaseCredentials")
@ConfigurationProperties(prefix = "takeshi.firebase")
public class FirebaseCredentials {

    /**
     * jsonFileName
     */
    private String jsonFileName = "firebase.json";

    /**
     * databaseUrl，url后面不需要/，例如：https://takeshi-firebase-adminsdk-12345.firebaseio.com
     */
    private String databaseUrl;

    /**
     * AWSSecrets里面的key，此优先级高于databaseUrl
     */
    private String databaseUrlSecrets;

}
