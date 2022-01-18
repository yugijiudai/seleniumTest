package com.lml.selenium.handler.element;

import com.lml.selenium.dto.BaseSeleniumDto;
import com.lml.selenium.dto.EleHandlerDto;
import com.lml.selenium.enums.ActionEnum;
import com.lml.selenium.factory.WaitFactory;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;

/**
 * @author yugi
 * @apiNote 切换frame处理器
 * @since 2019-04-30
 */
@Slf4j
public class SwitchToFrameHandler implements ElementHandler {


    @Override
    public void doHandle(BaseSeleniumDto baseSeleniumDto) {
        EleHandlerDto handleDto = (EleHandlerDto) baseSeleniumDto;
        Wait<WebDriver> wait = WaitFactory.createDefaultWait();
        wait.until(webDriver -> ExpectedConditions.frameToBeAvailableAndSwitchToIt(handleDto.getElement()));
        log.debug("切换[" + handleDto.getBy() + "],frame成功");
    }

    @Override
    public boolean preHandle(EleHandlerDto handleDto) {
        return true;
    }


    @Override
    public ActionEnum getAction() {
        return ActionEnum.SWITCH_TO_FRAME;
    }


}
