package com.lml.selenium.handler.other;

import com.lml.selenium.dto.BaseSeleniumDto;
import com.lml.selenium.dto.NoEleHandleDto;
import com.lml.selenium.enums.ActionEnum;
import com.lml.selenium.util.WebUtil;

/**
 * @author yugi
 * @apiNote 等待的处理器
 * @since 2019-05-05
 */
public class WaitHandler implements OtherHandler {


    @Override
    public ActionEnum getAction() {
        return ActionEnum.WAIT;
    }

    @Override
    public void doHandle(BaseSeleniumDto baseSeleniumDto) {
        NoEleHandleDto noEleHandleDto = (NoEleHandleDto) baseSeleniumDto;
        WebUtil.doWait(noEleHandleDto.getWaitTime());
    }
}
