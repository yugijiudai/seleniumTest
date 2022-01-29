package com.lml.selenium.factory;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.setting.dialect.Props;
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
    private final SetDto setDto = initGlobal();

    /**
     * service全局就初始化一次,如果每个用例都初始化service,driver,关闭driver和service,在调用doubleClick的时候会触发classNotFound,目前取消关闭service来解决╮(╯▽╰)╭
     */
    private ChromeDriverService service;

    /**
     * 最开始的初始化工作,全局只会执行一次, 读取配置文件并且存放到setDto里面,初始化所有handler和service
     *
     * @return {@link SetDto}
     */
    private SetDto initGlobal() {
        // 初始化处理器
        HandlerFactory.initAllHandler();
        Props props = new Props("selenium.properties");
        // 配置映射到对应的实体类中
        SetDto setting = new SetDto();
        BeanUtil.copyProperties(props, setting);
        log.info("初始化成功,配置是:{}", setting);
        initService(setting);
        return setting;
    }

    /**
     * 初始化驱动的service
     */
    private void initService(SetDto setting) {
        try {
            service = new ChromeDriverService.Builder().usingDriverExecutable(new File(setting.getDriverPath())).usingAnyFreePort().build();
            service.start();
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
        driver.close();
    }

    /**
     * 所有用例执行完成才调用,全局只执行一次,慎用！
     */
    public void closeService() {
        driver.quit();
        service.close();
    }

    /**
     * 初始化webDriver
     */
    public void initWebDriver() {
        try {
            ChromeOptions options = createChromeOption();
            driver = new ChromeDriverProxy(service, options);
            if (setDto.getUseMaxWindow()) {
                driver.manage().window().maximize();
            }
            if (setDto.getDebugMode()) {
                // 如果是debug模式,则会开启隐式等待
                driver.manage().timeouts().implicitlyWait(Duration.ofMillis(setDto.getImplicitlyWait()));
            }
        }
        catch (Exception e) {
            throw new InitException("初始化失败", e);
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
        options.addArguments("--disable-popup-blocking");
        // 启动无沙盒模式运行
        options.addArguments("no-sandbox");
        // 禁用扩展
        options.addArguments("disable-extensions");
        // 默认浏览器检查
        options.addArguments("no-default-browser-check");
        Map<String, Object> prefs = Maps.newHashMap();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        // 禁用保存密码提示框
        options.setExperimentalOption("prefs", prefs);
        options.setExperimentalOption("w3c", false);
        options.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
        options.setAcceptInsecureCerts(true);
        options.setExperimentalOption("useAutomationExtension", false);
        if (setDto.getUseBmpProxy()) {
            options.setProxy(RequestProxy.createProxy());
        }
        if (setDto.getUseNoHead()) {
            options.addArguments("-headless");
        }
        return options;
    }


}
