package com.lml.selenium.handler.other;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ClassUtil;
import com.lml.selenium.dto.BaseSeleniumDto;
import com.lml.selenium.dto.NoEleHandlerDto;
import com.lml.selenium.dto.RunMethodDto;
import com.lml.selenium.enums.ActionEnum;

/**
 * @author yugi
 * @apiNote 执行自定义方法的处理器
 * @since 2019-05-05
 */
public class RunMethodHandler implements OtherHandler {


    @Override
    public ActionEnum getAction() {
        return ActionEnum.RUN_METHOD;
    }

    @Override
    public void doHandle(BaseSeleniumDto baseSeleniumDto) {
        NoEleHandlerDto noEleHandlerDto = (NoEleHandlerDto) baseSeleniumDto;
        this.invokeMethod(noEleHandlerDto.getRunMethodDto());
    }

    /**
     * 调用对应的方法
     *
     * @param runMethodDto {@link RunMethodDto}
     * @return 调用方法后的返回值
     */
    @SuppressWarnings("UnusedReturnValue")
    public Object invokeMethod(RunMethodDto runMethodDto) {
        String className = runMethodDto.getClassName();
        String methodName = runMethodDto.getMethodName();
        Object[] args = runMethodDto.getArgs();
        return ClassUtil.invoke(className, methodName, ArrayUtil.isEmpty(args) ? new Object[]{} : args);
    }
}
