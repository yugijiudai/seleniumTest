package com.lml.selenium.handler.element;

import com.lml.selenium.dto.EleHandleDto;
import com.lml.selenium.exception.FindElementException;
import com.lml.selenium.handler.SeleniumHandler;
import com.lml.selenium.util.WebUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
     * @param handleDto {@link EleHandleDto}
     * @return 如果是true表示可以往下执行
     */
    boolean preHandle(EleHandleDto handleDto);


    /**
     * 重复查找和执行动作（click或sendKeys等），当出现引用的element过时后，重新查找该element
     *
     * @param handleDto {@link EleHandleDto}
     * @return {@link WebElement}
     */
    default WebElement retryingFindAndDoAction(EleHandleDto handleDto) {
        WebElement element = null;
        int attempts = 0;
        By by = handleDto.getBy();
        Integer retry = handleDto.getRetry();
        // 如果没有指定,用默认的
        int attemptsTime = retry != null ? retry : WebUtil.getSetDto().getAttemptsTime();
        while (attempts++ < attemptsTime) {
            try {
                element = WebUtil.fluentWaitUntilFind(handleDto);
                handleDto.setElement(element);
                if (this.preHandle(handleDto)) {
                    this.doHandle(handleDto);
                    break;
                }
            }
            catch (Exception e) {
                if (attempts == attemptsTime) {
                    String message = e.getMessage();
                    log.error("执行{}时尝试{}次仍然发生错误:{}", by, attemptsTime, message);
                    throw new FindElementException(message);
                }
                log.warn("执行动作:{}操作节点{}时,发生错误,重试第{}次,异常如下:{}", handleDto.getActionEnum(), by, attempts, e.getMessage());
            }
        }
        return element;
    }
}
