package com.lml.selenium.handler.other;

import com.lml.selenium.dto.BaseSeleniumDto;
import com.lml.selenium.enums.ActionEnum;
import com.lml.selenium.util.WebUtil;

/**
 * @author yugi
 * @apiNote js原生alert框处理
 * @since 2019-05-05
 */
public class AlertHandler implements OtherHandler {


    @Override
    public ActionEnum getAction() {
        return ActionEnum.ALERT;
    }

    @Override
    public void doHandle(BaseSeleniumDto baseSeleniumDto) {
        // 先等待一下
        WebUtil.doWait(500);
        WebUtil.clickAlert();
    }
}
