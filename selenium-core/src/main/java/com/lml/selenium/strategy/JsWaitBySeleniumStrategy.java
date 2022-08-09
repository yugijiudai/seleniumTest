package com.lml.selenium.strategy;

import com.lml.selenium.exception.FindElementException;
import com.lml.selenium.factory.WaitFactory;
import com.lml.selenium.util.JsUtil;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Wait;

import java.time.Duration;

/**
 * @author yugi
 * @apiNote 使用selenium的wait对象来来轮询页面的情况, 直到找到或者超时为止
 * @since 2022-08-09
 */
@Slf4j
public class JsWaitBySeleniumStrategy implements JsWaitStrategy {

    @Override
    public void waitLoadByJs(String script, long maxWaitTime, Integer interval) {
        try {
            Wait<WebDriver> waitDriver = WaitFactory.createDefaultWait(Duration.ofMillis(maxWaitTime), Duration.ofMillis(interval));
            waitDriver.until(driver -> JsUtil.<Boolean>runJs(script));
        }
        catch (Throwable e) {
            log.warn("脚本:【{}】, 超出最长等待时间{},跳出循环", script, maxWaitTime);
            throw new FindElementException("超出最长等待时间:" + maxWaitTime);
        }
    }
}
