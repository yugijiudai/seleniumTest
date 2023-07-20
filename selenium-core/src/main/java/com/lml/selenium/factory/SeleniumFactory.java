package com.lml.selenium.factory;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.setting.dialect.Props;
import com.lml.selenium.dto.SetDto;
import com.lml.selenium.exception.BizException;
import com.lml.selenium.exception.InitException;
import com.lml.selenium.ext.AbstractChromeOption;
import com.lml.selenium.ext.MyChromeOption;
import com.lml.selenium.holder.ReqHolder;
import com.lml.selenium.proxy.ChromeDriverProxy;
import com.lml.selenium.proxy.RequestProxy;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yugi
 * @apiNote selenium初始化的工厂
 * @since 2019-05-05
 */
@UtilityClass
@Slf4j
public class SeleniumFactory {

    /**
     * 用来跟踪对应的driver,用于给用户灵活使用driver,每次初始化可以使用新的driver或者使用之前用的,key是跟踪id,value是对应driver和service,通过这个id找回对应的driver和service
     */
    private final ConcurrentHashMap<String, Pair<WebDriver, ChromeDriverService>> traceDriverMap = new ConcurrentHashMap<>();

    /**
     * id关联map,key是每次请求的独立id,全局独立唯一,value是这个请求对应的driver跟踪id
     */
    private final Map<String, String> traceIdMap = new ConcurrentHashMap<>();

    /**
     * 配置文件映射到的实体类
     */
    @Getter
    private final SetDto setDto = initSetting();

    /**
     * 获取driver
     *
     * @return 对应的驱动
     */
    public WebDriver getDriverHolder() {
        String driverId = getDriverId();
        Pair<WebDriver, ChromeDriverService> pair = traceDriverMap.get(driverId);
        if (pair == null) {
            throw new BizException(StrUtil.format("无法找到对应的驱动:{}", driverId));
        }
        return pair.getLeft();
    }

    /**
     * 根据当前请求id获取对应的driverId
     *
     * @return 返回driverId
     */
    private String getDriverId() {
        return traceIdMap.get(ReqHolder.getTraceId());
    }

    public void getTraceIdMap() {
        log.info("traceIdMap:");
        for (Map.Entry<String, String> entry : traceIdMap.entrySet()) {
            log.info("{}-{}", entry.getKey(), entry.getValue());
        }
        log.info("traceDriverMap:");
        for (Map.Entry<String, Pair<WebDriver, ChromeDriverService>> entry : traceDriverMap.entrySet()) {
            log.info("{}-{}-{}", entry.getKey(), entry.getValue().getLeft(), entry.getValue().getRight());
        }
    }

    /**
     * 添加测试id
     *
     * @param driverId 驱动追踪的id
     * @param driver   需要添加的driver
     */
    public void addDriverHolder(String driverId, WebDriver driver) {
        Pair<WebDriver, ChromeDriverService> pair = traceDriverMap.get(driverId);
        if (pair == null) {
            throw new BizException("找不到对应的驱动跟踪id");
        }
        traceDriverMap.put(driverId, Pair.of(driver, pair.getRight()));
    }

    /**
     * 移除driver
     */
    public void removeDriver() {
        traceDriverMap.remove(getDriverId());
    }

    /**
     * 获取service
     *
     * @return service
     */
    public ChromeDriverService getServiceHolder() {
        Pair<WebDriver, ChromeDriverService> pair = traceDriverMap.get(getDriverId());
        return pair == null ? null : pair.getRight();
    }

    /**
     * 添加service
     *
     * @param driverId 驱动追踪id
     * @param service  需要添加的service
     */
    public void addServiceHolder(String driverId, ChromeDriverService service) {
        traceDriverMap.put(driverId, Pair.of(null, service));
    }


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
     *
     * @param driverId 驱动的追踪id
     * @return 返回初始化好的service
     */
    private ChromeDriverService initService(String driverId) {
        ChromeDriverService service = getServiceHolder();
        try {
            if (service == null || !service.isRunning()) {
                service = new ChromeDriverService.Builder().usingDriverExecutable(new File(setDto.getDriverPath())).usingAnyFreePort().build();
                service.start();
                addServiceHolder(driverId, service);
            }
        }
        catch (IOException e) {
            throw new InitException("初始化service失败", e);
        }
        return service;
    }

    /**
     * 关闭驱动,可以用在afterMethod
     */
    public void quitDriver() {
        // 先关闭代理再关闭driver
        RequestProxy.closeProxy();
        WebDriver driver = getDriverHolder();
        driver.quit();
        ChromeDriverService service = getServiceHolder();
        service.close();
        removeDriver();
        ReqHolder.removeTraceId();
    }


    /**
     * 构建运行跟踪id和driver跟踪id关系
     *
     * @param driverId driver跟踪id
     * @return 返回driver跟踪id
     */
    private String buildRelationId(String driverId) {
        // 生成一个这次运行的跟踪id
        String traceId = ReqHolder.buildId();
        // 如果没有请求id则默认生成一个，和对应的driver和service绑定在一起
        driverId = StringUtils.isBlank(driverId) ? ReqHolder.buildId() : driverId;
        // 运行跟踪的id和driver的id绑定一对一关系
        traceIdMap.put(traceId, driverId);
        ReqHolder.addTraceId(traceId);
        return driverId;
    }

    /**
     * 初始化webDriver
     *
     * @param abstractChromeOption 自己定义初始化好的chromeOption
     * @param driverId             driverId追踪id,如果为非空则表示初始化一个新的driver,如果是非空则使用之前使用的driver
     * @return 返回初始化好的driver对应的追踪id
     */
    public String initWebDriver(AbstractChromeOption abstractChromeOption, String driverId) {
        boolean noNeedToOpenNew = StringUtils.isNotBlank(driverId);
        driverId = buildRelationId(driverId);
        if (noNeedToOpenNew) {
            log.warn("driverId:{}存在,不需要再次初始化", driverId);
            return driverId;
        }
        ChromeDriverService service = initService(driverId);
        if (abstractChromeOption == null) {
            // 如果没有则使用默认的配置
            abstractChromeOption = new MyChromeOption();
        }
        WebDriver driver = new ChromeDriverProxy(service, abstractChromeOption.createChromeOption());
        String[] windowSize = setDto.getWindowSize().split(",");
        if (windowSize.length != 3) {
            throw new InitException("请检查窗口大小的参数格式!");
        }
        String isMaxWindow = windowSize[0];
        if (BooleanUtils.isTrue(Boolean.parseBoolean(isMaxWindow))) {
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
        addDriverHolder(driverId, driver);
        return driverId;
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
        JSONObject params = new JSONObject();
        params.set("behavior", "allow");
        params.set("downloadPath", setDto.getDownloadPath());
        // 直接使用cdp的方式来运行命令,不需要走http协议
        driverService.executeCdpCommand("Page.setDownloadBehavior", params);
        // commandParams.set("params", params);
        // String url = StrUtil.format("{}/session/{}/chromium/send_command", service.getUrl(), driverService.getSessionId());
        // HttpUtil.post(url, commandParams.toString());
    }


}
