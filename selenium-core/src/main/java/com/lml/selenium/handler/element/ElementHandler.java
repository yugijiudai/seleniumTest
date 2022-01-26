package com.lml.selenium.handler.element;

import com.lml.selenium.dto.EleHandlerDto;
import com.lml.selenium.handler.SeleniumHandler;
import com.lml.selenium.util.WebUtil;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author yugi
 * @apiNote 元素处理器
 * @since 2019-04-30
 */
public interface ElementHandler extends SeleniumHandler {

    Logger log = LoggerFactory.getLogger(ElementHandler.class);


    /**
     * 处理前的一些条件
     *
     * @param handleDto {@link EleHandlerDto}
     * @return 如果是true表示可以往下执行
     */
    boolean preHandle(EleHandlerDto handleDto);


    /**
     * 先查找元素,然后查找到元素后对其进行处理
     *
     * @param handleDto {@link EleHandlerDto}
     * @return {@link WebElement}
     */
    default List<WebElement> retryingFindAndDoAction(EleHandlerDto handleDto) {
        List<WebElement> webElements = WebUtil.fluentWaitUntilFind(handleDto);
        handleDto.setElements(webElements);
        if (this.preHandle(handleDto)) {
            this.doHandle(handleDto);
        }
        return webElements;
    }

}
