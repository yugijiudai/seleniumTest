package com.lml.selenium.handler.other;

import com.google.common.collect.Lists;
import com.lml.selenium.dto.BaseSeleniumDto;
import com.lml.selenium.dto.NoEleHandlerDto;
import com.lml.selenium.enums.ActionEnum;
import com.lml.selenium.factory.SeleniumFactory;
import com.lml.selenium.util.WebUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;

/**
 * @author yugi
 * @apiNote 切换window处理器
 * @since 2019-04-30
 */
@Slf4j
public class SwitchWindowHandler implements OtherHandler {

    @Override
    public void doHandle(BaseSeleniumDto baseSeleniumDto) {
        NoEleHandlerDto noEleHandlerDto = (NoEleHandlerDto) baseSeleniumDto;
        Set<String> windowHandles = SeleniumFactory.getDriver().getWindowHandles();
        List<String> list = Lists.newArrayList(windowHandles);
        log.info("当前窗口有:{}", list);
        String window = list.get(Integer.parseInt(noEleHandlerDto.getExt()));
        WebUtil.switchToWindow(window);
        log.info("切换window:{}成功,当前url是:{}", window, SeleniumFactory.getDriver().getCurrentUrl());
    }


    @Override
    public ActionEnum getAction() {
        return ActionEnum.SWITCH_WINDOW;
    }

}
