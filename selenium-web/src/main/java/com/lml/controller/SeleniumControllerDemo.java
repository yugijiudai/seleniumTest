package com.lml.controller;

import cn.hutool.core.lang.Pair;
import com.lml.selenium.factory.SeleniumFactory;
import com.lml.selenium.proxy.RequestProxy;
import com.lml.selenium.util.JsUtil;
import com.lml.selenium.util.WaitUtl;
import com.lml.selenium.vo.BrowserVo;
import net.lightbody.bmp.core.har.Har;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author yugi
 * @apiNote
 * @since 2023-07-12
 */
@RestController
public class SeleniumControllerDemo {


    @GetMapping(value = "/printValue")
    public String printValue() {
        SeleniumFactory.getTraceIdMap();
        return "ok";
    }


    @GetMapping(value = "/runDriverTest")
    public String runDriverTest(String content, String driverId, String harName) {
        String id = SeleniumFactory.initWebDriver(null, driverId);
        RequestProxy.newHar(harName);
        WebDriver driverHolder = SeleniumFactory.getDriverHolder();
        String url = "https://www.baidu.com";
        driverHolder.get(url);
        driverHolder.findElement(By.id("kw")).sendKeys(content);
        driverHolder.findElement(By.id("su")).click();
        WaitUtl.waitUrlChange(url);
        JsUtil.waitPageLoad(50000L);
        Pair<List<BrowserVo>, Har> listHarPair = RequestProxy.captureRequest();
        List<BrowserVo> key = listHarPair.getKey();
        for (BrowserVo browserVo : key) {
            System.out.println(browserVo);
        }
        return id;
    }


}
