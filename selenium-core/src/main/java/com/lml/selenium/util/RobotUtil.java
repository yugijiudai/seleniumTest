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
import java.io.File;

/**
 * @author yugi
 * @apiNote 机器人工具类
 * @since 2022-07-19
 */
@UtilityClass
@Slf4j
public class RobotUtil {


    private int robotDelay = 300;


    /**
     * 自己定义的robot延迟时间
     *
     * @param delay 延迟时间(单位:秒)
     */
    private void setRobotDelay(int delay) {
        robotDelay = delay;
    }

    /**
     * 选择上传或者下载文件,这个只适用于有弹窗的下载和上传，如果没有开启弹窗会有问题
     *
     * @param fileName           文件的名字,可以带上盘符,如:D:\\1.png
     * @param waitDownLoadPrompt 等待弹窗出来的时间,因为有些点击下载后弹窗未必弹出来，所以这里需要设置一个等待时间(时间:毫秒)
     * @param isDownload         是否下载文件
     * @return 如果是下载文件则返回下载后文件的路径
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
        File oldFile = FileUtil.file(oldFileName);
        while (!oldFile.canWrite()) {
            log.warn("文件{}不可写,等待重试", oldFile.getPath());
            WebUtil.doWait(100);
        }
        File newFile = FileUtil.rename(oldFile, fileName, true);
        log.info("文件下载和重命名成功,新名字为:{}", newFile.getPath());
        return newFile.getPath();
    }

    /**
     * 这里利用复制和粘贴的方法处理弹窗下载或者上传
     *
     * @param fileName   新文件的名字，必须非空
     * @param isDownload 标识上传还是下载
     * @return 如果是下载则返回下载的文件路径
     */
    private String handlePrompt(String fileName, boolean isDownload) {
        if (StringUtils.isBlank(fileName)) {
            throw new BizException("文件的名字不能为空!");
        }
        String name = setFilePathByOs(fileName);
        log.info("文件地址:{}", name);
        StringSelection filepath = new StringSelection(name);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(filepath, null);
        try {
            Robot robot = new Robot();
            if (EnvUtil.getOs().contains("Mac")) {
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
        WebUtil.doWait(300);
        WebDriver driver = SeleniumFactory.getDriver();
        String mainWindow = driver.getWindowHandle();
        JsUtil.runJs("window.open('', '_blank')");
        WebUtil.switchToWindow(null);
        driver.get("chrome://downloads/");
        // 先等待一下，shadowRoot有可能会没出来导致脚本执行报错
        JsUtil.waitUntilJsReady();
        // 确保这个页面有打开,出现下载内容这个dom
        WaitUtl.waitLoadByJs("return document.querySelector(\"body > downloads-manager\").shadowRoot.querySelector(\"#toolbar\").shadowRoot.querySelector(\"#toolbar\") !== null");
        String downloadTaskScript = "document.querySelector('downloads-manager').shadowRoot.querySelector('#downloadsList downloads-item').shadowRoot";
        String progress = StrUtil.format("return {}.querySelector('#progress') === null || {}.querySelector('#progress').value === 100", downloadTaskScript, downloadTaskScript);
        WaitUtl.waitLoadByJs(progress);
        // String fileName = JsUtil.runJs(StrUtil.format("return {}.querySelector('div#content #file-link').text", downloadTaskScript));
        String downloadPath = JsUtil.runJs(StrUtil.format("return {}.querySelector('#show').title", downloadTaskScript));
        // String downLoadedAt = JsUtil.runJs(StrUtil.format("return {}.querySelector('div.is-active.focus-row-active #file-icon-wrapper img').src", downloadTaskScript));
        // String sourceURL = JsUtil.runJs(StrUtil.format("return {}.querySelector('div#content #file-link').href", downloadTaskScript));
        driver.close();
        driver.switchTo().window(mainWindow);
        return downloadPath;
    }

    /**
     * window的处理
     *
     * @param robot {@link Robot}
     */
    private void handleWin(Robot robot) {
        keyPress(robot, KeyEvent.VK_CONTROL);
        keyPress(robot, KeyEvent.VK_A);
        keyRelease(robot, KeyEvent.VK_A);
        keyPress(robot, KeyEvent.VK_V);
        keyRelease(robot, KeyEvent.VK_CONTROL);
        keyRelease(robot, KeyEvent.VK_V);
        keyPress(robot, KeyEvent.VK_ENTER);
        keyRelease(robot, KeyEvent.VK_ENTER);
    }

    /**
     * mac系统的处理(注:mac系统路径是无效的)
     *
     * @param robot {@link Robot}
     */
    private void handleMac(Robot robot) {
        keyPress(robot, KeyEvent.VK_META);
        keyPress(robot, KeyEvent.VK_TAB);
        keyRelease(robot, KeyEvent.VK_META);
        keyRelease(robot, KeyEvent.VK_TAB);
        keyPress(robot, KeyEvent.VK_META);
        keyPress(robot, KeyEvent.VK_A);
        keyRelease(robot, KeyEvent.VK_A);
        keyPress(robot, KeyEvent.VK_V);
        keyRelease(robot, KeyEvent.VK_META);
        keyRelease(robot, KeyEvent.VK_V);
        keyPress(robot, KeyEvent.VK_ENTER);
        keyRelease(robot, KeyEvent.VK_ENTER);
        // 可能会出现后缀名不同，所以要两次回车
        keyPress(robot, KeyEvent.VK_ENTER);
        keyRelease(robot, KeyEvent.VK_ENTER);
    }


    /**
     * 带有等待的按键
     *
     * @param robot   机器人对象
     * @param keycode 要按的键
     */
    private void keyPress(Robot robot, int keycode) {
        robot.keyPress(keycode);
        robot.delay(robotDelay);
    }

    /**
     * 带有等待的释放按键
     *
     * @param robot   机器人对象
     * @param keycode 要释放的按键
     */
    private void keyRelease(Robot robot, int keycode) {
        robot.keyRelease(keycode);
        robot.delay(robotDelay);
    }

    /**
     * 根据操作系统设置文件的名字
     *
     * @param fileName 文件名字
     * @return mac直接返回文件名字, win系统需要拼接全路径, 因为在弹窗的时候下载目录无法修改
     */
    private String setFilePathByOs(String fileName) {
        if (EnvUtil.getOs().contains("Mac")) {
            return fileName;
        }
        return getFileFullPath(fileName);
    }

    /**
     * 文件的全路径
     *
     * @param fileName 文件名称
     * @return 拼接好的全路径
     */
    public String getFileFullPath(String fileName) {
        String downloadPath = SeleniumFactory.getSetDto().getDownloadPath();
        return FileUtil.file(downloadPath + "/" + fileName).getPath();
    }

}
