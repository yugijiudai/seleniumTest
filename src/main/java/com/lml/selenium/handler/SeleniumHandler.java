package com.lml.selenium.handler;

import com.lml.selenium.dto.BaseSeleniumDto;
import com.lml.selenium.enums.ActionEnum;

/**
 * @author yugi
 * @apiNote selenium总的处理器
 * @since 2019-05-05
 */
public interface SeleniumHandler {

    /**
     * 获取当前动作的类型
     *
     * @return {@link ActionEnum}
     */
    ActionEnum getAction();


    /**
     * 对应的处理方法
     *
     * @param baseSeleniumDto {@link BaseSeleniumDto}
     */
    void doHandle(BaseSeleniumDto baseSeleniumDto);
}
