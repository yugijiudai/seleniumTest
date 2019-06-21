package com.lml.selenium.util;

import com.lml.selenium.exception.BizException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;

/**
 * @author yugi
 * @apiNote 上传工具类
 * @since 2019-05-21
 */
@UtilityClass
@Slf4j
public class UploadUtil {

    private static final String FILE = "D:\\1.png";


    /**
     * 选择上传文件(用于非input type=file的上传)
     */
    public void selectUploadFile() {
        //  FIXME yugi 2019/5/16:  这里切换的时候可能需要等待,不然填写文件的时候可能会发生错误
        WebUtil.doWait(500);
        StringSelection filepath = new StringSelection(FILE);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(filepath, null);
        try {
            Robot robot = new Robot();
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_V);
            robot.keyRelease(KeyEvent.VK_V);
            robot.keyRelease(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_ENTER);
            robot.keyRelease(KeyEvent.VK_ENTER);
        }
        catch (Exception e) {
            throw new BizException(e);
        }
    }
}
