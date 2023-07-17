package com.takeshi.config;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.AsymmetricAlgorithm;
import cn.hutool.crypto.asymmetric.RSA;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.takeshi.component.RedisComponent;
import com.takeshi.config.properties.TakeshiProperties;
import com.takeshi.enums.TakeshiRedisKeyEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.MessageSource;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.BoundValueOperations;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;

/**
 * StaticConfig
 * 配置一些yml里面变量可以在static类/方法中使用
 *
 * @author 七濑武【Nanase Takeshi】
 */
@AutoConfiguration(before = TakeshiConfig.class)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
public class StaticConfig {

    /**
     * ObjectMapper
     */
    public static ObjectMapper objectMapper;

    /**
     * 解析国际化消息
     */
    public static MessageSource messageSource;

    /**
     * RedisComponent
     */
    public static RedisComponent redisComponent;

    /**
     * 自定义额外属性值
     */
    public static TakeshiProperties takeshiProperties;

    /**
     * 模块应用名称
     */
    public static String applicationName;

    /**
     * 运行环境
     */
    public static String active;

    /**
     * 服务器端口
     */
    public static String serverPort;

    /**
     * RSA算法用到的项目的私钥
     */
    public static String privateKeyBase64;

    /**
     * RSA算法用到的项目的公钥
     */
    public static String publicKeyBase64;

    /**
     * RSA
     */
    public static RSA rsa;

    /**
     * 构造函数
     *
     * @param applicationName   applicationName
     * @param active            active
     * @param serverPort        serverPort
     * @param objectMapper      objectMapper
     * @param messageSource     messageSource
     * @param redisComponent    redisComponent
     * @param takeshiProperties takeshiProperties
     */
    public StaticConfig(@Value("${spring.application.name}") String applicationName,
                        @Value("${spring.profiles.active}") String active,
                        @Value("${server.port}") String serverPort,
                        ObjectMapper objectMapper,
                        MessageSource messageSource,
                        RedisComponent redisComponent,
                        TakeshiProperties takeshiProperties) {
        StaticConfig.applicationName = applicationName;
        StaticConfig.active = active;
        StaticConfig.serverPort = serverPort;
        StaticConfig.objectMapper = objectMapper;
        StaticConfig.messageSource = messageSource;
        StaticConfig.redisComponent = redisComponent;
        StaticConfig.takeshiProperties = takeshiProperties;
        // 保存rsa算法的公钥和私钥到redis中
        String projectPrivateKey = TakeshiRedisKeyEnum.PRIVATE_KEY_BASE64.projectKey();
        String projectPublicKey = TakeshiRedisKeyEnum.PUBLIC_KEY_BASE64.projectKey();
        BoundValueOperations<String, String> privateKeyBoundValue = redisComponent.boundValueOps(projectPrivateKey);
        BoundValueOperations<String, String> publicKeyBoundValue = redisComponent.boundValueOps(projectPublicKey);
        String privateKeyValue = privateKeyBoundValue.get();
        String publicKeyValue = publicKeyBoundValue.get();
        if (StrUtil.hasBlank(privateKeyValue, publicKeyValue)) {
            KeyPair keyPair = SecureUtil.generateKeyPair(AsymmetricAlgorithm.RSA.getValue(), SecureUtil.DEFAULT_KEY_SIZE, takeshiProperties.getProjectName().getBytes(StandardCharsets.UTF_8));
            privateKeyValue = Base64.encode(keyPair.getPrivate().getEncoded());
            publicKeyValue = Base64.encode(keyPair.getPublic().getEncoded());
            privateKeyBoundValue.setIfAbsent(privateKeyValue);
            publicKeyBoundValue.setIfAbsent(publicKeyValue);
        }
        StaticConfig.privateKeyBase64 = privateKeyValue;
        StaticConfig.publicKeyBase64 = publicKeyValue;
        StaticConfig.rsa = SecureUtil.rsa(privateKeyValue, publicKeyValue);
    }

    /**
     * 是dev环境
     *
     * @return boolean
     */
    public static boolean isDevActive() {
        return StrUtil.equals("dev", StaticConfig.active);
    }

    /**
     * 是test环境
     *
     * @return boolean
     */
    public static boolean isTestActive() {
        return StrUtil.equals("test", StaticConfig.active);
    }

    /**
     * 是prod环境
     *
     * @return boolean
     */
    public static boolean isProdActive() {
        return StrUtil.equals("prod", StaticConfig.active);
    }

}
