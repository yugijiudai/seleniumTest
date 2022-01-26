package com.lml.selenium.client;

import com.lml.selenium.dto.EleHandlerDto;
import com.lml.selenium.dto.NoEleHandlerDto;
import com.lml.selenium.entity.Selenium;
import com.lml.selenium.enums.ActionEnum;
import com.lml.selenium.factory.EleHandlerDtoFactory;
import com.lml.selenium.factory.HandlerFactory;
import com.lml.selenium.factory.NoEleHandlerDtoFactory;
import com.lml.selenium.handler.element.ElementHandler;
import com.lml.selenium.handler.other.OtherHandler;
import com.lml.selenium.util.WebUtil;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * @author yugi
 * @apiNote 处理器调用者
 * @since 2019-05-06
 */
@UtilityClass
@Slf4j
public class HandlerClient {


    /**
     * 根据seleniumBo业务对象来调用对应的操作
     *
     * @param selenium {@link Selenium}
     * @return 如果是需要查找节点的操作则返回查找到的节点, 否则返回null
     */
    public List<WebElement> doAction(Selenium selenium) {
        ActionEnum action = selenium.getElementAction();
        WebUtil.getCurrentFrame();
        if (action.isNeedToFindDom()) {
            ElementHandler handler = HandlerFactory.getElementHandler(action);
            EleHandlerDto eleHandlerDto = EleHandlerDtoFactory.buildDto(selenium);
            return handler.retryingFindAndDoAction(eleHandlerDto);
        }
        OtherHandler otherHandler = HandlerFactory.getOtherHandler(action);
        NoEleHandlerDto noEleHandlerDto = NoEleHandlerDtoFactory.buildDto(selenium);
        otherHandler.doHandle(noEleHandlerDto);
        return null;
    }


}
