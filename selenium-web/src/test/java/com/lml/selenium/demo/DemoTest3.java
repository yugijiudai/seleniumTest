package com.lml.selenium.demo;

import cn.hutool.core.util.IdUtil;
import com.lml.selenium.common.SeleniumBaseTest;
import com.lml.selenium.factory.SeleniumFactory;
import com.lml.selenium.util.RobotUtil;
import com.lml.selenium.util.WebUtil;
import org.openqa.selenium.By;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author yugi
 * @apiNote
 * @since 2022-07-18
 */
public class DemoTest3 extends SeleniumBaseTest {

    @BeforeClass
    public void beforeClass() {
        this.initWebDriver();
    }

    @AfterClass
    public void afterClass() {
        this.quitDriver();
    }

    @Test(testName = "测试下载", description = "测试下载")
    public void testDownLoad() {
        SeleniumFactory.getDriver().get("http://nodejs.cn/download/");
        WebUtil.retryFindElements(By.xpath("//a[contains(text(), ' 32 位 ')]")).get(0).click();
        String newFileName = IdUtil.fastSimpleUUID() + ".csv";
        System.out.println(RobotUtil.selectFile(newFileName, 1000, true));
    }


    @Override
    public String getCaseTemplate() {
        return "";
    }
}
