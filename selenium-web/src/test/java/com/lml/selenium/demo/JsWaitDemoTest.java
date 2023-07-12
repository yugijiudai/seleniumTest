package com.lml.selenium.demo;

import cn.hutool.core.util.StrUtil;
import com.lml.selenium.common.SeleniumBaseTest;
import com.lml.selenium.enums.JsWaitEnum;
import com.lml.selenium.factory.SeleniumFactory;
import com.lml.selenium.util.JsUtil;
import com.lml.selenium.util.WaitUtl;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author yugi
 * @apiNote 等待的测试用例
 * @since 2022-08-05
 */
public class JsWaitDemoTest extends SeleniumBaseTest {


    @BeforeMethod
    public void init() {
        initWebDriver();
        SeleniumFactory.getDriverHolder().get(SeleniumFactory.getSetDto().getBaseUrl());
    }

    @AfterMethod
    public void afterClass() {
        this.quitDriver();
    }

    @Test(testName = "测试使用selenium等待")
    public void testJsWaitWithSelenium() {
        WaitUtl.setWaitStrategy(JsWaitEnum.SELENIUM);
        this.waitDom("selenium");
    }


    @Test(testName = "测试使用js原生等待")
    public void testJsWaitWithJs() {
        WaitUtl.setWaitStrategy(JsWaitEnum.JS);
        this.waitDom("js");
    }


    @Test(testName = "测试使用while循环等待")
    public void testJsWaitWithLoop() {
        WaitUtl.setWaitStrategy(JsWaitEnum.LOOP);
        this.waitDom("loop");
    }


    /**
     * 利用延迟去创建这个dom,然后去等待
     *
     * @param id 这个dom的id
     */
    private void waitDom(String id) {
        JsUtil.runJs(StrUtil.format("createDom('{}', 2000)", id));
        String waitJs = StrUtil.format("return document.getElementById('{}') != null", id);
        WaitUtl.waitLoadByJs(waitJs);
    }


    @Override
    public String getCaseTemplate() {
        return "";
    }
}
