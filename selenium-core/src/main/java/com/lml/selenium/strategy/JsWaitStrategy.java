package com.lml.selenium.strategy;

/**
 * @author yugi
 * @apiNote js等待的策略
 * @since 2022-08-09
 */
public interface JsWaitStrategy {

    /**
     * 使用自定义的js脚本来进行等待
     *
     * @param script      等待的脚本
     * @param maxWaitTime 最长等待时间(毫秒)
     * @param interval    每次轮询间隔的时间(毫秒)
     */
    void waitLoadByJs(String script, long maxWaitTime, Integer interval);

}
