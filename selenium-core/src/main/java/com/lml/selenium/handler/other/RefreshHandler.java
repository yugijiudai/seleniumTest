package com.lml.selenium.handler.other;

import com.lml.selenium.dto.BaseSeleniumDto;
import com.lml.selenium.enums.ActionEnum;
import com.lml.selenium.factory.SeleniumFactory;

/**
 * @author yugi
 * @apiNote 刷新的处理器
 * @since 2019-05-05
 */
public class RefreshHandler implements OtherHandler {


    @Override
    public ActionEnum getAction() {
        return ActionEnum.REFRESH;
    }

    @Override
    public void doHandle(BaseSeleniumDto baseSeleniumDto) {
        SeleniumFactory.getDriver().navigate().refresh();
    }
}
