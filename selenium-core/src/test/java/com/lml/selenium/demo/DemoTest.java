package com.lml.selenium.demo;

import com.lml.selenium.BaseTest;
import com.lml.selenium.dto.UserDto;
import com.lml.selenium.factory.SeleniumFactory;
import com.lml.selenium.util.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author yugi
 * @apiNote demo的测试用例
 * @since 2019-05-05
 */
@Slf4j
public class DemoTest extends BaseTest {


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

    /**
     * 什么都不填直接点登录
     */
    @Test(description = "什么都不填直接点登录")
    public void testLogin01() {
        this.doHandle(new Integer[][]{{0, 1}});
    }


    /**
     * 输入错密码
     */
    @Test(description = "输入错密码")
    public void testLogin02() {
        this.doHandle(new Integer[][]{{2, 4}});
    }

    /**
     * 登录成功
     */
    @Test(description = "登录成功")
    public void testLogin03() {
        this.doHandle(new Integer[][]{{5, 0}});
        UserDto user = UserUtil.getUser();
        Assert.assertNotNull(user);
        Assert.assertEquals(user.getPass(), "111111");
        Assert.assertEquals(user.getUsername(), "lml");
    }

    @Test(description = "登录成功,不是用步骤的方法")
    public void testLogin04() {
        this.doHandleByModel(new String[]{"登录成功"});
    }


}
