package com.lml.selenium.demo.asserts;

import com.lml.selenium.dto.UserDto;
import com.lml.selenium.factory.SeleniumFactory;
import com.lml.selenium.util.JsUtil;
import com.lml.selenium.util.UserUtil;
import com.lml.selenium.util.WebUtil;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import java.util.List;

/**
 * @author yugi
 * @apiNote demo的断言
 * @since 2019-06-21
 */
public class DemoAssert {


    /**
     * 断言testDemo01
     *
     * @param assertMsg 要断言的信息
     */
    public void assertTestDemo01(String assertMsg) {
        WebElement tip = WebUtil.retryFindElements(By.id("tip")).get(0);
        Assert.assertEquals(tip.getText(), assertMsg);
    }

    /**
     * 断言testDemo02
     *
     * @param assertMsg 要断言的信息
     */
    public void assertTestDemo02(String assertMsg) {
        Alert alert = SeleniumFactory.getDriver().switchTo().alert();
        Assert.assertEquals(alert.getText(), assertMsg);
        WebUtil.clickAlert();
    }

    /**
     * 断言testDemo03
     *
     * @param webElements 登录按钮
     */
    public void assertTestDemo03(List<WebElement> webElements) {
        WebUtil.clickAlert();
        Assert.assertEquals(webElements.size(), 1);
        Assert.assertEquals(webElements.get(0).getAttribute("type"), "button");
        JsUtil.addCommonScript(JsUtil.DOM_SCRIPT);
        String name = JsUtil.runJs(String.format("return domHelper.domObj.getDomValue(%s)", "$('#name')"));
        String pass = JsUtil.runJs(String.format("return domHelper.domObj.getDomValue(%s)", "$('#pass')"));
        UserDto userDto = new UserDto().setUsername(name).setPass(pass);
        UserUtil.setUser(userDto);
    }

    /**
     * 断言testDemo04
     *
     * @param list      js脚本返回来的元素dom列表
     * @param expectMsg 要断言的信息
     */
    public void assertTestDemo04(List<WebElement> list, String expectMsg) {
        StringBuilder actualMsg = new StringBuilder();
        for (WebElement webElement : list) {
            actualMsg.append(webElement.getText()).append(",");
        }
        Assert.assertEquals(actualMsg.substring(0, actualMsg.length() - 1), expectMsg);
    }

    /**
     * 对testWindowAndCallback的断言,打开新窗口,把找到的元素列表带回来
     *
     * @param list      找到的元素列表
     * @param expectMsg 要断言的值
     */
    public void assertWindowAndCallBack(List<WebElement> list, String expectMsg) {
        Assert.assertEquals(list.size(), 3);
        for (WebElement webElement : list) {
            Assert.assertEquals(webElement.getTagName(), "input");
            Assert.assertEquals(webElement.getAttribute("value"), expectMsg);
        }
    }
}
