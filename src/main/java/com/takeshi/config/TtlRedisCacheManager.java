package com.takeshi.config;

import cn.hutool.core.util.ObjUtil;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.lang.Nullable;

import java.time.Duration;

/**
 * TtlRedisCacheManager
 *
 * @author 七濑武【Nanase Takeshi】
 */
public class TtlRedisCacheManager extends RedisCacheManager {

    private TtlRedisCacheManager(RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration) {
        super(cacheWriter, defaultCacheConfiguration);
    }

    /**
     * 对cacheNames用#分割，第二个值为缓存时间（单位：秒）<br/>
     * 如果没有#分割则使用{@link TtlRedisCacheManager#defaultInstance}设置的默认缓存时间
     *
     * @param name        must not be {@literal null}.
     * @param cacheConfig can be {@literal null}.
     * @return RedisCache
     */
    @Override
    protected RedisCache createRedisCache(String name, @Nullable RedisCacheConfiguration cacheConfig) {
        String[] split = name.split("#");
        name = split[0];
        if (split.length > 1 && ObjUtil.isNotNull(cacheConfig)) {
            long ttl = Long.parseLong(split[1]);
            cacheConfig = cacheConfig.entryTtl(Duration.ofSeconds(ttl));
        }
        return super.createRedisCache(name, cacheConfig);
    }

    /**
     * TtlRedisCacheManager
     *
     * @param objectMapper objectMapper
     * @param factory      factory
     * @return TtlRedisCacheManager
     */
    public static TtlRedisCacheManager defaultInstance(ObjectMapper objectMapper, RedisConnectionFactory factory) {
        // 复制一个全新的ObjectMapper，避免修改全局配置
        ObjectMapper om = objectMapper.copy();
        // 配置保存到redis中带完全限定类名
        om.activateDefaultTyping(objectMapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        RedisCacheConfiguration config =
                RedisCacheConfiguration.defaultCacheConfig()
                                       // cacheNames没有使用#分隔时，设置默认缓存过期时间
                                       .entryTtl(Duration.ofDays(1))
                                       // 定义用于反/序列化缓存键
                                       .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                                       // 定义用于反/序列化缓存值
                                       .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(om)))
                                       // 禁止缓存NULL值，如果缓存NULL值就抛出异常
                                       .disableCachingNullValues();
        // 使用redis配置，会导致@Cacheable(sync=true)时，同步锁只对cacheName加锁，不是对完整的key进行加锁，建议使用RedissonSpringCacheManager
        return new TtlRedisCacheManager(RedisCacheWriter.lockingRedisCacheWriter(factory), config);
    }

}
