package com.takeshi.config;

import lombok.Getter;
import lombok.Setter;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * TtlRedissonCacheManager
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Setter
@Getter
public class TtlRedissonCacheManager extends RedissonSpringCacheManager {

    /**
     * 默认缓存配置，缓存1天
     */
    private CacheConfig defaultCacheConfig = new CacheConfig(TimeUnit.DAYS.toMillis(1), 0);

    /**
     * 构造函数
     *
     * @param redisson RedissonClient
     */
    public TtlRedissonCacheManager(RedissonClient redisson) {
        super(redisson);
    }

    /**
     * 构造函数
     *
     * @param redisson redisson
     * @param config   config
     */
    public TtlRedissonCacheManager(RedissonClient redisson, Map<String, ? extends CacheConfig> config) {
        super(redisson, config);
    }

    /**
     * 构造函数
     *
     * @param redisson redisson
     * @param config   config
     * @param codec    codec
     */
    public TtlRedissonCacheManager(RedissonClient redisson, Map<String, ? extends CacheConfig> config, Codec codec) {
        super(redisson, config, codec);
    }

    /**
     * 构造函数
     *
     * @param redisson       redisson
     * @param configLocation configLocation
     */
    public TtlRedissonCacheManager(RedissonClient redisson, String configLocation) {
        super(redisson, configLocation);
    }

    /**
     * 构造函数
     *
     * @param redisson       redisson
     * @param configLocation configLocation
     * @param codec          codec
     */
    public TtlRedissonCacheManager(RedissonClient redisson, String configLocation, Codec codec) {
        super(redisson, configLocation, codec);
    }

    @Override
    protected CacheConfig createDefaultConfig() {
        return defaultCacheConfig;
    }

}
