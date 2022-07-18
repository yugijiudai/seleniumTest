package com.lml.selenium.demo;

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

    @Test
    public void testDownLoad() {
        SeleniumFactory.getDriver().get("http://nodejs.cn/download/");
        WebUtil.retryFindElements(By.xpath("//a[contains(text(), ' 32 位 ')]")).get(0).click();
        RobotUtil.selectFile("hello.csv", 3000, 5000);
    }


    @Override
    public String getCaseTemplate() {
        return "";
    }
}
