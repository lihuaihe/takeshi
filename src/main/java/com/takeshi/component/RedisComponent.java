package com.takeshi.component;

import cn.hutool.core.util.ArrayUtil;
import com.takeshi.util.GsonUtil;
import com.takeshi.util.ZonedDateTimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Range;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * RedisUtils
 *
 * @author 七濑武【Nanase Takeshi】
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisComponent {

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 获取 StringRedisTemplate
     *
     * @return StringRedisTemplate
     */
    public StringRedisTemplate redisTemplate() {
        return stringRedisTemplate;
    }

    /**
     * 字符串
     *
     * @param key key
     * @return BoundValueOperations
     */
    public BoundValueOperations<String, String> boundValueOps(String key) {
        return stringRedisTemplate.boundValueOps(key);
    }

    /**
     * 写入缓存
     *
     * @param key   key
     * @param value value
     */
    public void save(String key, String value) {
        this.boundValueOps(key).set(value);
    }

    /**
     * 写入缓存设置失效时间
     *
     * @param key        key
     * @param value      value
     * @param expireTime 秒数
     */
    public void save(String key, String value, long expireTime) {
        this.boundValueOps(key).set(value, expireTime, TimeUnit.SECONDS);
    }

    /**
     * 写入缓存设置失效时间和时间单位
     *
     * @param key        key
     * @param value      value
     * @param expireTime 失效时间
     * @param timeUnit   时间单位
     */
    public void save(String key, String value, long expireTime, TimeUnit timeUnit) {
        this.boundValueOps(key).set(value, expireTime, timeUnit);
    }

    /**
     * 写入缓存设置失效时间和时间单位
     *
     * @param key     key
     * @param value   value
     * @param timeout 失效时间
     */
    public void save(String key, String value, Duration timeout) {
        this.boundValueOps(key).set(value, timeout);
    }

    /**
     * 如果绑定键不存在，则设置绑定键以保存字符串写入缓存
     *
     * @param key   key
     * @param value value
     * @return boolean
     */
    public Boolean saveIfAbsent(String key, String value) {
        return this.boundValueOps(key).setIfAbsent(value);
    }

    /**
     * 如果绑定键不存在，则设置绑定键以保存字符串写入缓存设置失效时间
     *
     * @param key        key
     * @param value      value
     * @param expireTime 秒数
     * @return boolean
     */
    public Boolean saveIfAbsent(String key, String value, long expireTime) {
        return this.boundValueOps(key).setIfAbsent(value, expireTime, TimeUnit.SECONDS);
    }

    /**
     * 如果绑定键不存在，则设置绑定键以保存字符串写入缓存设置失效时间和时间单位
     *
     * @param key        key
     * @param value      value
     * @param expireTime 失效时间
     * @param timeUnit   时间单位
     * @return boolean
     */
    public Boolean saveIfAbsent(String key, String value, long expireTime, TimeUnit timeUnit) {
        return this.boundValueOps(key).setIfAbsent(value, expireTime, timeUnit);
    }

    /**
     * 如果绑定键不存在，则设置绑定键以保存字符串写入缓存设置失效时间和时间单位
     *
     * @param key     key
     * @param value   value
     * @param timeout 失效时间
     * @return boolean
     */
    public Boolean saveIfAbsent(String key, String value, Duration timeout) {
        return this.boundValueOps(key).setIfAbsent(value, timeout);
    }

    /**
     * 如果绑定键存在，则设置绑定键以保存字符串写入缓存
     *
     * @param key   key
     * @param value value
     * @return boolean
     */
    public Boolean saveIfPresent(String key, String value) {
        return this.boundValueOps(key).setIfPresent(value);
    }

    /**
     * 如果绑定键存在，则设置绑定键以保存字符串写入缓存设置失效时间
     *
     * @param key        key
     * @param value      value
     * @param expireTime 秒数
     * @return boolean
     */
    public Boolean saveIfPresent(String key, String value, long expireTime) {
        return this.boundValueOps(key).setIfPresent(value, expireTime, TimeUnit.SECONDS);
    }

    /**
     * 如果绑定键存在，则设置绑定键以保存字符串写入缓存设置失效时间和时间单位
     *
     * @param key        key
     * @param value      value
     * @param expireTime 失效时间
     * @param timeUnit   时间单位
     * @return boolean
     */
    public Boolean saveIfPresent(String key, String value, long expireTime, TimeUnit timeUnit) {
        return this.boundValueOps(key).setIfPresent(value, expireTime, timeUnit);
    }

    /**
     * 如果绑定键存在，则设置绑定键以保存字符串写入缓存设置失效时间和时间单位
     *
     * @param key     key
     * @param value   value
     * @param timeout 失效时间
     * @return boolean
     */
    public Boolean saveIfPresent(String key, String value, Duration timeout) {
        return this.boundValueOps(key).setIfPresent(value, timeout);
    }

    /**
     * 写入缓存设置失效时间到指定时间
     *
     * @param key      key
     * @param value    value
     * @param dateTime 希望在哪个时间失效，毫秒数
     */
    public void saveToDateTime(String key, String value, long dateTime) {
        this.boundValueOps(key).set(value, Duration.between(Instant.now(), Instant.ofEpochMilli(dateTime)));
    }

    /**
     * 写入缓存设置失效时间到指定时间
     *
     * @param key     key
     * @param value   value
     * @param timeout 希望在哪个时间失效
     */
    public void saveToDateTime(String key, String value, Instant timeout) {
        this.boundValueOps(key).set(value, Duration.between(Instant.now(), timeout));
    }

    /**
     * 写入缓存设置失效时间到当天结束时间，例如：2023-04-23 23:59:59
     *
     * @param key   key
     * @param value value
     */
    public void saveToEndOfDay(String key, String value) {
        this.boundValueOps(key).set(value, ZonedDateTimeUtil.untilEndOfDay());
    }

    /**
     * 写入缓存设置失效时间到本周日的结束时间，例如周日时间是：2023-04-23，那么周日结束时间就是 2023-04-23 23:59:59
     *
     * @param key   key
     * @param value value
     */
    public void saveToEndOfWeek(String key, String value) {
        this.boundValueOps(key).set(value, ZonedDateTimeUtil.untilEndOfWeek());
    }

    /**
     * 写入缓存设置失效时间到本月的结束时间，例如本月最后一天时间是：2023-04-30，那么本月结束时间就是 2023-04-30 23:59:59
     *
     * @param key   key
     * @param value value
     */
    public void saveToEndOfMonth(String key, String value) {
        this.boundValueOps(key).set(value, ZonedDateTimeUtil.untilEndOfMonth());
    }

    /**
     * 写入缓存设置失效时间到本年的结束时间，例如本年最后一天时间是：2023-12-31，那么本年结束时间就是 2023-12-31 23:59:59
     *
     * @param key   key
     * @param value value
     */
    public void saveToEndOfYear(String key, String value) {
        this.boundValueOps(key).set(value, ZonedDateTimeUtil.untilEndOfYear());
    }

    /**
     * 读取缓存
     *
     * @param key key
     * @return value
     */
    public String get(String key) {
        return this.boundValueOps(key).get();
    }

    /**
     * 读取缓存，JSON字符串转为实体类对象
     *
     * @param key       key
     * @param beanClass beanClass
     * @param <T>       T
     * @return T
     */
    public <T> T get(String key, Class<T> beanClass) {
        return GsonUtil.fromJson(this.boundValueOps(key).get(), beanClass);
    }

    /**
     * 获取key的剩余有效时间
     *
     * @param key key
     * @return 剩余有效时间（秒）
     */
    public Long getExpire(String key) {
        return stringRedisTemplate.getExpire(key);
    }

    /**
     * 获取key的剩余有效时间，并将时间转换成指定的时间单位返回
     *
     * @param key      key
     * @param timeUnit 指定时间单位
     * @return 剩余有效时间
     */
    public Long getExpire(String key, TimeUnit timeUnit) {
        return stringRedisTemplate.getExpire(key, timeUnit);
    }

    /**
     * 批量删除对应的key
     *
     * @param keys keys
     */
    public void delete(Collection<String> keys) {
        stringRedisTemplate.delete(keys);
    }

    /**
     * 批量删除对应的key
     *
     * @param keys keys
     */
    public void delete(String... keys) {
        stringRedisTemplate.delete(Arrays.asList(keys));
    }

    /**
     * 删除对应的key
     *
     * @param key key
     */
    public void delete(String key) {
        if (hasKey(key)) {
            stringRedisTemplate.delete(key);
        }
    }

    /**
     * 查询所有匹配的key
     *
     * @param pattern 正则
     * @return 匹配到的key集合
     */
    public Set<String> keys(String pattern) {
        return stringRedisTemplate.keys(pattern);
    }

    /**
     * 判断缓存中是否有对应的key
     *
     * @param key key
     * @return boolean
     */
    public Boolean hasKey(String key) {
        return stringRedisTemplate.hasKey(key);
    }

    /**
     * 将绑定键下存储为字符串值的整数值增加一
     *
     * @param key key
     * @return 在管道/事务中使用时为空
     */
    public Long increment(String key) {
        return this.boundValueOps(key).increment();
    }

    /**
     * 将绑定键下存储为字符串值的整数值增加{@code delta}
     *
     * @param key   key
     * @param delta delta
     * @return 在管道/事务中使用时为空
     */
    public Long increment(String key, long delta) {
        return this.boundValueOps(key).increment(delta);
    }

    /**
     * 将绑定键下存储为字符串值的浮点数值增加{@code delta}
     *
     * @param key   key
     * @param delta delta
     * @return 在管道/事务中使用时为空
     */
    public Double increment(String key, double delta) {
        return this.boundValueOps(key).increment(delta);
    }

    /**
     * 将绑定键下存储为字符串值的整数值减一
     *
     * @param key key
     * @return 在管道/事务中使用时为空
     */
    public Long decrement(String key) {
        return this.boundValueOps(key).decrement();
    }

    /**
     * 将绑定键下存储为字符串值的整数值减{@code delta}
     *
     * @param key   key
     * @param delta delta
     * @return 在管道/事务中使用时为空
     */
    public Long decrement(String key, long delta) {
        return this.boundValueOps(key).decrement(delta);
    }

    /**
     * 将value附加到绑定键
     *
     * @param key   key
     * @param value value
     * @return 在管道/事务中使用时为空
     */
    public Integer append(String key, String value) {
        return this.boundValueOps(key).append(value);
    }

    /**
     * 设置key的生存时间
     *
     * @param key     key
     * @param timeout 失效时间
     * @param unit    时间单位
     */
    public void expire(String key, long timeout, TimeUnit unit) {
        this.boundValueOps(key).expire(timeout, unit);
    }

    /**
     * 设置key的生存时间
     *
     * @param key     key
     * @param timeout 失效时间
     */
    public void expire(String key, Instant timeout) {
        this.boundValueOps(key).expire(Duration.between(Instant.now(), timeout));
    }

    /**
     * 设置key的生存时间
     *
     * @param key     key
     * @param timeout 失效时间
     */
    public void expire(String key, Duration timeout) {
        this.boundValueOps(key).expire(timeout);
    }

    /**
     * 设置key的生存时间，失效时间到当天结束时间，例如：2023-04-23 23:59:59
     *
     * @param key key
     */
    public void expireAtEndOfDay(String key) {
        this.boundValueOps(key).expire(ZonedDateTimeUtil.untilEndOfDay());
    }

    /**
     * 设置key的生存时间，失效时间到本周日的结束时间，例如周日时间是：2023-04-23，那么周日结束时间就是 2023-04-23 23:59:59
     *
     * @param key key
     */
    public void expireAtEndOfWeek(String key) {
        this.boundValueOps(key).expire(ZonedDateTimeUtil.untilEndOfWeek());
    }

    /**
     * 设置key的生存时间，失效时间到本月的结束时间，例如本月最后一天时间是：2023-04-30，那么本月结束时间就是 2023-04-30 23:59:59
     *
     * @param key key
     */
    public void expireAtEndOfMonth(String key) {
        this.boundValueOps(key).expire(ZonedDateTimeUtil.untilEndOfMonth());
    }

    /**
     * 设置key的生存时间，失效时间到本年的结束时间，例如本年最后一天时间是：2023-12-31，那么本年结束时间就是 2023-12-31 23:59:59
     *
     * @param key key
     */
    public void expireAtEndOfYear(String key) {
        this.boundValueOps(key).expire(ZonedDateTimeUtil.untilEndOfYear());
    }

    /**
     * hash哈希
     *
     * @param key key
     * @return BoundHashOperations
     */
    public BoundHashOperations<String, String, String> boundHashOps(String key) {
        return stringRedisTemplate.boundHashOps(key);
    }

    /**
     * 哈希 添加
     *
     * @param key     key
     * @param hashKey hashKey
     * @param value   value
     */
    public void hashPut(String key, String hashKey, String value) {
        this.boundHashOps(key).put(hashKey, value);
    }

    /**
     * 哈希 添加
     *
     * @param key key
     * @param map map
     */
    public void hashPutAll(String key, Map<String, String> map) {
        this.boundHashOps(key).putAll(map);
    }

    /**
     * 哈希 自增
     *
     * @param key     key
     * @param hashKey hashKey
     * @param delta   delta
     * @return Long
     */
    public Long hashIncrement(String key, String hashKey, long delta) {
        return this.boundHashOps(key).increment(hashKey, delta);
    }

    /**
     * 哈希 自增
     *
     * @param key     key
     * @param hashKey hashKey
     * @param delta   delta
     * @return Double
     */
    public Double hashIncrement(String key, String hashKey, double delta) {
        return this.boundHashOps(key).increment(hashKey, delta);
    }

    /**
     * 哈希 添加，仅当 hashKey 不存在时，才设置哈希 hashKey 的 value
     *
     * @param key     key
     * @param hashKey hashKey
     * @param value   value
     * @return boolean
     */
    public Boolean hashPutIfAbsent(String key, String hashKey, String value) {
        return this.boundHashOps(key).putIfAbsent(hashKey, value);
    }

    /**
     * 哈希 在绑定键处获取整个哈希
     *
     * @param key key
     * @return Map
     */
    public Map<String, String> hashEntries(String key) {
        return this.boundHashOps(key).entries();
    }

    /**
     * 哈希 在绑定键处获取整个哈希
     *
     * @param key       key
     * @param beanClass beanClass
     * @param <T>       T
     * @return Map
     */
    public <T> Map<String, T> hashEntries(String key, Class<T> beanClass) {
        return Optional.ofNullable(this.boundHashOps(key).entries())
                       .map(m -> m.entrySet()
                                  .stream()
                                  .collect(Collectors.toMap(Map.Entry::getKey, item -> GsonUtil.fromJson(item.getValue(), beanClass))))
                       .orElse(null);
    }

    /**
     * 哈希获取数据
     *
     * @param key     key
     * @param hashKey hashKey
     * @return String
     */
    public String hashGet(String key, String hashKey) {
        return this.boundHashOps(key).get(hashKey);
    }

    /**
     * 哈希获取数据
     *
     * @param key       key
     * @param hashKey   hashKey
     * @param beanClass beanClass
     * @param <T>       T
     * @return T
     */
    public <T> T hashGet(String key, String hashKey, Class<T> beanClass) {
        return GsonUtil.fromJson(this.boundHashOps(key).get(hashKey), beanClass);
    }

    /**
     * 哈希根据key删除数据
     *
     * @param key      key
     * @param hashKeys hashKeys
     */
    public void hashDelete(String key, String... hashKeys) {
        this.boundHashOps(key).delete(Arrays.stream(hashKeys).toArray());
    }

    /**
     * list列表
     *
     * @param key key
     * @return BoundListOperations
     */
    public BoundListOperations<String, String> boundListOps(String key) {
        return stringRedisTemplate.boundListOps(key);
    }

    /**
     * list列表添加
     *
     * @param key   key
     * @param value value
     */
    public void listPush(String key, String value) {
        this.boundListOps(key).rightPush(value);
    }

    /**
     * list列表获取
     *
     * @param key   key
     * @param start start
     * @param end   end
     * @return 在管道/事务中使用时为空
     */
    public List<String> listRange(String key, long start, long end) {
        return this.boundListOps(key).range(start, end);
    }

    /**
     * list列表获取
     *
     * @param key       key
     * @param start     start
     * @param end       end
     * @param beanClass beanClass
     * @param <T>       T
     * @return 在管道/事务中使用时为空
     */
    public <T> List<T> listRange(String key, long start, long end, Class<T> beanClass) {
        return Optional.ofNullable(this.boundListOps(key).range(start, end))
                       .map(l -> l.stream().map(item -> GsonUtil.fromJson(item, beanClass)).collect(Collectors.toList()))
                       .orElse(null);
    }

    /**
     * Set集合
     *
     * @param key key
     * @return BoundSetOperations
     */
    public BoundSetOperations<String, String> boundSetOps(String key) {
        return stringRedisTemplate.boundSetOps(key);
    }

    /**
     * Set集合添加
     *
     * @param key    key
     * @param values values
     */
    public void setAdd(String key, String... values) {
        if (ArrayUtil.isNotEmpty(values)) {
            this.boundSetOps(key).add(values);
        }
    }

    /**
     * Set集合移除元素
     *
     * @param key    key
     * @param values values
     * @return 在管道/事务中使用时为空
     */
    public Long setRemove(String key, String... values) {
        if (ArrayUtil.isEmpty(values)) {
            return 0L;
        }
        return this.boundSetOps(key).remove(Arrays.stream(values).toArray());
    }

    /**
     * Set集合获取
     *
     * @param key key
     * @return Set
     */
    public Set<String> setMembers(String key) {
        return this.boundSetOps(key).members();
    }

    /**
     * Set集合获取
     *
     * @param key       key
     * @param beanClass beanClass
     * @param <T>       T
     * @return Set
     */
    public <T> Set<T> setMembers(String key, Class<T> beanClass) {
        return Optional.ofNullable(this.boundSetOps(key).members())
                       .map(s -> s.stream().map(item -> GsonUtil.fromJson(item, beanClass)).collect(Collectors.toSet()))
                       .orElse(null);
    }

    /**
     * 检查Set集合中是否包含value
     *
     * @param key   key
     * @param value value
     * @return boolean
     */
    public Boolean setIsMembers(String key, String value) {
        return this.boundSetOps(key).isMember(value);
    }

    /**
     * ZSet有序集合
     *
     * @param key key
     * @return BoundZSetOperations
     */
    public BoundZSetOperations<String, String> boundZSetOps(String key) {
        return stringRedisTemplate.boundZSetOps(key);
    }

    /**
     * 有序集合添加
     *
     * @param key   key
     * @param value value
     * @param score score
     */
    public void zSetAdd(String key, String value, double score) {
        this.boundZSetOps(key).add(value, score);
    }

    /**
     * 有序集合删除
     * 从排序集中删除值。返回已移除元素的数量。
     *
     * @param key    key
     * @param values values
     * @return 在管道/事务中使用时为空
     */
    public Long zSetRemove(String key, String... values) {
        return this.boundZSetOps(key).remove(Arrays.stream(values).toArray());
    }

    /**
     * 有序集合添加
     * 从以键为绑定键的排序集中获取具有值的元素的分数
     *
     * @param key   key
     * @param value value
     * @return 在管道/事务中使用时为空
     */
    public Double zSetScore(String key, String value) {
        return this.boundZSetOps(key).score(value);
    }

    /**
     * 有序集合添加分数
     *
     * @param key   key
     * @param value value
     * @param delta delta
     */
    public void zSetIncrementScore(String key, String value, double delta) {
        this.boundZSetOps(key).incrementScore(value, delta);
    }

    /**
     * 有序集合获取
     *
     * @param key   key
     * @param start start
     * @param end   end
     * @return Set
     */
    public Set<String> zSetRange(String key, long start, long end) {
        return this.boundZSetOps(key).range(start, end);
    }

    /**
     * 有序集合获取
     *
     * @param key       key
     * @param start     start
     * @param end       end
     * @param beanClass beanClass
     * @param <T>       T
     * @return Set
     */
    public <T> Set<T> zSetRange(String key, long start, long end, Class<T> beanClass) {
        return Optional.ofNullable(this.boundZSetOps(key).range(start, end))
                       .map(s -> s.stream().map(item -> GsonUtil.fromJson(item, beanClass)).collect(Collectors.toSet()))
                       .orElse(null);
    }

    /**
     * 有序集合删除
     * 使用绑定键从排序集中删除开始和结束之间范围内的元素
     *
     * @param key   key
     * @param start start
     * @param end   end
     * @return 在管道/事务中使用时为空
     */
    public Long zSetRemoveRange(String key, long start, long end) {
        return this.boundZSetOps(key).removeRange(start, end);
    }

    /**
     * 有序集合删除
     * 使用绑定键从排序集中删除 org.springframework.data.redis.connection.RedisZSetCommands.Range 中的元素。
     *
     * @param key   key
     * @param range range
     * @return 在管道/事务中使用时为空
     */
    public Long zSetRemoveRange(String key, Range<String> range) {
        return this.boundZSetOps(key).removeRangeByLex(range);
    }

    /**
     * 有序集合获取
     *
     * @param key   key
     * @param start start
     * @param end   end
     * @return Set
     */
    public Set<String> zSetReverseRange(String key, long start, long end) {
        return this.boundZSetOps(key).reverseRange(start, end);
    }

    /**
     * 有序集合获取
     *
     * @param key       key
     * @param start     start
     * @param end       end
     * @param beanClass beanClass
     * @param <T>       T
     * @return Set
     */
    public <T> Set<T> zSetReverseRange(String key, long start, long end, Class<T> beanClass) {
        return Optional.ofNullable(this.boundZSetOps(key).reverseRange(start, end))
                       .map(s -> s.stream().map(item -> GsonUtil.fromJson(item, beanClass)).collect(Collectors.toSet()))
                       .orElse(null);
    }

    /**
     * 有序集合获取
     *
     * @param key key
     * @param min min
     * @param max max
     * @return Set
     */
    public Set<String> zSetRangeByScore(String key, double min, double max) {
        return this.boundZSetOps(key).rangeByScore(min, max);
    }

    /**
     * 有序集合获取
     *
     * @param key       key
     * @param min       min
     * @param max       max
     * @param beanClass beanClass
     * @param <T>       T
     * @return Set
     */
    public <T> Set<T> zSetRangeByScore(String key, double min, double max, Class<T> beanClass) {
        return Optional.ofNullable(this.boundZSetOps(key).rangeByScore(min, max))
                       .map(s -> s.stream().map(item -> GsonUtil.fromJson(item, beanClass)).collect(Collectors.toSet()))
                       .orElse(null);
    }

    /**
     * 有序集合删除
     * 使用绑定键从排序集中删除分数在 min 和 max 之间的元素。
     *
     * @param key key
     * @param min min
     * @param max max
     * @return 在管道/事务中使用时为空
     */
    public Long zSetRemoveRange(String key, double min, double max) {
        return this.boundZSetOps(key).removeRangeByScore(min, max);
    }

    /**
     * 有序集合获取
     *
     * @param key key
     * @param min min
     * @param max max
     * @return Set
     */
    public Set<String> zSetReverseRangeByScore(String key, double min, double max) {
        return this.boundZSetOps(key).reverseRangeByScore(min, max);
    }

    /**
     * 有序集合获取
     *
     * @param key       key
     * @param min       min
     * @param max       max
     * @param beanClass beanClass
     * @param <T>       T
     * @return Set
     */
    public <T> Set<T> zSetReverseRangeByScore(String key, double min, double max, Class<T> beanClass) {
        return Optional.ofNullable(this.boundZSetOps(key).reverseRangeByScore(min, max))
                       .map(s -> s.stream().map(item -> GsonUtil.fromJson(item, beanClass)).collect(Collectors.toSet()))
                       .orElse(null);
    }

    /**
     * 有序集合获取排名
     *
     * @param key   key
     * @param value value
     * @return 在管道/事务中使用时为空
     */
    public Long zSetRank(String key, String value) {
        return this.boundZSetOps(key).rank(value);
    }

    /**
     * 有序集合获取排名
     *
     * @param key   key
     * @param value value
     * @return 在管道/事务中使用时为空
     */
    public Long zSetReverseRank(String key, String value) {
        return this.boundZSetOps(key).reverseRank(value);
    }

    /**
     * 有序集合获取排名
     *
     * @param key   key
     * @param start start
     * @param end   end
     * @return Set
     */
    public Set<ZSetOperations.TypedTuple<String>> zSetRankWithScore(String key, long start, long end) {
        return this.boundZSetOps(key).rangeWithScores(start, end);
    }

    /**
     * 有序集合获取排名
     *
     * @param key   key
     * @param start start
     * @param end   end
     * @return Set
     */
    public Set<ZSetOperations.TypedTuple<String>> zSetReverseRangeWithScores(String key, long start, long end) {
        return this.boundZSetOps(key).reverseRangeWithScores(start, end);
    }

    /**
     * 有序集合获取排名
     *
     * @param key key
     * @param min min
     * @param max max
     * @return Set
     */
    public Set<ZSetOperations.TypedTuple<String>> zSetRangeByScoreWithScores(String key, long min, long max) {
        return this.boundZSetOps(key).rangeByScoreWithScores(min, max);
    }

    /**
     * 有序集合获取排名
     *
     * @param key key
     * @param min min
     * @param max max
     * @return Set
     */
    public Set<ZSetOperations.TypedTuple<String>> zSetReverseRangeByScoreWithScores(String key, long min, long max) {
        return this.boundZSetOps(key).reverseRangeByScoreWithScores(min, max);
    }

    /**
     * 地理操作
     *
     * @param key key
     * @return BoundGeoOperations
     */
    public BoundGeoOperations<String, String> boundGeoOps(String key) {
        return stringRedisTemplate.boundGeoOps(key);
    }

    /**
     * 将指定的地理空间位置（经度，纬度，名称）添加到指定key中
     *
     * @param key    key
     * @param lon    经度
     * @param lat    维度
     * @param member 名称
     * @return 添加的元素数。在管道/事务中使用时为空
     */
    public Long geoAdd(String key, double lon, double lat, String member) {
        return this.boundGeoOps(key).add(new Point(lon, lat), member);
    }

    /**
     * 将 {@link RedisGeoCommands.GeoLocation} 添加到指定key中
     *
     * @param key      key
     * @param location 位置
     * @return 添加的元素数。在管道/事务中使用时为空
     */
    public Long geoAdd(String key, RedisGeoCommands.GeoLocation<String> location) {
        return this.boundGeoOps(key).add(location);
    }

    /**
     * 将成员/Point对的Map添加到指定key中
     *
     * @param key                 key
     * @param memberCoordinateMap memberCoordinateMap
     * @return 添加的元素数。在管道/事务中使用时为空
     */
    public Long geoAdd(String key, Map<String, Point> memberCoordinateMap) {
        return this.boundGeoOps(key).add(memberCoordinateMap);
    }

    /**
     * 将 {@link RedisGeoCommands.GeoLocation} 集合添加到指定key中
     *
     * @param key       key
     * @param locations locations
     * @return 添加的元素数。在管道/事务中使用时为空
     */
    public Long geoAdd(String key, Iterable<RedisGeoCommands.GeoLocation<String>> locations) {
        return this.boundGeoOps(key).add(locations);
    }

    /**
     * 获取一个或多个成员的位置的 {@link Point} 表示
     *
     * @param key     key
     * @param members members
     * @return 坐标集合
     */
    public List<Point> geoPosition(String key, String... members) {
        return this.boundGeoOps(key).position(members);
    }

    /**
     * 获取 member1 和 member2 之间的距离
     *
     * @param key     key
     * @param member1 member1
     * @param member2 member2
     * @return 距离
     */
    public Distance geoDistance(String key, String member1, String member2) {
        return this.boundGeoOps(key).distance(member1, member2);
    }

    /**
     * 获取给定指标中 member1 和 member2 之间的距离
     *
     * @param key     key
     * @param member1 member1
     * @param member2 member2
     * @param metric  metric
     * @return 距离
     */
    public Distance geoDistance(String key, String member1, String member2, Metric metric) {
        return this.boundGeoOps(key).distance(member1, member2, metric);
    }

    /**
     * 获取以给定经纬度为中心的边界内的成员，默认距离单位为 {@link Metrics#KILOMETERS}
     *
     * @param key      key
     * @param lon      经度
     * @param lat      维度
     * @param distance 距离
     * @return GeoResults
     */
    public GeoResults<RedisGeoCommands.GeoLocation<String>> geoRadius(String key, double lon, double lat,
                                                                      double distance) {
        return this.boundGeoOps(key).radius(new Circle(new Point(lon, lat), new Distance(distance, Metrics.KILOMETERS)));
    }

    /**
     * 获取以给定经纬度为中心的边界内的成员
     *
     * @param key      key
     * @param lon      经度
     * @param lat      维度
     * @param distance 距离
     * @param metrics  距离单位
     * @return GeoResults
     */
    public GeoResults<RedisGeoCommands.GeoLocation<String>> geoRadius(String key, double lon, double lat,
                                                                      double distance, Metrics metrics) {
        return this.boundGeoOps(key).radius(new Circle(new Point(lon, lat), new Distance(distance, metrics)));
    }

    /**
     * 获取指定个数内的以给定经纬度为中心的边界内的成员
     *
     * @param key      key
     * @param lon      经度
     * @param lat      维度
     * @param distance 距离
     * @param metrics  距离单位
     * @param count    指定的个数
     * @return GeoResults
     */
    public GeoResults<RedisGeoCommands.GeoLocation<String>> geoRadius(String key, double lon, double lat,
                                                                      double distance, Metrics metrics, long count) {
        Circle circle = new Circle(new Point(lon, lat), new Distance(distance, metrics));
        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().includeDistance().includeCoordinates().sortAscending().limit(count);
        return this.boundGeoOps(key).radius(circle, args);
    }

    /**
     * 获取由成员坐标定义的圆内的成员，并应用Metric给定半径
     *
     * @param key      key
     * @param member   成员
     * @param distance 距离
     * @return GeoResults
     */
    public GeoResults<RedisGeoCommands.GeoLocation<String>> geoRadius(String key, String member, double distance) {
        return this.boundGeoOps(key).radius(member, new Distance(distance, Metrics.KILOMETERS));
    }

    /**
     * 移除成员
     *
     * @param key     key
     * @param members members
     * @return 删除的元素数。在管道/事务中使用时为空
     */
    public Long geoRemove(String key, String... members) {
        return this.boundGeoOps(key).remove(members);
    }

    /**
     * 获取一个或多个成员的位置的 Geohash 表示
     *
     * @param key     key
     * @param members members
     * @return 除非在管道事务中使用，否则永远不会为 null
     */
    public List<String> geoHash(String key, String... members) {
        return this.boundGeoOps(key).hash(members);
    }

}
