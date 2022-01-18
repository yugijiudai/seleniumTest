package com.lml.selenium.util;

import cn.hutool.db.nosql.redis.RedisDS;
import com.lml.selenium.cache.BaseCacheEntity;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

/**
 * @author yugi
 * @apiNote redis的工具类
 * @since 2019-05-30
 */
@UtilityClass
@Slf4j
public class RedisUtil {

    /**
     * selenium下的基础key前缀
     */
    private final String BASE_PREFIX = "selenium:";


    /**
     * redis设置key-value
     *
     * @param entity 缓存类
     */
    public void set(BaseCacheEntity entity) {
        try (Jedis jedis = getJedis()) {
            jedis.setex(getSeleniumCacheKey(entity.getGenKey()), entity.getExpire(), entity.getVal());
        }
    }

    /**
     * redis设置key-value
     *
     * @param entity 缓存类
     */
    public void hset(BaseCacheEntity entity) {
        try (Jedis jedis = getJedis()) {
            String key = getSeleniumCacheKey(entity.getGenKey());
            jedis.hset(key, entity.getField(), entity.getVal());
            jedis.expire(key, entity.getExpire());
        }
    }

    /**
     * redis设置key-value
     *
     * @param entity 缓存类
     * @return 返回对应的值
     */
    public String hget(BaseCacheEntity entity) {
        try (Jedis jedis = getJedis()) {
            return jedis.hget(getSeleniumCacheKey(entity.getGenKey()), entity.getField());
        }
    }

    /**
     * 根据key获取value
     *
     * @param entity 缓存类
     * @return 返回对应的value
     */
    public String get(BaseCacheEntity entity) {
        return RedisDS.create().getStr(getSeleniumCacheKey(entity.getGenKey()));
    }


    /**
     * 获取selenium的缓存key
     *
     * @param key 要缓存的key
     * @return 放在redis的key
     */
    private String getSeleniumCacheKey(String key) {
        return BASE_PREFIX + key;
    }

    /**
     * 获取jedis对象
     *
     * @return {@link Jedis}
     */
    private Jedis getJedis() {
        return RedisDS.create().getJedis();
    }

}
