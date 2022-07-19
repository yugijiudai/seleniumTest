package com.lml.selenium.util;

import com.lml.selenium.exception.BizException;
import com.lml.selenium.factory.SeleniumFactory;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

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
     * 选择上传或者下载文件
     *
     * @param fileName           文件的名字,可以带上盘符,如:D:\\1.png
     * @param waitDownLoadPrompt 等待弹窗出来的时间,因为有些点击下载后弹窗未必弹出来，所以这里需要设置一个等待时间(时间:毫秒)
     * @param waitDownLoad       按完回车后下载或者上传文件的等待时间,如果文件比较大,这里的等待时间需要设置长一点(时间:毫秒)
     */
    public void selectFile(String fileName, Integer waitDownLoadPrompt, Integer waitDownLoad) {
        if (!SeleniumFactory.getSetDto().getPromptForDownload()) {
            log.warn("弹窗下载没有开启!");
        }
        WebUtil.doWait(waitDownLoadPrompt);
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
            WebUtil.doWait(waitDownLoad);
        }
        catch (Exception e) {
            throw new BizException(e);
        }
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
