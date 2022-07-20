package com.lml.selenium.factory;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.setting.dialect.Props;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lml.selenium.dto.SetDto;
import com.lml.selenium.exception.InitException;
import com.lml.selenium.ext.MyChromeOption;
import com.lml.selenium.proxy.ChromeDriverProxy;
import com.lml.selenium.proxy.RequestProxy;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

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
     * @param chromeOption 自己定义初始化好的chromeOption
     */
    public void initWebDriver(ChromeOptions chromeOption) {
        initService();
        if (chromeOption == null) {
            // 如果没有则使用默认的配置
            MyChromeOption newOption = new MyChromeOption();
            chromeOption = newOption.createChromeOption().getKey();
        }
        driver = new ChromeDriverProxy(service, chromeOption);
        if (setDto.getUseMaxWindow()) {
            driver.manage().window().maximize();
        }
        if (setDto.getDebugMode()) {
            // 如果是debug模式,则会开启隐式等待
            driver.manage().timeouts().implicitlyWait(Duration.ofMillis(setDto.getImplicitlyWait()));
        }
        try {
            ChromeDriver driverService = (ChromeDriver) driver;
            Map<String, Object> commandParams = new HashMap<>();
            commandParams.put("cmd", "Page.setDownloadBehavior");
            Map<String, String> params = new HashMap<>();
            params.put("behavior", "allow");
            params.put("downloadPath", "D:\\driver\\download");
            commandParams.put("params", params);
            ObjectMapper objectMapper = new ObjectMapper();
            HttpClient httpClient = HttpClientBuilder.create().build();
            String command = objectMapper.writeValueAsString(commandParams);
            String u = service.getUrl() + "/session/" + driverService.getSessionId() + "/chromium/send_command";
            HttpPost request = new HttpPost(u);
            request.addHeader("content-type", "application/json");
            request.setEntity(new StringEntity(command));
            httpClient.execute(request);
        }
        catch (Exception e) {
            throw new InitException(e);
        }


        // d.getCommandExecutor().execute()
        // driver.command_executor._commands["send_command"] = ("POST", '/session/$sessionId/chromium/send_command')
        // params = {'cmd': 'Page.setDownloadBehavior', 'params': {'behavior': 'allow', 'downloadPath': r"C:\Users\Any\Downloads"}}
        // driver.execute("send_command", params)
    }

}
