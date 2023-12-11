package com.takeshi.config;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.AsymmetricAlgorithm;
import cn.hutool.crypto.asymmetric.RSA;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.takeshi.component.RedisComponent;
import com.takeshi.config.properties.TakeshiProperties;
import com.takeshi.enums.TakeshiRedisKeyEnum;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.MessageSource;
import org.springframework.core.Ordered;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.util.concurrent.TimeUnit;

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
     * RSA
     */
    public static RSA rsa;

    /**
     * 构造函数
     *
     * @param applicationName   applicationName
     * @param active            active
     * @param objectMapper      objectMapper
     * @param messageSource     messageSource
     * @param redisComponent    redisComponent
     * @param takeshiProperties takeshiProperties
     * @throws InterruptedException 获取锁异常
     */
    public StaticConfig(@Value("${spring.application.name:null}") String applicationName,
                        @Value("${spring.profiles.active:null}") String active,
                        ObjectMapper objectMapper,
                        MessageSource messageSource,
                        RedisComponent redisComponent,
                        TakeshiProperties takeshiProperties) throws InterruptedException {
        StaticConfig.applicationName = applicationName;
        StaticConfig.active = active;
        StaticConfig.objectMapper = objectMapper;
        StaticConfig.messageSource = messageSource;
        StaticConfig.redisComponent = redisComponent;
        StaticConfig.takeshiProperties = takeshiProperties;
        if (StrUtil.isNotBlank(takeshiProperties.getProjectName())) {
            RLock lock = redisComponent.getLock(TakeshiRedisKeyEnum.LOCK_RSA_SECURE.projectKey());
            if (lock.tryLock(10, TimeUnit.SECONDS)) {
                // 保存rsa算法的公钥和私钥到redis中
                try {
                    String rsaPrivateKey = TakeshiRedisKeyEnum.PRIVATE_KEY_BASE64.projectKey();
                    String rsaPublicKey = TakeshiRedisKeyEnum.PUBLIC_KEY_BASE64.projectKey();
                    if (redisComponent.hasKey(rsaPrivateKey) && redisComponent.hasKey(rsaPublicKey)) {
                        StaticConfig.rsa = SecureUtil.rsa(redisComponent.get(rsaPrivateKey), redisComponent.get(rsaPublicKey));
                    } else {
                        KeyPair keyPair = SecureUtil.generateKeyPair(AsymmetricAlgorithm.RSA.getValue(), SecureUtil.DEFAULT_KEY_SIZE, takeshiProperties.getProjectName().getBytes(StandardCharsets.UTF_8));
                        StaticConfig.rsa = SecureUtil.rsa(keyPair.getPrivate().getEncoded(), keyPair.getPublic().getEncoded());
                        redisComponent.saveIfAbsent(rsaPrivateKey, StaticConfig.rsa.getPrivateKeyBase64());
                        redisComponent.saveIfAbsent(rsaPublicKey, StaticConfig.rsa.getPublicKeyBase64());
                    }
                } finally {
                    lock.unlock();
                }
            } else {
                throw new IllegalStateException("Creating RSA object using generated private and public keys failed to acquire lock.");
            }
        }
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
