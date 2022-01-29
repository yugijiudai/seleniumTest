package com.lml.selenium.demo;

import com.lml.selenium.common.SeleniumBaseTest;
import com.lml.selenium.factory.SeleniumFactory;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author yugi
 * @apiNote demo的测试用例
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


}
