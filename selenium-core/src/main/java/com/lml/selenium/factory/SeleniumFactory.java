package com.lml.selenium.factory;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.setting.dialect.Props;
import com.google.common.collect.Maps;
import com.lml.selenium.dto.SetDto;
import com.lml.selenium.entity.Selenium;
import com.lml.selenium.enums.DataSourceEnum;
import com.lml.selenium.enums.ValidEnum;
import com.lml.selenium.exception.InitException;
import com.lml.selenium.proxy.ChromeDriverProxy;
import com.lml.selenium.proxy.RequestProxy;
import com.lml.selenium.util.DbUtil;
import com.lml.selenium.util.JSWaiter;
import com.lml.selenium.util.ValidationUtils;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.testng.Assert;

import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * @author yugi
 * @apiNote selenium初始化的工厂
 * @since 2019-05-05
 */
@UtilityClass
@Slf4j
public class SeleniumFactory {


    private ChromeDriverService service;

    @Getter
    private WebDriver driver;

    /**
     * 配置文件映射到的实体类
     */
    @Getter
    private final SetDto setDto = new SetDto();


    static {
        // 初始化处理器
        HandlerFactory.initAllHandler();
        Props props = new Props("application.properties");
        // 配置映射到对应的实体类中
        BeanUtil.copyProperties(props, setDto);
        log.info("初始化成功,配置是:{}", setDto);
    }


    /**
     * 关闭驱动
     */
    public void quitDriver() {
        RequestProxy.closeProxy();
        driver.close();
        service.stop();
    }


    /**
     * 初始化
     */
    public void webDriverInit() {
        try {
            service = new ChromeDriverService.Builder().usingDriverExecutable(new File(setDto.getDriverPath())).usingAnyFreePort().build();
            service.start();
            ChromeOptions options = createChromeOption();
            driver = new ChromeDriverProxy(service, options);
            if (setDto.getUseMaxWindow()) {
                driver.manage().window().maximize();
            }
            if (setDto.getDebugMode()) {
                // 如果是debug模式,则会开启隐式等待
                driver.manage().timeouts().implicitlyWait(Duration.ofMillis(setDto.getMaxWaitTime()));
            }
            JSWaiter.setDriver(driver);
        }
        catch (Exception e) {
            Assert.fail("初始化失败", e);
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


    /**
     * 从excel或者数据库里面加载相关的数据,从第一行开始,初始化SeleniumBo业务对象
     *
     * @param name excel或者数据库table的名字
     * @param step 从几个开始
     * @param end  运行到第几个
     * @return 返回加载好的数据
     */
    public List<Selenium> initSelenium(String name, int step, int end) {
        log.info("执行:{}的用例...................", name);
        if (setDto.getUseDb()) {
            return initSeleniumByDb(name, step, end);
        }
        return initSeleniumByExcel(name, step, end);
    }


    /**
     * 从数据里面加载相关的数据,指定开始行数,初始化SeleniumBo业务对象
     *
     * @param name 表的名字
     * @param step 从第几步开始(0表示从头开始)
     * @param end  运行到第几步(0表示运行到最后)
     * @return 返回加载好的数据
     */
    private List<Selenium> initSeleniumByDb(String name, int step, int end) {
        try {
            if (end > 0) {
                if (end < step) {
                    throw new InitException("结束的步骤不正确");
                }
                String sql = String.format("select * from %s where id >= ? and id <= ? and valid = 'Y' order by id", name);
                return DbUtil.getDb(DataSourceEnum.SELENIUM).query(sql, Selenium.class, step, end);
            }
            String sql = String.format("select * from %s where id >= ? and valid = 'Y' order by id", name);
            return DbUtil.getDb(DataSourceEnum.SELENIUM).query(sql, Selenium.class, step);
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new InitException(e.getMessage());
        }
    }

    /**
     * 从数据里面加载相关的数据,指定对应的模块,初始化SeleniumBo业务对象
     *
     * @param name      表的名字
     * @param modelName 对应的模块
     * @return 返回加载好的数据
     */
    public List<Selenium> initSeleniumByDb(String name, String modelName) {
        try {
            String sql = String.format("select * from %s where model = ? and valid = 'Y' order by id", name);
            return DbUtil.getDb(DataSourceEnum.SELENIUM).query(sql, Selenium.class, modelName);
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new InitException(e.getMessage());
        }
    }


    /**
     * 从excel里面加载相关的数据,指定开始行数,初始化SeleniumBo业务对象
     *
     * @param name excel的名字
     * @param step 从第几步开始(0表示从头开始)
     * @param end  运行到第几步(0表示运行到最后)
     * @return 返回加载好的数据
     */
    private List<Selenium> initSeleniumByExcel(String name, int step, int end) {
        ExcelReader reader = ExcelUtil.getReader(ResourceUtil.getStream(String.format("case/%s.xls", name)));
        int size = reader.getSheet().getLastRowNum();
        if (step > size) {
            throw new InitException("开始的步骤不正确");
        }
        if (end > 0) {
            if (end < step) {
                throw new InitException("结束的步骤不正确");
            }
            size = end;
        }
        // 这里不能使用readAll直接映射成实体类,因为下拉窗口有可能读到是空字符串,这样会导致报错
        List<Map<String, Object>> read = reader.read(0, step, size);
        List<Selenium> list = read.stream().map(map -> {
            handleEmptyString(map);
            return copyAndCheckContent(map);
        }).filter(seleniumBo -> ValidEnum.Y.equals(seleniumBo.getValid())).collect(Collectors.toList());
        log.info("要操作的情况如下:{}", list);
        return list;
    }

    /**
     * 将map的属性复制到bo类并且校验数据的完整性
     *
     * @param map 要复制的map
     * @return 返回复制和校验好的bo
     */
    private Selenium copyAndCheckContent(Map<String, Object> map) {
        Selenium selenium = new Selenium();
        BeanUtil.copyProperties(map, selenium);
        try {
            ValidationUtils.validate(selenium);
        }
        catch (Exception e) {
            String msg = String.format("id:%s,%s", selenium.getId(), e.getMessage());
            throw new InitException(msg);
        }
        return selenium;
    }


    /**
     * 处理空字符串
     *
     * @param map excel里的数据
     */
    private void handleEmptyString(Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            // 处理下拉窗口读到可能是空字符串
            Object value = entry.getValue();
            if (value != null && StrUtil.isBlank(value.toString())) {
                map.put(entry.getKey(), null);
            }
        }
    }


}
