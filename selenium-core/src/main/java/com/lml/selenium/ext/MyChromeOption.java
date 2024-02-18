package com.lml.selenium.ext;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lml.selenium.dto.SetDto;
import com.lml.selenium.factory.SeleniumFactory;
import com.lml.selenium.proxy.RequestProxy;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;

import java.util.Map;
import java.util.logging.Level;

/**
 * @author yugi
 * @apiNote 默认自定义的chrome配置项
 * @since 2022-07-19
 */
public class MyChromeOption implements AbstractChromeOption {


    @Override
    public ChromeOptions createChromeOption() {
        SetDto setDto = SeleniumFactory.getSetDto();
        ChromeOptions options = new ChromeOptions();
        LoggingPreferences logPrefs = new LoggingPreferences();
        //解决新版的403出错问题
        options.addArguments("--remote-allow-origins=*");
        logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
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
        options.addArguments("--safebrowsing-disable-download-protection");
        // 禁用阻止弹出窗口
        // options.addArguments("--disable-popup-blocking");
        Map<String, Object> prefs = Maps.newHashMap();
        prefs.put("credentials_enable_service", false);
        // 禁用保存密码提示框
        prefs.put("profile.password_manager_enabled", false);
        // 是否打开下载弹窗
        prefs.put("download.prompt_for_download", setDto.getPromptForDownload());
        // 文件默认的下载路径
        // prefs.put("download.default_directory", setDto.getDownloadPath());
        options.setExperimentalOption("prefs", prefs);
        options.setCapability(ChromeOptions.LOGGING_PREFS, logPrefs);
        // options.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
        options.setAcceptInsecureCerts(true);
        options.setExperimentalOption("useAutomationExtension", false);

        options.addArguments("--ignore-certificate-errors");

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
