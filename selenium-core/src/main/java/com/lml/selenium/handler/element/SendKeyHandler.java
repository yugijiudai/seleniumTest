package com.lml.selenium.handler.element;

import com.lml.selenium.dto.BaseSeleniumDto;
import com.lml.selenium.dto.EleHandlerDto;
import com.lml.selenium.enums.ActionEnum;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * @author yugi
 * @apiNote 在输入框发送输入的指令
 * @since 2019-04-30
 */
public class SendKeyHandler implements ElementHandler {


    @Override
    public void doHandle(BaseSeleniumDto baseSeleniumDto) {
        EleHandlerDto handleDto = (EleHandlerDto) baseSeleniumDto;
        List<WebElement> elements = handleDto.getElements();
        for (WebElement element : elements) {
            // 先点击选中,然后再清空
            element.click();
            element.clear();
            element.sendKeys(handleDto.getKeys());
        }
    }

    @Override
    public boolean preHandle(EleHandlerDto handleDto) {
        return true;
    }


    @Override
    public ActionEnum getAction() {
        return ActionEnum.SEND_KEYS;
    }

}
