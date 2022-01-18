package com.lml.selenium.factory;

import com.lml.selenium.entity.Selenium;
import com.lml.selenium.enums.FindTypeEnum;
import com.lml.selenium.exception.BizException;
import lombok.experimental.UtilityClass;
import org.openqa.selenium.By;

/**
 * @author yugi
 * @apiNote 选择器工厂
 * @since 2019-05-06
 */
@UtilityClass
public class SelectFactory {


    /**
     * 根据查找类型和元素,获得元素查找器
     *
     * @param selenium {@link Selenium}
     * @return 元素查找器
     */
    By getSelector(Selenium selenium) {
        FindTypeEnum findType = selenium.getFindType();
        switch (findType) {
            case ID:
                return By.id(selenium.getElement());
            case TAG_NAME:
                return By.tagName(selenium.getElement());
            case XPATH:
                return By.xpath(selenium.getElement());
            case CLASS_NAME:
                return By.className(selenium.getElement());
            case NAME:
                return By.name(selenium.getElement());
            case LINK_TEXT:
                return By.linkText(selenium.getElement());
            case CSS_SELECTOR:
                return By.cssSelector(selenium.getElement());
            default:
                throw new BizException("请传入正确的查找方式!");
        }
    }

    /**
     * 根据查找类型和元素,获得元素查找器
     *
     * @param findType 查找的方式
     * @param ele      要查找的元素
     * @return 元素查找器
     */
    public By getSelector(String findType, String ele) {
        Selenium selenium = new Selenium().setFindType(FindTypeEnum.parse(findType)).setElement(ele);
        return getSelector(selenium);
    }


}
