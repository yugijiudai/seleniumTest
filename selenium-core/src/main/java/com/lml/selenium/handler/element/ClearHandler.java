package com.lml.selenium.handler.element;

import com.lml.selenium.dto.BaseSeleniumDto;
import com.lml.selenium.dto.EleHandlerDto;
import com.lml.selenium.enums.ActionEnum;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * @author yugi
 * @apiNote 清除处理
 * @since 2019-04-30
 */
@Slf4j
public class ClearHandler implements ElementHandler {


    @Override
    public boolean preHandle(EleHandlerDto handleDto) {
        return true;
    }

    @Override
    public ActionEnum getAction() {
        return ActionEnum.CLEAR;
    }

    @Override
    public void doHandle(BaseSeleniumDto baseSeleniumDto) {
        EleHandlerDto dto = (EleHandlerDto) baseSeleniumDto;
        List<WebElement> elements = dto.getElements();
        for (WebElement element : elements) {
            element.clear();
        }
    }
}
