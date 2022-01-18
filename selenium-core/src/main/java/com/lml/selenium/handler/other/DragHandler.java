package com.lml.selenium.handler.other;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.lml.selenium.dto.BaseSeleniumDto;
import com.lml.selenium.dto.NoEleHandlerDto;
import com.lml.selenium.enums.ActionEnum;
import com.lml.selenium.factory.EleHandlerDtoFactory;
import com.lml.selenium.factory.SelectFactory;
import com.lml.selenium.factory.SeleniumFactory;
import com.lml.selenium.util.WebUtil;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

/**
 * @author yugi
 * @apiNote 拖拉处理, 格式:{from: {findType: "id", ele: "btn"}, to: {findType: "id", ele: "ipt"}}
 * @since 2019-04-30
 */
@Slf4j
public class DragHandler implements OtherHandler {

    /**
     * 需要拖动的元素前缀
     */
    private static final String FROM = "from";

    /**
     * 元素拖动到的地方前缀
     */
    private static final String TO = "to";

    /**
     * 元素查询的方式
     */
    private static final String FIND_TYPE = "findType";

    /**
     * 查找元素的前缀
     */
    private static final String ELE = "ele";


    @Override
    public ActionEnum getAction() {
        return ActionEnum.DRAG;
    }

    @Override
    public void doHandle(BaseSeleniumDto baseSeleniumDto) {
        NoEleHandlerDto noEleHandlerDto = (NoEleHandlerDto) baseSeleniumDto;
        JSONObject json = JSONUtil.parseObj(noEleHandlerDto.getExt());
        // 查询需要拖动的元素
        JSONObject fromObj = json.getJSONObject(FROM);
        By sourceBy = SelectFactory.getSelector(fromObj.getStr(FIND_TYPE), fromObj.getStr(ELE));
        WebElement source = WebUtil.fluentWaitUntilFind(EleHandlerDtoFactory.buildCommon(sourceBy));

        // 查询需要拖到的地方
        JSONObject toObj = json.getJSONObject(TO);
        By targetBy = SelectFactory.getSelector(toObj.getStr(FIND_TYPE), toObj.getStr(ELE));
        WebElement target = WebUtil.fluentWaitUntilFind(EleHandlerDtoFactory.buildCommon(targetBy));

        Actions action = new Actions(SeleniumFactory.getDriver());
        action.dragAndDrop(source, target).build().perform();
    }
}
