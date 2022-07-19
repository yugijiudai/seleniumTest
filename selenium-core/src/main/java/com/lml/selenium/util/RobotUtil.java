package com.lml.selenium.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.lml.selenium.exception.BizException;
import com.lml.selenium.factory.SeleniumFactory;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;

/**
 * @author yugi
 * @apiNote 机器人工具类
 * @since 2022-07-19
 */
@UtilityClass
@Slf4j
public class RobotUtil {


    /**
     * 选择上传或者下载文件,这个只适用于有弹窗的下载和上传，如果没有开启弹窗会有问题
     *
     * @param fileName           文件的名字,可以带上盘符,如:D:\\1.png
     * @param waitDownLoadPrompt 等待弹窗出来的时间,因为有些点击下载后弹窗未必弹出来，所以这里需要设置一个等待时间(时间:毫秒)
     * @param isDownload         是否下载文件
     * @return 如果是下载文件则返回下载后文件的名字
     */
    public String selectFile(String fileName, Integer waitDownLoadPrompt, boolean isDownload) {
        WebUtil.doWait(waitDownLoadPrompt);
        if (!SeleniumFactory.getSetDto().getPromptForDownload() && isDownload) {
            return handleNoPrompt(fileName);
        }
        return handlePrompt(fileName, isDownload);
    }

    /**
     * 处理没有弹窗的下载
     *
     * @param fileName 如果为空则用浏览器默认下载的名字，非空则会使用新的名字
     * @return 返回下载的文件名字
     */
    private String handleNoPrompt(String fileName) {
        log.warn("弹窗下载没有开启");
        String oldFileName = waitUntilDownloadCompleted();
        if (StringUtils.isBlank(fileName)) {
            return oldFileName;
        }
        FileUtil.rename(FileUtil.file(SeleniumFactory.getSetDto().getDownloadPath() + "/" + oldFileName), fileName, true);
        log.info("文件下载和重命名成功,新名字为:{}", fileName);
        return fileName;
    }

    /**
     * 这里利用复制和粘贴的方法处理弹窗下载或者上传
     *
     * @param fileName   新文件的名字，必须非空
     * @param isDownload 标识上传还是下载
     * @return 如果是下载则返回下载的文件名字
     */
    private String handlePrompt(String fileName, boolean isDownload) {
        if (StringUtils.isBlank(fileName)) {
            throw new BizException("文件的名字不能为空!");
        }
        StringSelection filepath = new StringSelection(fileName);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(filepath, null);
        try {
            Robot robot = new Robot();
            String os = System.getProperty("os.name");
            if (os.contains("Mac")) {
                handleMac(robot);
            }
            else {
                handleWin(robot);
            }
            if (isDownload) {
                return waitUntilDownloadCompleted();
            }
            return null;
        }
        catch (Exception e) {
            throw new BizException(e);
        }
    }


    /**
     * 等待文件下载
     *
     * @return 返回下载后文件的名字
     */
    public String waitUntilDownloadCompleted() {
        WebDriver driver = SeleniumFactory.getDriver();
        String mainWindow = driver.getWindowHandle();
        JsUtil.runJs("window.open()");
        for (String winHandle : driver.getWindowHandles()) {
            WebUtil.switchToWindow(winHandle);
        }
        driver.get("chrome://downloads");
        String downloadTaskScript = "document.querySelector('downloads-manager').shadowRoot.querySelector('#downloadsList downloads-item').shadowRoot";
        Boolean hasDownloadTask = JsUtil.runJs(StrUtil.format("return {}.querySelector('#progress') !== null", downloadTaskScript));
        if (hasDownloadTask) {
            Long percentage = 0L;
            while (percentage != 100) {
                percentage = JsUtil.runJs(StrUtil.format("return {}.querySelector('#progress').value", downloadTaskScript));
                log.debug("下载进度:{}", percentage);
                WebUtil.doWait(200);
            }
        }
        String fileName = JsUtil.runJs(StrUtil.format("return {}.querySelector('div#content #file-link').text", downloadTaskScript));
        // String downLoadedAt = JsUtil.runJs(StrUtil.format("return {}.querySelector('div.is-active.focus-row-active #file-icon-wrapper img').src", downloadTaskScript));
        // String sourceURL = JsUtil.runJs(StrUtil.format("return {}.querySelector('div#content #file-link').href", downloadTaskScript));
        // close the downloads tab2
        driver.close();
        // switch back to main window
        driver.switchTo().window(mainWindow);
        return fileName;
    }

    /**
     * window的处理
     *
     * @param robot {@link Robot}
     */
    private void handleWin(Robot robot) {
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_CONTROL);
        robot.delay(100);
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
    }

    /**
     * mac系统的处理(注:mac系统路径是无效的)
     *
     * @param robot {@link Robot}
     */
    private void handleMac(Robot robot) {
        robot.keyPress(KeyEvent.VK_META);
        robot.keyPress(KeyEvent.VK_TAB);
        robot.keyRelease(KeyEvent.VK_META);
        robot.keyRelease(KeyEvent.VK_TAB);
        robot.delay(100);
        robot.keyPress(KeyEvent.VK_META);
        robot.keyPress(KeyEvent.VK_A);
        robot.keyRelease(KeyEvent.VK_A);
        robot.keyPress(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_META);
        robot.keyRelease(KeyEvent.VK_V);
        robot.delay(100);
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.delay(100);
        robot.keyRelease(KeyEvent.VK_ENTER);
        robot.delay(100);
        // 可能会出现后缀名不同，所以要两次回车
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.delay(100);
        robot.keyRelease(KeyEvent.VK_ENTER);
    }

}
