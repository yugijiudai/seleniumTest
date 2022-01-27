package com.lml.selenium.factory;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.lml.selenium.dto.NoEleHandlerDto;
import com.lml.selenium.entity.Selenium;
import com.lml.selenium.enums.ActionEnum;
import com.lml.selenium.exception.BizException;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

/**
 * @author yugi
 * @apiNote 用来构建NoEleHandleDto
 * @since 2019-05-06
 */
@UtilityClass
public class NoEleHandlerDtoFactory {

    /**
     * js脚本参数的key
     */
    private static final String ARGS = "args";

    /**
     * js脚本的key
     */
    private static final String SCRIPT = "script";

    /**
     * js脚本回调函数的key
     */
    private static final String CALL_FN = "callFn";

    /**
     * 构建NoEleHandleDto
     *
     * @param selenium {@link Selenium}
     * @return {@link NoEleHandlerDto}
     */
    public NoEleHandlerDto buildDto(Selenium selenium) {
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
            case DRAG:
                return buildDrag(selenium);
            default:
                throw new BizException("找不到对应的构建器!");
        }
    }

    /**
     * 构建script需要用的参数
     * ext的格式如下:
     * {
     * 'args': ['123'], js运行所需要的参数，非必填
     * 'script': 'console.log(111)', 要运行的js脚本
     * 'callFn': {} js运行完要执行的回调方法,可以把js的返回值作为参数带给这个回调方法, 格式和RunMethodHandler的ext一样
     * }
     *
     * @param selenium {@link Selenium}
     * @return {@link NoEleHandlerDto}
     */
    private NoEleHandlerDto buildScript(Selenium selenium) {
        NoEleHandlerDto noEleHandlerDto = buildCommon();
        String ext = selenium.getExt();
        if (StringUtils.isBlank(ext) || !JSONUtil.isJsonObj(ext)) {
            throw new IllegalArgumentException("执行脚本的格式不对或者空了!");
        }
        JSONObject obj = JSONUtil.parseObj(ext);
        String script = obj.getStr(SCRIPT);
        if (StringUtils.isBlank(script)) {
            throw new IllegalArgumentException("要执行的脚本不能是空!");
        }
        String args = obj.getStr(ARGS);
        if (StringUtils.isNotBlank(args)) {
            noEleHandlerDto.setArgs(JSONUtil.parseArray(args).toArray());
        }
        return noEleHandlerDto.setCallFn(obj.getStr(CALL_FN)).setScript(script);
    }

    /**
     * 构建wait需要用的参数
     *
     * @param selenium {@link Selenium}
     * @return {@link NoEleHandlerDto}
     */
    private NoEleHandlerDto buildWait(Selenium selenium) {
        return buildCommon().setWaitTime(Integer.parseInt(selenium.getExt()));
    }

    /**
     * 构建runMethod需要用的参数
     *
     * @param selenium {@link Selenium}
     * @return {@link NoEleHandlerDto}
     */
    private NoEleHandlerDto buildRunMethod(Selenium selenium) {
        return buildCommon().setExt(selenium.getExt());
    }

    /**
     * 构建switchWindow需要用的参数
     *
     * @param selenium {@link Selenium}
     * @return {@link NoEleHandlerDto}
     */
    private NoEleHandlerDto buildSwitchWindow(Selenium selenium) {
        return buildCommon().setExt(selenium.getExt());
    }

    /**
     * 构建switchMyFrame需要用的参数
     *
     * @param selenium {@link Selenium}
     * @return {@link NoEleHandlerDto}
     */
    private NoEleHandlerDto buildSwitchMyFrame(Selenium selenium) {
        return buildCommon().setExt(selenium.getExt());
    }

    /**
     * 构建拖拉需要用的参数
     *
     * @param selenium {@link Selenium}
     * @return {@link NoEleHandlerDto}
     */
    private NoEleHandlerDto buildDrag(Selenium selenium) {
        return buildCommon().setExt(selenium.getExt());
    }

    /**
     * 构建alert需要用的参数
     *
     * @return {@link NoEleHandlerDto}
     */
    private NoEleHandlerDto buildAlert() {
        return buildCommon();
    }

    /**
     * 构建refresh需要用的参数
     *
     * @return {@link NoEleHandlerDto}
     */
    private NoEleHandlerDto buildRefresh() {
        return buildCommon();
    }

    /**
     * 构建通用的参数
     *
     * @return {@link NoEleHandlerDto}
     */
    private NoEleHandlerDto buildCommon() {
        return new NoEleHandlerDto();
    }
}
