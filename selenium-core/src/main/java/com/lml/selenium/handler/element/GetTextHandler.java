package com.lml.selenium.handler.element;

import com.lml.selenium.dto.BaseSeleniumDto;
import com.lml.selenium.dto.EleHandlerDto;
import com.lml.selenium.enums.ActionEnum;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yugi
 * @apiNote 查找文本处理器
 * @since 2019-04-30
 */
@Slf4j
public class GetTextHandler implements ElementHandler {

    @Override
    public void doHandle(BaseSeleniumDto baseSeleniumDto) {
        EleHandlerDto handleDto = (EleHandlerDto) baseSeleniumDto;
        log.info("查找文本:{}", handleDto.getBy());
    }

    @Override
    public boolean preHandle(EleHandlerDto handleDto) {
        return true;
    }

    @Override
    public ActionEnum getAction() {
        return ActionEnum.GET_TEXT;
    }


}
