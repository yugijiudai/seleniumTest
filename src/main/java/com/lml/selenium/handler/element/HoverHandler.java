package com.lml.selenium.handler.element;

import com.lml.selenium.dto.BaseSeleniumDto;
import com.lml.selenium.dto.EleHandleDto;
import com.lml.selenium.enums.ActionEnum;
import com.lml.selenium.util.WebUtil;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.interactions.Actions;

/**
 * @author yugi
 * @apiNote 鼠标停留处理
 * @since 2019-04-30
 */
@Slf4j
public class HoverHandler implements ElementHandler {


    @Override
    public void doHandle(BaseSeleniumDto baseSeleniumDto) {
        EleHandleDto handleDto = (EleHandleDto) baseSeleniumDto;
        Actions mouseHover = new Actions(WebUtil.driver);
        mouseHover.moveToElement(handleDto.getElement()).perform();
    }

    @Override
    public boolean preHandle(EleHandleDto handleDto) {
        return true;
    }

    @Override
    public ActionEnum getAction() {
        return ActionEnum.HOVER;
    }


}
