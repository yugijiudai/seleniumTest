package com.lml.selenium.handler.other;

import com.lml.selenium.dto.BaseSeleniumDto;
import com.lml.selenium.dto.NoEleHandlerDto;
import com.lml.selenium.enums.ActionEnum;
import com.lml.selenium.util.JsUtil;

/**
 * @author yugi
 * @apiNote 等待页面加载的处理器
 * @since 2019-05-05
 */
public class WaitHandler implements OtherHandler {


    @Override
    public ActionEnum getAction() {
        return ActionEnum.WAIT;
    }

    @Override
    public void doHandle(BaseSeleniumDto baseSeleniumDto) {
        NoEleHandlerDto noEleHandlerDto = (NoEleHandlerDto) baseSeleniumDto;
        JsUtil.waitPageLoad(noEleHandlerDto.getWaitTime());
    }
}
