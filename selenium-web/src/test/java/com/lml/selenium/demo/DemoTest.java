package com.lml.selenium.demo;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.lml.selenium.common.SeleniumBaseTest;
import com.lml.selenium.dto.NoEleHandlerDto;
import com.lml.selenium.dto.UserDto;
import com.lml.selenium.entity.Selenium;
import com.lml.selenium.enums.ActionEnum;
import com.lml.selenium.factory.NoEleHandlerDtoFactory;
import com.lml.selenium.factory.SeleniumFactory;
import com.lml.selenium.handler.other.RunScriptHandler;
import com.lml.selenium.util.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author yugi
 * @apiNote demo的测试用例
 * @since 2019-05-05
 */
@Slf4j
public class DemoTest extends SeleniumBaseTest {

    private static String expect;

    /**
     * 回调用的方法
     */
    public void scriptRun(String nameAndValue) {
        System.out.println(nameAndValue);
        if (expect != null) {
            Assert.assertEquals(nameAndValue, expect);
        }
    }


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

    @AfterClass
    public void afterClass() {
        SeleniumFactory.closeService();
    }

    /**
     * 什么都不填直接点登录
     */
    @Test(testName = "什么都不填直接点登录")
    public void testDemo1() {
        this.doHandle(Pair.of(0, 1));
    }


    /**
     * 输入错密码
     */
    @Test(testName = "输入错密码")
    public void testDemo02() {
        this.doHandle(Pair.of(2, 4));
    }

    /**
     * 登录成功
     */
    @Test(testName = "登录成功,从第五步直接执行到最后")
    public void testDemo03() {
        this.doHandle(Pair.of(5, 0));
        UserDto user = UserUtil.getUser();
        Assert.assertNotNull(user);
        Assert.assertEquals(user.getPass(), "111111");
        Assert.assertEquals(user.getUsername(), "lml");
    }

    @Test(testName = "登录成功,使用模块执行")
    public void testDemo04() {
        this.doHandleByModel(new String[]{"登录成功"});
    }

    @Test(testName = "iframe")
    public void testDemo5() {
        this.doHandleByModel(new String[]{"iframeSelf"});
    }


    @Test(testName = "测试脚本运行")
    public void testScriptHandler() {
        String file = ResourceUtil.readUtf8Str("script/runScript.json");
        JSONArray array = JSONUtil.parseArray(file);
        for (Object ext : array) {
            expect = JSONUtil.parseObj(ext).getStr("expect");
            NoEleHandlerDto noEleHandlerDto = NoEleHandlerDtoFactory.buildDto(new Selenium().setExt(ext.toString()).setElementAction(ActionEnum.RUN_SCRIPT));
            try {
                new RunScriptHandler().doHandle(noEleHandlerDto);
            }
            catch (Throwable e) {
                Assert.fail(JSONUtil.parseObj(ext).getStr("tag"), e);
            }
        }
    }
}
