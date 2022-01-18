package com.lml.selenium.handler.other;

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
        if (StrUtil.isBlank(script)) {
            throw new IllegalArgumentException("要执行的脚本不能为空!");
        }
        Object obj = args != null && args.length > 0 ? JsUtil.runJs(script, args) : JsUtil.runJs(script);
        this.handleCallBack(noEleHandlerDto, obj);
    }

    /**
     * 如果有定义回调函数,则处理回调函数
     *
     * @param noEleHandlerDto {@link NoEleHandlerDto}
     * @param obj             执行js脚本返回来的结果
     */
    private void handleCallBack(NoEleHandlerDto noEleHandlerDto, Object obj) {
        String callFn = noEleHandlerDto.getCallFn();
        if (StrUtil.isNotBlank(callFn)) {
            RunMethodDto dto = JSONUtil.toBean(callFn, RunMethodDto.class);
            dto.setArgs(new Object[]{obj});
            RunMethodHandler handler = new RunMethodHandler();
            handler.invokeMethod(dto);
        }
    }


    @Override
    public ActionEnum getAction() {
        return ActionEnum.RUN_SCRIPT;
    }


}
