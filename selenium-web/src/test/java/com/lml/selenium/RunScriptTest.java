package com.lml.selenium;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.lml.selenium.dto.NoEleHandlerDto;
import com.lml.selenium.entity.Selenium;
import com.lml.selenium.enums.ActionEnum;
import com.lml.selenium.factory.NoEleHandlerDtoFactory;
import com.lml.selenium.factory.SeleniumFactory;
import com.lml.selenium.handler.other.RunScriptHandler;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author yugi
 * @apiNote
 * @since 2022-01-27
 */
public class RunScriptTest {

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

    @BeforeClass
    public void init() {
        SeleniumFactory.initWebDriver();
    }

    @AfterClass
    public void after() {
        SeleniumFactory.quitDriver();
    }

    @Test
    public void testScript() {
        String file = ResourceUtil.readUtf8Str("script/runScript.json");
        JSONArray array = JSONUtil.parseArray(file);
        for (Object ext : array) {
            expect = JSONUtil.parseObj(ext).getStr("expect");
            NoEleHandlerDto noEleHandlerDto = NoEleHandlerDtoFactory.buildDto(new Selenium().setExt(ext.toString()).setElementAction(ActionEnum.RUN_SCRIPT));
            new RunScriptHandler().doHandle(noEleHandlerDto);
        }

    }
}
