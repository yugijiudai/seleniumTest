package com.lml.selenium.handler.element;

import cn.hutool.core.util.ArrayUtil;
import com.lml.selenium.dto.BaseSeleniumDto;
import com.lml.selenium.dto.EleHandlerDto;
import com.lml.selenium.enums.ActionEnum;
import com.lml.selenium.enums.ClickActionEnum;
import com.lml.selenium.factory.SeleniumFactory;
import com.lml.selenium.util.JsUtil;
import com.lml.selenium.util.WebUtil;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.NoSuchElementException;

/**
 * @author yugi
 * @apiNote 鼠标点击处理
 * @since 2019-04-30
 */
@Slf4j
public class ClickHandler implements ElementHandler {

    private static final String[] TAG_NAMES_CLICK_BY_JS = new String[]{"button", "a", "span", "img", "li", "input"};

    private static final String DISABLED_FLAG = "true";

    @Override
    public boolean preHandle(EleHandlerDto handleDto) {
        WebElement element = handleDto.getElement();
        JavascriptExecutor executor = (JavascriptExecutor) SeleniumFactory.getDriver();
        Object result = executor.executeScript("var items = {}; for (var index = 0; index < arguments[0].attributes.length; ++index) { items[arguments[0].attributes[index].name] = arguments[0].attributes[index].value } return items;", element);
        log.debug("click element attributes:{}", result);
        String attribute = element.getAttribute("disabled");
        log.debug("disabled = {}", attribute);
        if (DISABLED_FLAG.equalsIgnoreCase(attribute) || !element.isEnabled()) {
            log.warn("元素[{}]不是Enable状态，不能点击", handleDto.getBy());
            return false;
        }
        return true;
    }


    @Override
    public void doHandle(BaseSeleniumDto baseSeleniumDto) {
        EleHandlerDto handleDto = (EleHandlerDto) baseSeleniumDto;
        By by = handleDto.getBy();
        WebElement element = handleDto.getElement();
        ClickActionEnum actionExecuteMethod = handleDto.getActionExecuteMethod();
        this.clickWebElement(actionExecuteMethod, element);
        log.debug("点击元素[" + by + "]成功");
    }


    @Override
    public ActionEnum getAction() {
        return ActionEnum.CLICK;
    }


    /**
     * 触发点击
     *
     * @param actionExecuteMethod 是否强制用selenium的API调用，因为JS调用弹出Alert或模态窗口可能导致阻塞
     * @param webElement          {@link WebElement}
     */
    private void clickWebElement(ClickActionEnum actionExecuteMethod, WebElement webElement) {
        if (ClickActionEnum.API.equals(actionExecuteMethod)) {
            this.apiClick(webElement);
        }
        else if (ClickActionEnum.JS.equals(actionExecuteMethod)) {
            this.jsClick(webElement);
        }
        else if (ClickActionEnum.BY_TAG_TYPE.equals(actionExecuteMethod)) {
            log.debug("根据标签来判断点击的方法");
            this.byTag(webElement);
        }
        else if (ClickActionEnum.RIGHT_CLICK.equals(actionExecuteMethod)) {
            this.rightClick(webElement);
        }
        else if (ClickActionEnum.DOUBLE_CLICK.equals(actionExecuteMethod)) {
            this.doubleClick(webElement);
        }
    }

    /**
     * 右键
     *
     * @param webElement 要点击的元素
     */
    private void rightClick(WebElement webElement) {
        Actions action = new Actions(SeleniumFactory.getDriver());
        action.moveToElement(webElement);
        action.contextClick(webElement).build().perform();
    }

    /**
     * 双击
     *
     * @param webElement 要点击的元素
     */
    private void doubleClick(WebElement webElement) {
        Actions action = new Actions(SeleniumFactory.getDriver());
        action.doubleClick(webElement).perform();
    }

    /**
     * 根据标签来智能判断
     *
     * @param webElement 要点击的元素
     */
    private void byTag(WebElement webElement) {
        String tagName = webElement.getTagName();
        // 如果不强制调用selenium API，并且是需要采用JavaScript点击的标签类型，则用JS进行点击
        if (ArrayUtil.contains(TAG_NAMES_CLICK_BY_JS, tagName)) {
            this.jsClick(webElement);
            return;
        }
        if (WebUtil.isFind(webElement)) {
            this.apiClick(webElement);
        }
    }

    /**
     * 使用原生的方法来点击
     *
     * @param webElement 要点击的元素
     */
    private void apiClick(WebElement webElement) {
        log.debug("使用原生的方法点击");
        webElement.click();
    }

    /**
     * 使用js来点击
     *
     * @param webElement 要点击的元素
     */
    private void jsClick(WebElement webElement) {
        try {
            if (WebUtil.isFind(webElement)) {
                log.debug("使用js来点击");
                JsUtil.runJs("arguments[0].click();", webElement);
                return;
            }
            log.warn("页面上的元素无法进行单击操作");
        }
        catch (StaleElementReferenceException e) {
            log.warn("页面元素没有附加在网页中" + e.getMessage());
        }
        catch (NoSuchElementException e) {
            log.warn("页面元素没有找到要操作的页面元素" + e.getMessage());
        }
        catch (Exception e) {
            log.warn("无法完成单击操作" + e.getMessage());
        }
    }


}
