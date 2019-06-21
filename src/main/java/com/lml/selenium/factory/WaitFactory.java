package com.lml.selenium.factory;

import com.lml.selenium.util.WebUtil;
import lombok.experimental.UtilityClass;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import java.time.Duration;
import java.util.NoSuchElementException;

/**
 * @author zhonghaowen
 * @apiNote wait工厂
 * @since 2017-05-19
 */
@UtilityClass
public class WaitFactory {

    /**
     * 默认的wait对象
     *
     * @return {@link Wait}
     */
    public Wait<WebDriver> createDefaultWait() {
        return createWait(15000, 600);
    }

    /**
     * 创建wai对象
     *
     * @param withTimeout  超时的时间
     * @param pollingEvery 轮询的间隔
     * @return {@link Wait}
     */
    private Wait<WebDriver> createWait(int withTimeout, int pollingEvery) {
        return new FluentWait<>(WebUtil.driver)
                .withTimeout(Duration.ofMillis(withTimeout))
                .pollingEvery(Duration.ofMillis(pollingEvery))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class)
                .ignoring(NullPointerException.class);
    }
}
