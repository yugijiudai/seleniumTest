package com.lml.selenium.strategy;

import com.lml.selenium.exception.FindElementException;
import com.lml.selenium.util.JsUtil;
import com.lml.selenium.util.WebUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yugi
 * @apiNote 通过使用简单的while+sleep来达到轮询效果
 * @since 2022-08-09
 */
@Slf4j
public class JsWaitByLoopStrategy implements JsWaitStrategy {

    @Override
    public void waitLoadByJs(String script, long maxWaitTime, Integer interval) {
        long end = System.currentTimeMillis() + maxWaitTime;
        log.info("执行等待脚本:" + script);
        while (!(Boolean) JsUtil.runJs(script)) {
            if (System.currentTimeMillis() > end) {
                log.warn("脚本:【{}】, 超出最长等待时间{},跳出循环", script, maxWaitTime);
                throw new FindElementException("超出最长等待时间:" + maxWaitTime);
            }
            WebUtil.doWait(interval);
        }
    }
}
