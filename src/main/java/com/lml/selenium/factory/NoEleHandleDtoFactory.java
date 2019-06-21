package com.lml.selenium.factory;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.lml.selenium.entity.Selenium;
import com.lml.selenium.dto.NoEleHandleDto;
import com.lml.selenium.enums.ActionEnum;
import com.lml.selenium.exception.BizException;
import lombok.experimental.UtilityClass;

/**
 * @author yugi
 * @apiNote 用来构建NoEleHandleDto
 * @since 2019-05-06
 */
@UtilityClass
public class NoEleHandleDtoFactory {

    /**
     * js脚本参数的key
     */
    private static final String ARGS = "args";

    /**
     * js脚本回调函数的key
     */
    private static final String FN = "fn";

    /**
     * 构建NoEleHandleDto
     *
     * @param selenium {@link Selenium}
     * @return {@link NoEleHandleDto}
     */
    public NoEleHandleDto buildDto(Selenium selenium) {
        ActionEnum action = selenium.getElementAction();
        switch (action) {
            case RUN_SCRIPT:
                return buildScript(selenium);
            case WAIT:
                return buildWait(selenium);
            case RUN_METHOD:
                return buildRunMethod(selenium);
            case SWITCH_WINDOW:
                return buildSwitchWindow(selenium);
            case ALERT:
                return buildAlert();
            case REFRESH:
                return buildRefresh();
            case SWITCH_MY_FRAME:
                return buildSwitchMyFrame(selenium);
            default:
                throw new BizException("找不到对应的构建器!");
        }
    }

    /**
     * 构建script需要用的参数
     *
     * @param selenium {@link Selenium}
     * @return {@link NoEleHandleDto}
     */
    private NoEleHandleDto buildScript(Selenium selenium) {
        NoEleHandleDto noEleHandleDto = buildCommon();
        String ext = selenium.getExt();
        if (StrUtil.isNotBlank(ext)) {
            // 设置这个脚本需要的参数和回调函数
            JSONObject obj = JSONUtil.parseObj(ext);
            String str = obj.getStr(ARGS);
            noEleHandleDto.setArgs(JSONUtil.parseArray(str).toArray());
            String callBack = obj.getStr(FN);
            noEleHandleDto.setCallFn(callBack);
        }
        return noEleHandleDto.setScript(selenium.getScript());
    }

    /**
     * 构建wait需要用的参数
     *
     * @param selenium {@link Selenium}
     * @return {@link NoEleHandleDto}
     */
    private NoEleHandleDto buildWait(Selenium selenium) {
        return buildCommon().setWaitTime(Integer.parseInt(selenium.getExt()));
    }

    /**
     * 构建runMethod需要用的参数
     *
     * @param selenium {@link Selenium}
     * @return {@link NoEleHandleDto}
     */
    private NoEleHandleDto buildRunMethod(Selenium selenium) {
        return buildCommon().setExt(selenium.getExt());
    }

    /**
     * 构建switchWindow需要用的参数
     *
     * @param selenium {@link Selenium}
     * @return {@link NoEleHandleDto}
     */
    private NoEleHandleDto buildSwitchWindow(Selenium selenium) {
        return buildCommon().setExt(selenium.getExt());
    }

    /**
     * 构建switchMyFrame需要用的参数
     *
     * @param selenium {@link Selenium}
     * @return {@link NoEleHandleDto}
     */
    private NoEleHandleDto buildSwitchMyFrame(Selenium selenium) {
        return buildCommon().setExt(selenium.getExt());
    }

    /**
     * 构建alert需要用的参数
     *
     * @return {@link NoEleHandleDto}
     */
    private NoEleHandleDto buildAlert() {
        return buildCommon();
    }

    /**
     * 构建refresh需要用的参数
     *
     * @return {@link NoEleHandleDto}
     */
    private NoEleHandleDto buildRefresh() {
        return buildCommon();
    }

    /**
     * 构建通用的参数
     *
     * @return {@link NoEleHandleDto}
     */
    private NoEleHandleDto buildCommon() {
        return new NoEleHandleDto();
    }
}
