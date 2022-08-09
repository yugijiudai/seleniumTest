package com.lml.selenium.factory;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.setting.dialect.Props;
import com.lml.selenium.dto.SetDto;
import com.lml.selenium.exception.InitException;
import com.lml.selenium.ext.AbstractChromeOption;
import com.lml.selenium.ext.MyChromeOption;
import com.lml.selenium.proxy.ChromeDriverProxy;
import com.lml.selenium.proxy.RequestProxy;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;

import java.io.File;
import java.io.IOException;

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
     *
     * @param abstractChromeOption 自己定义初始化好的chromeOption
     */
    public void initWebDriver(AbstractChromeOption abstractChromeOption) {
        initService();
        if (abstractChromeOption == null) {
            // 如果没有则使用默认的配置
            abstractChromeOption = new MyChromeOption();
        }
        driver = new ChromeDriverProxy(service, abstractChromeOption.createChromeOption());
        String[] windowSize = setDto.getWindowSize().split(",");
        if (windowSize.length != 3) {
            throw new InitException("请检查窗口大小的参数格式!");
        }
        String isMaxWindow = windowSize[0];
        if ("true".equals(isMaxWindow)) {
            driver.manage().window().maximize();
        }
        else {
            Dimension dimension = new Dimension(Integer.parseInt(windowSize[1]), Integer.parseInt(windowSize[2]));
            driver.manage().window().setSize(dimension);
        }
        if (!setDto.getPromptForDownload()) {
            // 只有弹窗模式禁止才适合用这个方式，这个方式开启之后就算弹窗模式设置成true也不会生效
            setDownloadBehavior(driver);
        }

    }

    /**
     * 设置下载的默认目录，设置win系统下一些exe文件下载下来提示风险导致保存无效的问题
     * driver.command_executor._commands["send_command"] = ("POST", '/session/$sessionId/chromium/send_command')
     * params = {'cmd': 'Page.setDownloadBehavior', 'params': {'behavior': 'allow', 'downloadPath': r"C:\Users\Any\Downloads"}}
     * driver.execute("send_command", params)
     *
     * @param driver {@link WebDriver}
     */
    private void setDownloadBehavior(WebDriver driver) {
        // JSONObject commandParams = JSONUtil.createObj();
        // commandParams.set("cmd", "Page.setDownloadBehavior");
        ChromeDriver driverService = (ChromeDriver) driver;
        JSONObject params = JSONUtil.createObj();
        params.set("behavior", "allow");
        params.set("downloadPath", setDto.getDownloadPath());
        // 直接使用cdp的方式来运行命令,不需要走http协议
        driverService.executeCdpCommand("Page.setDownloadBehavior", params);
        // commandParams.set("params", params);
        // String url = StrUtil.format("{}/session/{}/chromium/send_command", service.getUrl(), driverService.getSessionId());
        // HttpUtil.post(url, commandParams.toString());
    }


}
