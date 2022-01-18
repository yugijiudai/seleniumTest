package com.lml.selenium.factory;

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
        return new FluentWait<>(SeleniumFactory.getDriver())
                .withTimeout(Duration.ofMillis(15000))
                .pollingEvery(Duration.ofMillis(600))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class)
                .ignoring(NullPointerException.class);
    }
}
