package com.lml.selenium.util;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.lml.selenium.client.HandlerClient;
import com.lml.selenium.dto.EleHandlerDto;
import com.lml.selenium.dto.SetDto;
import com.lml.selenium.entity.Selenium;
import com.lml.selenium.enums.ActionEnum;
import com.lml.selenium.enums.ClickActionEnum;
import com.lml.selenium.enums.SwitchFrameActionEnum;
import com.lml.selenium.exception.FindElementException;
import com.lml.selenium.factory.EleHandlerDtoFactory;
import com.lml.selenium.factory.HandlerFactory;
import com.lml.selenium.factory.SeleniumFactory;
import com.lml.selenium.factory.WaitFactory;
import com.lml.selenium.handler.element.ElementHandler;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Wait;

import java.time.Duration;
import java.util.List;

/**
 * @author yugi
 * @apiNote 统一操作的工具类
 * @since 2019-05-01
 */
@Slf4j
@UtilityClass
public class WebUtil {


    /**
     * 根据by来查找需要的元素列表
     *
     * @param by 对应的元素
     * @return {@link WebElement}
     */
    public List<WebElement> retryFindElements(By by) {
        return WebUtil.findUntil(EleHandlerDtoFactory.buildCommon(by));
    }

    /**
     * 重复查找和click，当出现引用的element过时后，重新查找该element
     *
     * @param by 对应的元素
     */
    public void retryFindAndClick(By by) {
        retryingFindAndDoAction(EleHandlerDtoFactory.buildClick(ClickActionEnum.BY_TAG_TYPE, EleHandlerDtoFactory.buildCommon(by)));
    }

    /**
     * 通过Selenium API进行点击，避免JS点击不成功或者弹出对话框阻塞的问题
     *
     * @param by 对应的元素
     */
    public void retryFindAndClickBySeleniumApi(By by) {
        retryingFindAndDoAction(EleHandlerDtoFactory.buildClick(ClickActionEnum.API, EleHandlerDtoFactory.buildCommon(by)));
    }

    /**
     * 执行双击
     *
     * @param by 对应的元素
     */
    public void retryFindAndDoubleClick(By by) {
        retryingFindAndDoAction(EleHandlerDtoFactory.buildClick(ClickActionEnum.DOUBLE_CLICK, EleHandlerDtoFactory.buildCommon(by)));
    }

    /**
     * 执行右键
     *
     * @param by 对应的元素
     */
    public void retryFindAndRightClick(By by) {
        retryingFindAndDoAction(EleHandlerDtoFactory.buildClick(ClickActionEnum.RIGHT_CLICK, EleHandlerDtoFactory.buildCommon(by)));
    }

    /**
     * 通过Javascript进行点击，避免Selenium API点击不成功
     *
     * @param by 对应的元素
     */
    public void retryFindAndClickByJs(By by) {
        retryingFindAndDoAction(EleHandlerDtoFactory.buildClick(ClickActionEnum.JS, EleHandlerDtoFactory.buildCommon(by)));
    }


    /**
     * 鼠标停留在控件上
     *
     * @param by 对应的元素
     */
    public void retryFindAndHover(By by) {
        retryingFindAndDoAction(EleHandlerDtoFactory.buildHover(EleHandlerDtoFactory.buildCommon(by)));
    }

    /**
     * 清空内容
     *
     * @param by 对应的元素
     */
    public void retryingFindAndClear(By by) {
        retryingFindAndDoAction(EleHandlerDtoFactory.buildClear(EleHandlerDtoFactory.buildCommon(by)));
    }

    /**
     * 查找并且发送对应的内容
     *
     * @param by   对应的元素
     * @param keys 要发送的key
     */
    public void retryFindAndSendKeys(By by, String keys) {
        retryingFindAndDoAction(EleHandlerDtoFactory.buildSendKeys(keys, EleHandlerDtoFactory.buildCommon(by)));
    }


    /**
     * 点击alert弹窗
     */
    public void clickAlert() {
        HandlerClient.doAction(new Selenium().setElementAction(ActionEnum.ALERT));
    }


    /**
     * 查找页面元素
     *
     * @param eleHandlerDto {@link EleHandlerDto}
     * @return {@link WebElement}
     */
    public List<WebElement> findUntil(EleHandlerDto eleHandlerDto) {
        Long timeWait = eleHandlerDto.getWaitTime();
        SetDto setDto = SeleniumFactory.getSetDto();
        timeWait = timeWait != null ? timeWait : setDto.getMaxWaitTime();
        Integer interval = setDto.getInterval();
        try {
            WebDriver webDriver = SeleniumFactory.getDriver();
            Wait<WebDriver> waitDriver = WaitFactory.createDefaultWait(Duration.ofMillis(timeWait), Duration.ofMillis(interval));
            waitDriver.until(driver -> {
                // 这里不使用ExpectedConditions.presenceOfAllElementsLocatedBy,因为要判断所有元素可用才行
                List<WebElement> elements = driver.findElements(eleHandlerDto.getBy());
                return checkElementAllFind(elements);
            });
            return webDriver.findElements(eleHandlerDto.getBy());
        }
        catch (Throwable e) {
            ActionEnum actionEnum = eleHandlerDto.getActionEnum();
            String action = actionEnum == null ? "查找元素" : actionEnum.getCode();
            log.error("执行动作:【{}】操作节点【{}】时超过最大时间:{}ms,重试频率:{}ms", action, eleHandlerDto.getBy(), timeWait, interval);
            throw new FindElementException(e);
        }
    }

    /**
     * 校验元素是否全部可用
     *
     * @param elements 元素列表
     * @return true表示可用
     */
    private boolean checkElementAllFind(List<WebElement> elements) {
        if (elements.size() == 0) {
            // 如果是空列表,则表示没找到元素
            return false;
        }
        for (WebElement element : elements) {
            if (!isFind(element)) {
                log.debug("元素:{},不可用", element);
                return false;
            }
        }
        return true;
    }


    /**
     * 判断某个节点是否被找到
     *
     * @param webElement 对应的节点
     * @return true表示找到
     */
    public boolean isFind(WebElement webElement) {
        return webElement != null && webElement.isEnabled();
        // return webElement != null && webElement.isDisplayed() && webElement.isEnabled();
    }

    /**
     * 输出当前的frame
     */
    public void getCurrentFrame() {
        if (!SeleniumFactory.getSetDto().getDebugMode()) {
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
     * 切换到指定的window
     *
     * @param window window的名字
     */
    public void switchToWindow(String window) {
        HandlerClient.doAction(new Selenium().setExt(window).setElementAction(ActionEnum.SWITCH_WINDOW));
    }

    /**
     * 休眠等待一下
     *
     * @param time 要等待的时间(毫秒)
     */
    public void doWait(Integer time) {
        time = time == null ? SeleniumFactory.getSetDto().getDoWait() : time;
        // 休眠等待一下
        ThreadUtil.safeSleep(time);
    }


    /**
     * 刷新页面
     */
    public void refresh() {
        HandlerClient.doAction(new Selenium().setElementAction(ActionEnum.REFRESH));
    }


    /**
     * 根据url一步切换到对应的frame
     * 由于这里破项目的iframe没有id又没有name,实在太过于坑爹,所以这里提供一个直接通过url来定位到iframe的方法,这方法是通过递归来查询,性能可能会有点低下,可以通过实际情况来使用
     *
     * @param url 要切换的frame的url
     */
    public void switchTheFrame(String url) {
        JSONObject ext = JSONUtil.createObj().set("url", url).set("type", SwitchFrameActionEnum.SELF);
        HandlerClient.doAction(new Selenium().setExt(ext.toString()).setElementAction(ActionEnum.SWITCH_MY_FRAME));
    }


    /**
     * 重复查找和执行动作（click或sendKeys等），当出现引用的element过时后，重新查找该element
     *
     * @param eleHandlerDto {@link EleHandlerDto}
     */
    private void retryingFindAndDoAction(EleHandlerDto eleHandlerDto) {
        ElementHandler handler = HandlerFactory.getElementHandler(eleHandlerDto.getActionEnum());
        handler.retryingFindAndDoAction(eleHandlerDto);
    }

}
