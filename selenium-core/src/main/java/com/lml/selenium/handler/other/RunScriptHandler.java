package com.lml.selenium.handler.other;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.lml.selenium.dto.BaseSeleniumDto;
import com.lml.selenium.dto.NoEleHandlerDto;
import com.lml.selenium.dto.RunMethodDto;
import com.lml.selenium.dto.RunScriptDto;
import com.lml.selenium.enums.ActionEnum;
import com.lml.selenium.util.BizUtil;
import com.lml.selenium.util.JsUtil;

/**
 * @author yugi
 * @apiNote 执行脚本的处理器
 * @since 2019-05-05
 */
public class RunScriptHandler implements OtherHandler {

    private final static RunMethodHandler RUN_METHOD_HANDLER = new RunMethodHandler();

    @Override
    public void doHandle(BaseSeleniumDto baseSeleniumDto) {
        NoEleHandlerDto noEleHandlerDto = (NoEleHandlerDto) baseSeleniumDto;
        RunScriptDto runScriptDto = noEleHandlerDto.getRunScriptDto();
        Object[] args = runScriptDto.getArgs();
        String script = runScriptDto.getScript();
        Object jsReturnVal = ArrayUtil.isEmpty(args) ? JsUtil.runJs(script) : JsUtil.runJs(script, args);
        this.handleCallBack(runScriptDto, jsReturnVal);
    }

    /**
     * 如果有定义回调函数,则处理回调函数
     *
     * @param runScriptDto {@link RunScriptDto}
     * @param jsReturnVal  执行js脚本返回来的结果
     */
    private void handleCallBack(RunScriptDto runScriptDto, Object jsReturnVal) {
        String callFn = runScriptDto.getCallFn();
        if (StrUtil.isBlank(callFn)) {
            return;
        }
        RunMethodDto dto = JSONUtil.toBean(callFn, RunMethodDto.class);
        if (jsReturnVal != null) {
            // 如果js有返回值,再查看回调函数的参数个数,如果比RunMethodDto的args个数要多,则表示需要把js返回的结果也要作为入参带给回调函数
            Object[] params = BizUtil.appendCallbackMethodArgs(dto) ? ArrayUtil.insert(dto.getArgs(), 0, jsReturnVal) : new Object[]{jsReturnVal};
            dto.setArgs(params);
        }
        RUN_METHOD_HANDLER.invokeMethod(dto);
    }


    @Override
    public ActionEnum getAction() {
        return ActionEnum.RUN_SCRIPT;
    }


}
