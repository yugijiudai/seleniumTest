package com.lml.selenium.demo;

import com.lml.selenium.common.SeleniumBaseTest;
import com.lml.selenium.factory.SeleniumFactory;
import com.lml.selenium.util.WebUtil;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

/**
 * @author yugi
 * @apiNote demo2的测试用例, 执行完demo1后再执行这个, 确保selenium的初始化和销毁工作能正常执行
 * @since 2019-05-05
 */
@Slf4j
public class DemoTest2 extends SeleniumBaseTest {


    @Override
    public String getCaseTemplate() {
        return "demo_test";
    }

    @BeforeMethod
    public void init() {
        initWebDriver();
        SeleniumFactory.getDriver().get(SeleniumFactory.getSetDto().getBaseUrl());
    }

    @AfterMethod
    public void after() {
        quitDriver();
    }


    @Test(testName = "登录成功,使用模块执行")
    public void testDemo01() {
        this.doHandleByModel(new String[]{"登录成功"});
    }


    @Test(testName = "iframe")
    public void testDemo2() {
        this.doHandleByModel(new String[]{"iframeSelf"});
    }

    @Test(testName = "打开新窗口")
    public void testDemo3() {
        this.doHandleByModel(new String[]{"新窗口"});
        List<WebElement> list = WebUtil.retryFindElements(By.className("ipt"));
        Assert.assertEquals(list.size(), 3);
        for (WebElement webElement : list) {
            Assert.assertEquals(webElement.getTagName(), "input");
            Assert.assertEquals(webElement.getAttribute("value"), "hello");
        }
    }


}
