package com.lml.selenium.handler.other;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.lml.selenium.dto.BaseSeleniumDto;
import com.lml.selenium.dto.NoEleHandlerDto;
import com.lml.selenium.enums.ActionEnum;
import com.lml.selenium.enums.SwitchFrameActionEnum;
import com.lml.selenium.factory.SeleniumFactory;
import com.lml.selenium.util.JsUtil;
import com.lml.selenium.util.WebUtil;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * @author yugi
 * @apiNote 切换到父级或者default的frame处理器
 * @since 2019-04-30
 */
@Slf4j
public class SwitchMyFrameHandler implements OtherHandler {

    /**
     * 切换的类型
     */
    private static final String TYPE = "type";

    /**
     * 切换到frame的url
     */
    private static final String URL = "url";

    @Override
    public void doHandle(BaseSeleniumDto baseSeleniumDto) {
        NoEleHandlerDto noEleHandlerDto = (NoEleHandlerDto) baseSeleniumDto;
        JSONObject json = JSONUtil.parseObj(noEleHandlerDto.getExt());
        SwitchFrameActionEnum actionEnum = SwitchFrameActionEnum.parse(json.getStr(TYPE));
        WebUtil.getCurrentFrame();
        switch (actionEnum) {
            case PARENT:
                SeleniumFactory.getDriver().switchTo().parentFrame();
                break;
            case SELF:
                WebDriver driver = SeleniumFactory.getDriver();
                driver.switchTo().defaultContent();
                // 切换到最顶级后需要等待一下,不然有可能页面没切换完,js脚本就注入到页面上,导致获取iframe不准确
                WebUtil.doWait(100);
                String script = JsUtil.loadCommonScript(JsUtil.DOM_SCRIPT);
                String handle = String.format("%s return frameHelper.frameObj.findTheFrame('%s');", script, json.getStr(URL));
                List<WebElement> list = JsUtil.runJs(handle);
                for (WebElement element : list) {
                    driver.switchTo().frame(element);
                }
                break;
            default:
                SeleniumFactory.getDriver().switchTo().defaultContent();
                break;
        }
        WebUtil.getCurrentFrame();
    }


    @Override
    public ActionEnum getAction() {
        return ActionEnum.SWITCH_MY_FRAME;
    }

}
