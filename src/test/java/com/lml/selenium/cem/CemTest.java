package com.lml.selenium.cem;

import com.lml.selenium.BaseTest;
import com.lml.selenium.util.WebUtil;
import org.openqa.selenium.By;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author yugi
 * @apiNote
 * @since 2021-10-08
 */
public class CemTest extends BaseTest {
    @Override
    public String getCaseTemplate() {
        return null;
    }



    @BeforeMethod
    public void init() {
        webDriverInit();
        WebUtil.driver.get(WebUtil.getSetDto().getBaseUrl());
    }

    @AfterMethod
    public void after() {
        quitDriver();
    }

    @Test
    public void testLogin() {
        WebUtil.retryFindAndSendKeys(By.xpath("//*[@id=\"app\"]/div/div/div[3]/div/div[1]/input"), "");
        WebUtil.retryFindAndSendKeys(By.xpath("//*[@id=\"app\"]/div/div/div[3]/div/div[2]/input"), "");
        WebUtil.retryFindAndClick(By.xpath("//*[@id=\"app\"]/div/div/div[3]/div/button"));
        WebUtil.retryFindAndClick(By.xpath("//*[@id=\"app\"]/div[2]/div/div/div[3]/div[2]/div/div[1]/div[2]"));
        System.out.println(111);
        WebUtil.doWait(5000);
    }
}
