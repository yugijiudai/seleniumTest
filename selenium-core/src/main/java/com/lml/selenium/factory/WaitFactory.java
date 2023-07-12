package com.lml.selenium.factory;

import lombok.experimental.UtilityClass;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * @author zhonghaowen
 * @apiNote wait工厂
 * @since 2022-08-09
 */
@UtilityClass
public class WaitFactory {

    /**
     * 创建默认的等待对象
     *
     * @param timeout  超时时间
     * @param interval 轮询时间
     * @return 对应的等待类
     */
    public Wait<WebDriver> createDefaultWait(Duration timeout, Duration interval) {
        return createFluentWait(timeout, interval);
    }


    /**
     * 创建流畅等待的对象,可以根据自己需要来写等待的条件
     * wait.until(webDriver -> ExpectedConditions.frameToBeAvailableAndSwitchToIt(element));
     *
     * @param timeout  超时时间
     * @param interval 轮询时间
     * @return 对应的等待类
     */
    public Wait<WebDriver> createFluentWait(Duration timeout, Duration interval) {
        return new FluentWait<>(SeleniumFactory.getDriverHolder())
                .withTimeout(timeout)
                .pollingEvery(interval);
        // .ignoring(NoSuchElementException.class)
        // .ignoring(StaleElementReferenceException.class)
        // .ignoring(NullPointerException.class);
    }

    /**
     * 创建显示等待
     *
     * @param timeout  超时时间
     * @param interval 轮询时间
     * @return 对应的等待类
     */
    public Wait<WebDriver> createWebDriverWait(Duration timeout, Duration interval) {
        return new WebDriverWait(SeleniumFactory.getDriverHolder(), timeout, interval);
    }

    /**
     * 创建隐式等待(可以用于调试使用,不建议用于生产)
     *
     * @param timeout 最大的超时时间
     */
    public void createImplicitlyWait(Duration timeout) {
        WebDriver driver = SeleniumFactory.getDriverHolder();
        driver.manage().timeouts().implicitlyWait(timeout);
    }

}
