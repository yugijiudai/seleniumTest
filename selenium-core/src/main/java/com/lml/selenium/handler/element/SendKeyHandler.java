package com.lml.selenium.handler.element;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.lml.selenium.dto.BaseSeleniumDto;
import com.lml.selenium.dto.EleHandlerDto;
import com.lml.selenium.enums.ActionEnum;

/**
 * @author yugi
 * @apiNote 在输入框发送输入的指令
 * @since 2019-04-30
 */
public class SendKeyHandler implements ElementHandler {

    /**
     * 是否需要清除再输入
     */
    private static final String NEED_CLEAR = "needClear";

    /**
     * 要输入的keys
     */
    private static final String KEYS = "keys";


    @Override
    public void doHandle(BaseSeleniumDto baseSeleniumDto) {
        EleHandlerDto handleDto = (EleHandlerDto) baseSeleniumDto;
        // 先点击选中,然后再清空
        handleDto.getElement().click();
        String keys = handleDto.getKeys();
        // 为了兼容两种格式,旧的格式key是字符串,新的格式是{needClear: true, keys: []}
        if (!JSONUtil.isJson(keys)) {
            handleDto.getElement().clear();
            handleDto.getElement().sendKeys(keys);
            return;
        }
        JSONObject obj = JSONUtil.parseObj(keys);
        if (obj.getBool(NEED_CLEAR)) {
            // 先清除再输入
            handleDto.getElement().clear();
        }
        handleDto.getElement().sendKeys(ArrayUtil.toArray(obj.getJSONArray(KEYS).toList(String.class), String.class));

    }

    @Override
    public boolean preHandle(EleHandlerDto handleDto) {
        return true;
    }


    @Override
    public ActionEnum getAction() {
        return ActionEnum.SEND_KEYS;
    }

}
