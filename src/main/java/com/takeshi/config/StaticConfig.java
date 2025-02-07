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
import com.takeshi.util.TakeshiUtil;
import lombok.extern.slf4j.Slf4j;
import net.dreamlu.mica.auto.annotation.AutoRunListener;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.time.Duration;
import java.time.ZoneId;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * StaticConfig
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Slf4j
@AutoRunListener
public class StaticConfig implements SpringApplicationRunListener {

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
     * 构造方法，需要提供这个构造方法，否则不会被 Spring 加载
     *
     * @param application application
     * @param args        args
     */
    public StaticConfig(SpringApplication application, String[] args) {
        if (application.getListeners().stream().noneMatch(item -> item instanceof ApplicationPidFileWriter)) {
            // 添加应用程序PID文件监听器
            application.addListeners(new ApplicationPidFileWriter());
        }
    }

    @Override
    public void environmentPrepared(ConfigurableBootstrapContext bootstrapContext, ConfigurableEnvironment environment) {
        Binder binder = Binder.get(environment);
        TakeshiProperties takeshiProperties = binder.bind("takeshi", TakeshiProperties.class).orElse(null);
        String applicationName = environment.getProperty("spring.application.name");
        String active = environment.getProperty("spring.profiles.active");
        StaticConfig.takeshiProperties = takeshiProperties;
        StaticConfig.applicationName = applicationName;
        StaticConfig.active = active;
        String aesKey = takeshiProperties.getAesKey();
        if (StrUtil.isBlank(aesKey)) {
            // 如果没有指定aes的key，则使用默认的aesKey对项目名称+环境进行加密后截取前16位得到新的aesKey
            String data = StrUtil.concat(true, StrUtil.blankToDefault(takeshiProperties.getProjectName(), applicationName), StrUtil.DASHED, active);
            aesKey = StrUtil.subPre(
                    SecureUtil.aes("NT0Z1y2X725C6b7A".getBytes(StandardCharsets.UTF_8)).encryptBase64(data),
                    16
            );
        }
        StaticConfig.aes = SecureUtil.aes(aesKey.getBytes(StandardCharsets.UTF_8));
        // 将Android平板平台类型添加到Hutool的Platform静态变量中。
        Platform.mobilePlatforms.add(4, TakeshiConstants.ANDROID_TABLET);
        Platform.platforms.add(4, TakeshiConstants.ANDROID_TABLET);
    }

    @Override
    public void started(ConfigurableApplicationContext context, Duration timeTaken) {
        String projectName = context.getEnvironment().getProperty("takeshi.project-name");
        if (StrUtil.isNotBlank(projectName)) {
            RedissonClient redissonClient = context.getBean(RedissonClient.class);
            RLock lock = redissonClient.getLock(TakeshiRedisKeyEnum.LOCK_RSA_SECURE.projectKey());
            try {
                if (lock.tryLock(10, TimeUnit.SECONDS)) {
                    // 保存rsa算法的公钥和私钥到redis中
                    RBucket<String> rsaPrivateKeyBucket = redissonClient.getBucket(TakeshiRedisKeyEnum.PRIVATE_KEY_BASE64.projectKey());
                    RBucket<String> rsaPublicKeyBucket = redissonClient.getBucket(TakeshiRedisKeyEnum.PUBLIC_KEY_BASE64.projectKey());
                    if (rsaPrivateKeyBucket.isExists() && rsaPublicKeyBucket.isExists()) {
                        StaticConfig.rsa = SecureUtil.rsa(rsaPrivateKeyBucket.get(), rsaPublicKeyBucket.get());
                    } else {
                        KeyPair keyPair = SecureUtil.generateKeyPair(AsymmetricAlgorithm.RSA.getValue(), SecureUtil.DEFAULT_KEY_SIZE, projectName.getBytes(StandardCharsets.UTF_8));
                        StaticConfig.rsa = SecureUtil.rsa(keyPair.getPrivate().getEncoded(), keyPair.getPublic().getEncoded());
                        rsaPrivateKeyBucket.setIfAbsent(StaticConfig.rsa.getPrivateKeyBase64());
                        rsaPublicKeyBucket.setIfAbsent(StaticConfig.rsa.getPublicKeyBase64());
                    }
                }
            } catch (InterruptedException e) {
                throw new IllegalStateException("Creating RSA object using generated private and public keys failed to acquire lock.");
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }
    }

    @Override
    public void ready(ConfigurableApplicationContext context, Duration timeTaken) {
        ConfigurableEnvironment environment = context.getEnvironment();
        String applicationName = environment.getProperty("spring.application.name");
        String javaVendor = environment.getProperty("java.vendor");
        String javaVersion = environment.getProperty("java.version");
        String serverPort = environment.getProperty("server.port", "8080");
        String contextPath = environment.getProperty("server.servlet.context-path");
        log.info("""
                         
                          ________    _______   ________
                         |\\_____  \\  /  ___  \\ |\\   ____\\
                          \\|___/  /|/__/|_/  /|\\ \\  \\___|_
                              /  / /|__|//  / / \\ \\_____  \\
                             /  / /     /  /_/__ \\|____|\\  \\
                            /__/ /     |\\________\\ ____\\_\\  \\
                            |__|/       \\|_______||\\_________\\
                                                  \\|_________|
                         Application {} Successfully started using Java ({}) {} with PID {}
                         Default language: {}. Default region: {}. Default TimeZone: {}
                         Swagger Api Url: http://{}:{}{}/doc.html""",
                 applicationName, javaVersion, javaVendor, ProcessHandle.current().pid(),
                 Locale.getDefault().getLanguage(), Locale.getDefault().getCountry(), ZoneId.systemDefault(),
                 TakeshiUtil.getLocalhostStr(), serverPort, contextPath);
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
