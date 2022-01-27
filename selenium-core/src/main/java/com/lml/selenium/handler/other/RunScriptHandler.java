package com.lml.selenium.handler.other;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.lml.selenium.dto.BaseSeleniumDto;
import com.lml.selenium.dto.NoEleHandlerDto;
import com.lml.selenium.dto.RunMethodDto;
import com.lml.selenium.enums.ActionEnum;
import com.lml.selenium.util.JsUtil;

/**
 * @author yugi
 * @apiNote 执行脚本的处理器
 * @since 2019-05-05
 */
public class RunScriptHandler implements OtherHandler {


    @Override
    public void doHandle(BaseSeleniumDto baseSeleniumDto) {
        NoEleHandlerDto noEleHandlerDto = (NoEleHandlerDto) baseSeleniumDto;
        Object[] args = noEleHandlerDto.getArgs();
        String script = noEleHandlerDto.getScript();
        Object jsReturnVal = ArrayUtil.isEmpty(args) ? JsUtil.runJs(script) : JsUtil.runJs(script, args);
        this.handleCallBack(noEleHandlerDto, jsReturnVal);
    }

    /**
     * 如果有定义回调函数,则处理回调函数
     *
     * @param noEleHandlerDto {@link NoEleHandlerDto}
     * @param jsReturnVal     执行js脚本返回来的结果
     */
    private void handleCallBack(NoEleHandlerDto noEleHandlerDto, Object jsReturnVal) {
        String callFn = noEleHandlerDto.getCallFn();
        if (StrUtil.isNotBlank(callFn)) {
            RunMethodDto dto = JSONUtil.toBean(callFn, RunMethodDto.class);
            if (jsReturnVal != null) {
                dto.setArgs(new Object[]{jsReturnVal});
            }
            RunMethodHandler handler = new RunMethodHandler();
            handler.invokeMethod(dto);
        }
    }


    @Override
    public ActionEnum getAction() {
        return ActionEnum.RUN_SCRIPT;
    }


}
