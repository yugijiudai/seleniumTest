package com.lml.selenium.demo.asserts;

import com.lml.selenium.dto.UserDto;
import com.lml.selenium.util.JsUtil;
import com.lml.selenium.util.UserUtil;
import com.lml.selenium.util.WebUtil;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

/**
 * @author yugi
 * @apiNote demo的断言
 * @since 2019-06-21
 */
public class DemoAssert {


    /**
     * 断言testLogin01
     *
     * @param assertMsg 要断言的信息
     */
    public void assertTestLogin01(String assertMsg) {
        WebElement tip = WebUtil.retryFindAndGetText(By.id("tip"));
        Assert.assertEquals(tip.getText(), assertMsg);
    }

    /**
     * 断言testLogin02
     *
     * @param assertMsg 要断言的信息
     */
    public void assertTestLogin02(String assertMsg) {
        Alert alert = WebUtil.driver.switchTo().alert();
        Assert.assertEquals(alert.getText(), assertMsg);
        WebUtil.clickAlert();
    }

    /**
     * 断言testLogin03
     *
     * @param assertMsg 要断言的信息
     */
    public void assertTestLogin03(String assertMsg) {
        Alert alert = WebUtil.driver.switchTo().alert();
        Assert.assertEquals(alert.getText(), assertMsg);
        WebUtil.clickAlert();
        JsUtil.addCommonScript(JsUtil.DOM_SCRIPT);
        String name = JsUtil.runJs(String.format("return domHelper.domObj.getDomValue(%s)", "$('#name')"));
        String pass = JsUtil.runJs(String.format("return domHelper.domObj.getDomValue(%s)", "$('#pass')"));
        UserDto userDto = new UserDto().setUsername(name).setPass(pass);
        UserUtil.setUser(userDto);
    }

}
