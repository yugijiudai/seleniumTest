package com.lml.controller;

import com.lml.selenium.factory.SeleniumFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yugi
 * @apiNote
 * @since 2023-07-12
 */
@RestController
public class SeleniumControllerDemo {


    @GetMapping(value = "printValue")
    public String printValue() {
        SeleniumFactory.getTraceIdMap();
        return "ok";
    }


    @GetMapping(value = "runDriverTest")
    public String runDriverTest(String content, String driverId) {
        String id = SeleniumFactory.initWebDriver(null, driverId);
        WebDriver driverHolder = SeleniumFactory.getDriverHolder();
        driverHolder.get("https://www.baidu.com");
        driverHolder.findElement(By.id("kw")).sendKeys(content);
        return id;
    }


}
