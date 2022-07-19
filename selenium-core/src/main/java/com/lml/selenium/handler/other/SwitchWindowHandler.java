package com.lml.selenium.handler.other;

import com.google.common.collect.Lists;
import com.lml.selenium.dto.BaseSeleniumDto;
import com.lml.selenium.dto.NoEleHandlerDto;
import com.lml.selenium.enums.ActionEnum;
import com.lml.selenium.factory.SeleniumFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.openqa.selenium.WebDriver;

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
        WebDriver driver = SeleniumFactory.getDriver();
        Set<String> windowHandles = driver.getWindowHandles();
        List<String> list = Lists.newArrayList(windowHandles);
        log.info("当前窗口有:{}", list);
        String ext = noEleHandlerDto.getExt();
        // 如果ext是数字则切换到指定数字编号的窗口,如果不是则取最后一个打开的窗口
        String window = NumberUtils.isDigits(ext) ? list.get(Integer.parseInt(noEleHandlerDto.getExt())) : list.get(list.size() - 1);
        driver.switchTo().window(window);
        log.info("切换window:{}成功,当前url是:{}", window, driver.getCurrentUrl());
    }


    @Override
    public ActionEnum getAction() {
        return ActionEnum.SWITCH_WINDOW;
    }

}
