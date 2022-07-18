package com.lml.selenium.factory;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.setting.dialect.Props;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lml.selenium.dto.SetDto;
import com.lml.selenium.exception.InitException;
import com.lml.selenium.proxy.ChromeDriverProxy;
import com.lml.selenium.proxy.RequestProxy;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.logging.Level;

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
    public void initWebDriver() {
        initService();
        ChromeOptions chromeOption = createChromeOption();
        driver = new ChromeDriverProxy(service, chromeOption);
        if (setDto.getUseMaxWindow()) {
            driver.manage().window().maximize();
        }
        if (setDto.getDebugMode()) {
            // 如果是debug模式,则会开启隐式等待
            driver.manage().timeouts().implicitlyWait(Duration.ofMillis(setDto.getImplicitlyWait()));
        }
    }

    /**
     * 设置相关的options
     */
    private ChromeOptions createChromeOption() {
        ChromeOptions options = new ChromeOptions();
        LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
        // 禁用阻止弹出窗口
        // options.addArguments("--disable-popup-blocking");
        // 启动无沙盒模式运行
        options.addArguments("no-sandbox");
        // 禁用扩展
        options.addArguments("disable-extensions");
        // 默认浏览器检查
        options.addArguments("no-default-browser-check");
        // 无痕模式
        options.addArguments("--incognito");
        // 不显示 chrome正受到自动测试软件的控制
        options.addArguments("--disable-infobars");
        Map<String, Object> prefs = Maps.newHashMap();
        prefs.put("credentials_enable_service", false);
        // 禁用保存密码提示框
        prefs.put("profile.password_manager_enabled", false);
        options.setExperimentalOption("prefs", prefs);
        options.setExperimentalOption("w3c", false);
        options.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
        options.setAcceptInsecureCerts(true);
        options.setExperimentalOption("useAutomationExtension", false);
        // 模拟真正浏览器
        options.setExperimentalOption("excludeSwitches", Lists.newArrayList("enable-automation"));
        if (setDto.getUseBmpProxy()) {
            options.setProxy(RequestProxy.createProxy());
        }
        if (setDto.getUseNoHead()) {
            options.addArguments("-headless");
        }
        return options;
    }


}
