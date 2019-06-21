package com.lml.selenium.cache;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author yugi
 * @apiNote 缓存类的基类
 * @since 2019-05-30
 */
@Data
@Accessors(chain = true)
public abstract class BaseCacheEntity {

    /**
     * 缓存过期时间(单位:秒)
     */
    protected int expire = 60 * 60 * 24 * 31;

    private String key;

    private String val;

    private String field;

    /**
     * 构建要放在缓存的key
     *
     * @return 返回构件好的key
     */
    public String getGenKey() {
        return keyPrefix() + this.key;
    }

    /**
     * 缓存key的前缀
     *
     * @return 返回key的前缀
     */
    public abstract String keyPrefix();


}
