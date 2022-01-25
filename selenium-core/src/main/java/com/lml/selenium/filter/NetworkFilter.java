package com.lml.selenium.filter;

import cn.hutool.json.JSONObject;

/**
 * @author yugi
 * @apiNote 解析网络请求的过滤器
 * @since 2022-01-25
 */
public interface NetworkFilter {

    /**
     * 自定义的过滤规则
     *
     * @param harEntry har包里面的entry对象
     * @return true表示过滤
     */
    boolean filterFunc(JSONObject harEntry);
}
