package com.lml.selenium.holder;

import cn.hutool.core.util.IdUtil;
import lombok.experimental.UtilityClass;

/**
 * @author yugi
 * @apiNote 用于存放请求的一些统一数据
 * @since 2023-07-18
 */
@UtilityClass
public final class ReqHolder {

    /**
     * 用来保存traceId
     */
    private final ThreadLocal<String> traceIdHolder = new ThreadLocal<>();


    /**
     * 构建一个唯一的uuid
     *
     * @return 返回构建好的id
     */
    public String buildId() {
        return IdUtil.fastSimpleUUID();
    }

    /**
     * 添加traceId
     */
    public void addTraceId(String traceId) {
        traceIdHolder.set(traceId);
    }

    /**
     * 获取traceId
     */
    public String getTraceId() {
        return traceIdHolder.get();
    }

    /**
     * 清除traceId
     */
    public void removeTraceId() {
        traceIdHolder.remove();
    }

}
