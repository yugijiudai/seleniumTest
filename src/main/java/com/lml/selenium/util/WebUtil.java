package com.lml.selenium.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.setting.dialect.Props;
import com.lml.selenium.dto.EleHandleDto;
import com.lml.selenium.dto.SetDto;
import com.lml.selenium.enums.ClickActionEnum;
import com.lml.selenium.exception.FindElementException;
import com.lml.selenium.factory.EleHandleDtoFactory;
import com.lml.selenium.factory.HandlerFactory;
import com.lml.selenium.handler.element.ElementHandler;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.io.File;
import java.util.List;

/**
 * @author yugi
 * @apiNote 统一操作的工具类
 * @since 2019-05-01
 */
@Slf4j
@UtilityClass
public class WebUtil {

    private ChromeDriverService service;

    public WebDriver driver;

    /**
     * 配置文件映射到的实体类
     */
    @Getter
    private final SetDto setDto = new SetDto();


    static {
        // 初始化处理器
        HandlerFactory.initAllHandler();
        Props props = new Props("application.properties");
        // 配置映射到对应的实体类中
        BeanUtil.copyProperties(props, setDto);
        log.info("初始化成功,配置是:{}", setDto);
    }


    /**
     * 关闭驱动
     */
    public void quitDriver() {
        driver.quit();
        service.stop();
    }


    /**
     * 初始化
     */
    public void webDriverInit() {
        try {
            service = new ChromeDriverService.Builder().usingDriverExecutable(new File(setDto.getDriverPath())).usingAnyFreePort().build();
            service.start();
            driver = new ChromeDriver(service);
            // 窗口最大化
            driver.manage().window().maximize();
            JSWaiter.setDriver(driver);
        }
        catch (Exception e) {
            Assert.fail("初始化失败", e);
        }
    }


    /**
     * 重复查找获取控件的文本
     *
     * @param by 对应的元素
     * @return {@link WebElement}
     */
    public WebElement retryFindAndGetText(By by) {
        return retryingFindAndDoAction(EleHandleDtoFactory.buildGetText(EleHandleDtoFactory.buildCommon(by)));
    }

    /**
     * 重复查找和click，当出现引用的element过时后，重新查找该element
     *
     * @param by 对应的元素
     */
    public void retryFindAndClick(By by) {
        retryingFindAndDoAction(EleHandleDtoFactory.buildClick(ClickActionEnum.BY_TAG_TYPE, EleHandleDtoFactory.buildCommon(by)));
    }

    /**
     * 通过Selenium API进行点击，避免JS点击不成功或者弹出对话框阻塞的问题
     *
     * @param by 对应的元素
     */
    public void retryFindAndClickBySeleniumAPI(By by) {
        retryingFindAndDoAction(EleHandleDtoFactory.buildClick(ClickActionEnum.API, EleHandleDtoFactory.buildCommon(by)));
    }

    /**
     * 执行双击
     *
     * @param by 对应的元素
     */
    public void retryFindAndDoubleClick(By by) {
        retryingFindAndDoAction(EleHandleDtoFactory.buildClick(ClickActionEnum.DOUBLE_CLICK, EleHandleDtoFactory.buildCommon(by)));
    }

    /**
     * 执行右键
     *
     * @param by 对应的元素
     */
    public void retryFindAndRightClick(By by) {
        retryingFindAndDoAction(EleHandleDtoFactory.buildClick(ClickActionEnum.RIGHT_CLICK, EleHandleDtoFactory.buildCommon(by)));
    }

    /**
     * 通过Javascript进行点击，避免Selenium API点击不成功
     *
     * @param by 对应的元素
     */
    public void retryFindAndClickByJS(By by) {
        retryingFindAndDoAction(EleHandleDtoFactory.buildClick(ClickActionEnum.JS, EleHandleDtoFactory.buildCommon(by)));
    }


    /**
     * 鼠标停留在控件上
     *
     * @param by 对应的元素
     */
    public void retryFindAndHover(By by) {
        retryingFindAndDoAction(EleHandleDtoFactory.buildHover(EleHandleDtoFactory.buildCommon(by)));
    }

    /**
     * 清空内容
     *
     * @param by 对应的元素
     */
    public void retryingFindAndClear(By by) {
        retryingFindAndDoAction(EleHandleDtoFactory.buildClear(EleHandleDtoFactory.buildCommon(by)));
    }

    /**
     * 查找并且发送对应的内容
     *
     * @param by   对应的元素
     * @param keys 要发送的key
     */
    public void retryFindAndSendKeys(By by, String keys) {
        retryingFindAndDoAction(EleHandleDtoFactory.buildSendKeys(keys, EleHandleDtoFactory.buildCommon(by)));
    }

    /**
     * 查找并切换到IFrame
     *
     * @param by 对应的元素
     */
    public void retryFindAndSwitchToFrame(By by) {
        retryingFindAndDoAction(EleHandleDtoFactory.buildSwitchToFrame(EleHandleDtoFactory.buildCommon(by)));
    }


    /**
     * 点击alert弹窗
     */
    public void clickAlert() {
        driver.switchTo().alert().accept();
    }


    /**
     * 流畅等待，查找页面元素
     *
     * @param eleHandleDto {@link EleHandleDto}
     * @return {@link WebElement}
     */
    public WebElement fluentWaitUntilFind(EleHandleDto eleHandleDto) {
        Integer timeWait = eleHandleDto.getWaitTime();
        timeWait = timeWait != null ? timeWait : setDto.getTimeOutInSeconds();
        WebDriverWait waitSetting = new WebDriverWait(driver, timeWait, setDto.getSleepInMillis());
        WebElement element = waitSetting.until(driver -> {
            // 等待页面状态加载完成
            // waitPageLoaded();
            return driver.findElement(eleHandleDto.getBy());
        });
        log.debug("元素:{},存在:{}", element, isFind(element));
        return element;
    }


    /**
     * 判断某个节点是否被找到
     *
     * @param webElement 对应的节点
     * @return true表示找到
     */
    public boolean isFind(WebElement webElement) {
        return webElement != null && webElement.isDisplayed() && webElement.isEnabled();
    }

    /**
     * 输出当前的frame
     */
    public void getCurrentFrame() {
        if (!setDto.getDebugMode()) {
            return;
        }
        Object frame = JsUtil.runJs("return frameElement === null ? null : frameElement.src");
        if (frame != null) {
            log.debug("当前的frameUrl是:{}", frame);
        }
        String fileName = "getChildFrames";
        String script = JsUtil.loadCommonScript(fileName);
        Object result = JsUtil.runJs(String.format("%s return %s();", script, fileName));
        if (result != null) {
            log.debug("当前的frame下的子frame是:{}", result);
        }
    }


    /**
     * 打印当前页面的源代码，用于分析调试
     */
    public void printCurrentWebSourcecode() {
        log.info(JsUtil.runJs("return document.documentElement.innerHTML;"));
    }

    /**
     * 等待页面加载完成(使用document.readyState的方法)
     *
     * @deprecated 改为调用JSWaiter的waitUntilJQueryReady()方法
     */
    public void waitPageLoaded() {
        WebDriverWait waitSetting = new WebDriverWait(driver, setDto.getMaxWaitTime(), setDto.getInterval());
        waitSetting.until((ExpectedCondition<Boolean>) driver -> "complete".equals(JsUtil.runJs("return document.readyState")));
    }

    /**
     * 等待页面加载完成(使用自己定义的脚本方式)
     *
     * @see WebUtil#waitPageLoadedBySelfJS(String)
     */
    public void waitPageLoadedBySelfJS(String script) {
        waitPageLoadedBySelfJS(script, setDto.getMaxWaitTime(), setDto.getInterval());
    }


    /**
     * 切换到指定的window
     *
     * @param window window的名字
     */
    public void switchToWindow(String window) {
        log.info("准备开始切换window:" + window);
        driver.switchTo().window(window);
        log.info("切换window:{}成功,当前url是:{}", window, driver.getCurrentUrl());
    }

    /**
     * 休眠等待一下
     *
     * @param time 要等待的时间
     */
    public void doWait(Integer time) {
        time = time == null ? setDto.getDoWait() : time;
        // 休眠等待一下
        ThreadUtil.safeSleep(time);
    }


    /**
     * 刷新页面
     */
    public void refresh() {
        driver.navigate().refresh();
    }


    /**
     * 根据url一步切换到对应的frame
     * 由于这里破项目的iframe没有id又没有name,实在太过于坑爹,所以这里提供一个直接通过url来定位到iframe的方法,这方法是通过递归来查询,性能可能会有点低下,可以通过实际情况来使用
     *
     * @param url 要切换的frame的url
     */
    public void switchTheFrame(String url) {
        WebUtil.driver.switchTo().defaultContent();
        // 切换到最顶级后需要等待一下,不然有可能页面没切换完,js脚本就注入到页面上,导致获取iframe不准确
        WebUtil.doWait(100);
        String script = JsUtil.loadCommonScript(JsUtil.DOM_SCRIPT);
        String handle = String.format("%s return frameHelper.frameObj.findTheFrame('%s');", script, url);
        List<WebElement> list = JsUtil.runJs(handle);
        for (WebElement element : list) {
            WebUtil.driver.switchTo().frame(element);
        }
    }

    /**
     * 等待页面的某些元素或者某些东西加载完成(通过使用脚本来判断这些是否加载完成)
     *
     * @param script      要执行判断的脚本
     * @param maxWaitTime 最长等待时间
     * @param interval    每次轮询间隔的时间
     */
    private void waitPageLoadedBySelfJS(String script, long maxWaitTime, Integer interval) {
        long start = System.currentTimeMillis();
        log.info("执行等待脚本:" + script);
        while (!(Boolean) JsUtil.runJs(script)) {
            if (System.currentTimeMillis() - start > maxWaitTime) {
                log.warn("超出最长等待时间" + maxWaitTime + ",跳出循环");
                throw new FindElementException("超出最长等待时间:" + maxWaitTime);
            }
            doWait(interval);
        }
    }

    /**
     * 重复查找和执行动作（click或sendKeys等），当出现引用的element过时后，重新查找该element
     *
     * @param eleHandleDto {@link EleHandleDto}
     * @return {@link WebElement}
     */
    private WebElement retryingFindAndDoAction(EleHandleDto eleHandleDto) {
        ElementHandler handler = HandlerFactory.getElementHandler(eleHandleDto.getActionEnum());
        return handler.retryingFindAndDoAction(eleHandleDto);
    }

}
