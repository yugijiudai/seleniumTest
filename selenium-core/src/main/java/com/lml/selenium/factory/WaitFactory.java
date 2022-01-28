package com.lml.selenium.factory;

import com.lml.selenium.dto.SetDto;
import lombok.experimental.UtilityClass;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import java.time.Duration;

/**
 * @author zhonghaowen
 * @apiNote wait工厂
 * @since 2017-05-19
 */
@UtilityClass
public class WaitFactory {

    /**
     * 创建流畅等待的对象,可以根据自己需要来写等待的条件
     * wait.until(webDriver -> ExpectedConditions.frameToBeAvailableAndSwitchToIt(element));
     *
     * @return {@link Wait}
     */
    public Wait<WebDriver> createDefaultWait() {
        SetDto setDto = SeleniumFactory.getSetDto();
        return new FluentWait<>(SeleniumFactory.getDriver())
                .withTimeout(Duration.ofMillis(setDto.getMaxWaitTime()))
                .pollingEvery(Duration.ofMillis(setDto.getInterval()));
        // .ignoring(NoSuchElementException.class)
        // .ignoring(StaleElementReferenceException.class)
        // .ignoring(NullPointerException.class);
    }
}
