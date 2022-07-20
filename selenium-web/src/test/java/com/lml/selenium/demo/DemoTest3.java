package com.lml.selenium.demo;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.lml.selenium.common.SeleniumBaseTest;
import com.lml.selenium.dto.SetDto;
import com.lml.selenium.factory.SeleniumFactory;
import com.lml.selenium.util.RobotUtil;
import com.lml.selenium.util.WebUtil;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.io.File;

/**
 * @author yugi
 * @apiNote 文件下载的测试用例
 * @since 2022-07-18
 */
public class DemoTest3 extends SeleniumBaseTest {

    private final SetDto setDto = SeleniumFactory.getSetDto();

    @AfterClass
    public void afterClass() {
        this.quitDriver();
    }

    @Test(testName = "测试弹窗下载")
    public void testPromptDownLoad() {
        this.doDownload(true);
    }


    @Test(testName = "测试非弹窗下载")
    public void testNoPromptDownLoad() {
        this.doDownload(false);
    }

    private void doDownload(boolean isPrompt) {
        setDto.setPromptForDownload(isPrompt);
        this.initWebDriver();
        SeleniumFactory.getDriver().get("http://nodejs.cn/download/");
        WebUtil.retryFindElements(By.xpath("//a[contains(text(), ' 32 位 ')]")).get(0).click();
        String newFileName = IdUtil.fastSimpleUUID() + ".csv";
        String downloadFile = RobotUtil.selectFile(newFileName, 1000, true);
        File file = FileUtil.file(downloadFile);
        Assert.assertTrue(file.exists(), "文件不存在:" + downloadFile);
        String expectFileName = RobotUtil.getFileFullPath(newFileName);
        Assert.assertEquals(downloadFile, expectFileName);
    }

    @Override
    public String getCaseTemplate() {
        return "";
    }
}
