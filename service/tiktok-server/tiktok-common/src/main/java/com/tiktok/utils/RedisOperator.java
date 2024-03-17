package com.tiktok.utils;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类.
 *
 * @author lzc
 * @version 1.0
 */
@Component
public class RedisOperator {

    @Resource(name = "stringRedisTemplate")
    private StringRedisTemplate redisTemplate;

    /**
     * 判断key是否存在.
     *
     * @param key 键
     * @return key存在为true, 否则false
     */
    public boolean keyIsExist(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 返回给定key的剩余生存时间.
     *
     * @param key 键
     * @return 生存时间
     */
    public long ttl(String key) {
        return redisTemplate.getExpire(key);
    }

    /**
     * 设置过期时间, 单位秒.
     *
     * @param key 键
     */
    public void expire(String key, long timeout) {
        redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * key自增.
     *
     * @param key 键
     * @param delta 增加的值
     * @return 自增后的值
     */
    public long increment(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 哈希表字段自增.
     *
     * @param name 字段
     * @param key 键
     * @param delta 增量
     * @return 增加后的值
     */
    public long incrementHash(String name, String key, long delta) {
        return redisTemplate.opsForHash().increment(name, key, delta);
    }

    /**
     * 哈希表字段自减.
     *
     * @param name 字段
     * @param key 键
     * @param delta 减量
     * @return 减少后的值
     */
    public long decrementHash(String name, String key, long delta) {
        delta = delta * (-1);
        return redisTemplate.opsForHash().increment(name, key, delta);
    }

    /**
     * 为哈希表字段设置值.
     *
     * @param name 字段
     * @param key 键
     * @param value 值
     */
    public void setHashValue(String name, String key, String value) {
        redisTemplate.opsForHash().put(name, key, value);
    }

    /**
     * 获取哈希表字段的值.
     *
     * @param name 字段
     * @param key 减
     * @return 值
     */
    public String getHashValue(String name, String key) {
        return (String)redisTemplate.opsForHash().get(name, key);
    }

    /**
     * key自减.
     *
     * @param key 键
     * @param delta 减少的值
     * @return 自减后的值
     */
    public long decrement(String key, long delta) {
        return redisTemplate.opsForValue().decrement(key, delta);
    }

    /**
     * 查找所有符合给定模式pattern的key.
     *
     * @param pattern 匹配模式
     * @return 符合给定模式pattern的key的集合
     */
    public Set<String> keys(String pattern) {
        return redisTemplate.keys(pattern);
    }

    /**
     * 删除key.
     *
     * @param key 键
     */
    public void del(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 设置一个key-value.
     *
     * @param key 键
     * @param value 值
     */
    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置键值对同时指定过期时间.
     *
     * @param key 键
     * @param value 值
     * @param timeout 过期时间, 单位s
     *
     */
    public void set(String key, String value, long timeout) {
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }

    /**
     * 设置键值对同时指定60s过期时间, key不存在时操作才有效.
     *
     * @param key 键
     * @param value 值
     */
    public void setnx60s(String key, String value) {
        redisTemplate.opsForValue().setIfAbsent(key, value, 60, TimeUnit.SECONDS);
    }

    /**
     * 如果key不存在, 则设置, 如果存在, 则报错.
     *
     * @param key 键
     * @param value 值
     */
    public void setnx(String key, String value) {
        redisTemplate.opsForValue().setIfAbsent(key, value);
    }

    /**
     * 返回key所关联的字符串值.
     *
     * @param key 键
     * @return 值
     */
    public String get(String key) {
        return (String)redisTemplate.opsForValue().get(key);
    }

    /**
     * 批量查询.
     *
     * @param keys 多个键
     * @return 多个键的值.
     */
    public List<String> mget(List<String> keys) {
        return redisTemplate.opsForValue().multiGet(keys);
    }

    /**
     * 将哈希表的field的值设为value.
     *
     * @param key 键
     * @param field 字段
     * @param value 值
     */
    public void hset(String key, String field, Object value) {
        redisTemplate.opsForHash().put(key, field, value);
    }

    /**
     * 返回哈希表中给定域field的值.
     *
     * @param key 键
     * @param field 字段
     * @return 给定域field的值
     */
    public String hget(String key, String field) {
        return (String) redisTemplate.opsForHash().get(key, field);
    }

    /**
     * 删除哈希表中的一个或多个指定域, 不存在的域将被忽略.
     *
     * @param key 键
     * @param fields 多个字段
     */
    public void hdel(String key, Object... fields) {
        redisTemplate.opsForHash().delete(key, fields);
    }

    /**
     * 返回哈希表中, 所有的域和值.
     *
     * @param key 键
     * @return 所有的域和值
     */
    public Map<Object, Object> hgetall(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 将一个值插入到列表的表头.
     *
     * @param key 键
     * @param value 值
     * @return 列表的长度
     */
    public long lpush(String key, String value) {
        return redisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * 移除并返回列表头元素.
     *
     * @param key 键
     * @return 列表头元素
     */
    public String lpop(String key) {
        return (String)redisTemplate.opsForList().leftPop(key);
    }

    /**
     * 将一个值插入到列表的表尾.
     *
     * @param key 键
     * @param value 值
     * @return 列表的长度
     */
    public long rpush(String key, String value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }
}