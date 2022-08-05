package com.lml.selenium.demo;

import com.lml.selenium.common.SeleniumBaseTest;
import com.lml.selenium.factory.SeleniumFactory;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

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


    @Test(description = "登录成功,测试按模块执行")
    public void testDemo01() {
        this.doHandleByModel(new String[]{"登录成功"});
    }


    @Test(description = "测试iframe")
    public void testDemo2() {
        this.doHandleByModel(new String[]{"iframeSelf"});
    }

    @Test(description = "打开新窗口和查找到的元素")
    public void testWindowAndCallback() {
        this.doHandleByModel(new String[]{"新窗口"});
    }


}
