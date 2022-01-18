package com.lml.selenium.handler.element;

import com.lml.selenium.dto.BaseSeleniumDto;
import com.lml.selenium.dto.EleHandleDto;
import com.lml.selenium.enums.ActionEnum;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yugi
 * @apiNote 清除处理
 * @since 2019-04-30
 */
@Slf4j
public class ClearHandler implements ElementHandler {


    @Override
    public boolean preHandle(EleHandleDto handleDto) {
        return true;
    }

    @Override
    public ActionEnum getAction() {
        return ActionEnum.CLEAR;
    }

    @Override
    public void doHandle(BaseSeleniumDto baseSeleniumDto) {
        EleHandleDto dto = (EleHandleDto) baseSeleniumDto;
        dto.getElement().clear();
    }
}
