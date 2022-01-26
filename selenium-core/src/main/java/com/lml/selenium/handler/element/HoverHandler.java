package com.lml.selenium.handler.element;

import com.lml.selenium.dto.BaseSeleniumDto;
import com.lml.selenium.dto.EleHandlerDto;
import com.lml.selenium.enums.ActionEnum;
import com.lml.selenium.factory.SeleniumFactory;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.List;

/**
 * @author yugi
 * @apiNote 鼠标停留处理
 * @since 2019-04-30
 */
@Slf4j
public class HoverHandler implements ElementHandler {


    @Override
    public void doHandle(BaseSeleniumDto baseSeleniumDto) {
        EleHandlerDto handleDto = (EleHandlerDto) baseSeleniumDto;
        Actions mouseHover = new Actions(SeleniumFactory.getDriver());
        List<WebElement> elements = handleDto.getElements();
        for (WebElement element : elements) {
            mouseHover.moveToElement(element).perform();
        }
    }

    @Override
    public boolean preHandle(EleHandlerDto handleDto) {
        return true;
    }

    @Override
    public ActionEnum getAction() {
        return ActionEnum.HOVER;
    }


}
