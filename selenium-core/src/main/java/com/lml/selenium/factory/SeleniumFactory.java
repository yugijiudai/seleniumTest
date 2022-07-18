package com.lml.selenium.factory;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.setting.dialect.Props;
import com.lml.selenium.dto.SetDto;
import com.lml.selenium.exception.InitException;
import com.lml.selenium.ext.AbstractChromeOption;
import com.lml.selenium.proxy.ChromeDriverProxy;
import com.lml.selenium.proxy.RequestProxy;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.io.IOException;
import java.time.Duration;

/**
 * @author yugi
 * @apiNote selenium初始化的工厂
 * @since 2019-05-05
 */
@UtilityClass
@Slf4j
public class SeleniumFactory {


    @Getter
    private WebDriver driver;

    /**
     * 配置文件映射到的实体类
     */
    @Getter
    private final SetDto setDto = initSetting();

    /**
     * 全局的service对象
     */
    private ChromeDriverService service;

    /**
     * 初始化配置文件和handler
     *
     * @return {@link SetDto}
     */
    private SetDto initSetting() {
        // 初始化处理器
        HandlerFactory.initAllHandler();
        Props props = new Props("selenium.properties");
        // 配置映射到对应的实体类中
        SetDto setting = new SetDto();
        BeanUtil.copyProperties(props, setting);
        log.info("初始化成功,配置是:{}", setting);
        return setting;
    }

    /**
     * 初始化驱动的service
     */
    private void initService() {
        try {
            if (service == null || !service.isRunning()) {
                service = new ChromeDriverService.Builder().usingDriverExecutable(new File(setDto.getDriverPath())).usingAnyFreePort().build();
                service.start();
            }
        }
        catch (IOException e) {
            throw new InitException("初始化service失败", e);
        }
    }

    /**
     * 关闭驱动,可以用在afterMethod
     */
    public void quitDriver() {
        // 先关闭代理再关闭driver
        RequestProxy.closeProxy();
        driver.quit();
        service.close();
    }


    /**
     * 初始化webDriver
     */
    public void initWebDriver(AbstractChromeOption option) {
        initService();
        ChromeOptions chromeOption = option.createChromeOption();
        driver = new ChromeDriverProxy(service, chromeOption);
        if (setDto.getUseMaxWindow()) {
            driver.manage().window().maximize();
        }
        if (setDto.getDebugMode()) {
            // 如果是debug模式,则会开启隐式等待
            driver.manage().timeouts().implicitlyWait(Duration.ofMillis(setDto.getImplicitlyWait()));
        }
    }

}
