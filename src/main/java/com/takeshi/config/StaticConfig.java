package com.takeshi.config;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.AsymmetricAlgorithm;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.http.useragent.Platform;
import com.takeshi.config.properties.TakeshiProperties;
import com.takeshi.constants.TakeshiConstants;
import com.takeshi.enums.TakeshiRedisKeyEnum;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
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
@Slf4j
@AutoConfiguration(value = "StaticConfig", before = TakeshiConfig.class)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
public class StaticConfig {

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
     * AES
     */
    public static AES aes;

    /**
     * 构造函数
     *
     * @param applicationName   applicationName
     * @param active            active
     * @param redissonClient    redissonClient
     * @param takeshiProperties takeshiProperties
     * @throws InterruptedException 获取锁异常
     */
    public StaticConfig(@Value("${spring.application.name:}") String applicationName,
                        @Value("${spring.profiles.active:}") String active,
                        RedissonClient redissonClient,
                        TakeshiProperties takeshiProperties) throws InterruptedException {
        log.info("StaticConfig Bean initialization...");
        StaticConfig.applicationName = applicationName;
        StaticConfig.active = active;
        StaticConfig.takeshiProperties = takeshiProperties;
        String aesKey = takeshiProperties.getAesKey();
        if (StrUtil.isBlank(aesKey)) {
            // 如果没有指定aes的key，则使用默认的aesKey对项目名+环境进行加密后截取前16位得到新的aesKey
            String data = StrUtil.concat(true, StrUtil.blankToDefault(takeshiProperties.getProjectName(), applicationName), StrUtil.DASHED, active);
            aesKey = StrUtil.subPre(
                    SecureUtil.aes("NT0Z1y2X725C6b7A".getBytes(StandardCharsets.UTF_8)).encryptBase64(data)
                    , 16
            );
        }
        StaticConfig.aes = SecureUtil.aes(aesKey.getBytes(StandardCharsets.UTF_8));
        if (StrUtil.isNotBlank(takeshiProperties.getProjectName())) {
            RLock lock = redissonClient.getLock(TakeshiRedisKeyEnum.LOCK_RSA_SECURE.projectKey());
            if (lock.tryLock(10, TimeUnit.SECONDS)) {
                // 保存rsa算法的公钥和私钥到redis中
                try {
                    RBucket<String> rsaPrivateKeyBucket = redissonClient.getBucket(TakeshiRedisKeyEnum.PRIVATE_KEY_BASE64.projectKey());
                    RBucket<String> rsaPublicKeyBucket = redissonClient.getBucket(TakeshiRedisKeyEnum.PUBLIC_KEY_BASE64.projectKey());
                    if (rsaPrivateKeyBucket.isExists() && rsaPublicKeyBucket.isExists()) {
                        StaticConfig.rsa = SecureUtil.rsa(rsaPrivateKeyBucket.get(), rsaPublicKeyBucket.get());
                    } else {
                        KeyPair keyPair = SecureUtil.generateKeyPair(AsymmetricAlgorithm.RSA.getValue(), SecureUtil.DEFAULT_KEY_SIZE, takeshiProperties.getProjectName().getBytes(StandardCharsets.UTF_8));
                        StaticConfig.rsa = SecureUtil.rsa(keyPair.getPrivate().getEncoded(), keyPair.getPublic().getEncoded());
                        rsaPrivateKeyBucket.setIfAbsent(StaticConfig.rsa.getPrivateKeyBase64());
                        rsaPublicKeyBucket.setIfAbsent(StaticConfig.rsa.getPublicKeyBase64());
                    }
                } finally {
                    lock.unlock();
                }
            } else {
                throw new IllegalStateException("Creating RSA object using generated private and public keys failed to acquire lock.");
            }
        }
        // 将Android平板平台类型添加到Hutool的Platform静态变量中。
        Platform.mobilePlatforms.add(4, TakeshiConstants.ANDROID_TABLET);
        Platform.platforms.add(4, TakeshiConstants.ANDROID_TABLET);
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
     * 是sandbox环境
     *
     * @return boolean
     */
    public static boolean isSandboxActive() {
        return StrUtil.equals("sandbox", StaticConfig.active);
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
