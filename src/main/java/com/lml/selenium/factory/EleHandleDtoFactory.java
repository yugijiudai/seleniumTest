package com.lml.selenium.factory;

import com.lml.selenium.entity.Selenium;
import com.lml.selenium.dto.EleHandleDto;
import com.lml.selenium.enums.ActionEnum;
import com.lml.selenium.enums.ClickActionEnum;
import com.lml.selenium.exception.BizException;
import lombok.experimental.UtilityClass;
import org.openqa.selenium.By;

/**
 * @author yugi
 * @apiNote 用来构建EleHandleDto, 这里拆分成这么多个方法而不是一个大方法是为了以后可以方便给其他地方单独调用和可以清晰的区分出到底是要构建出哪一个动作类型的dto
 * @since 2019-05-06
 */
@UtilityClass
public class EleHandleDtoFactory {


    /**
     * 构建dto
     *
     * @param selenium {@link Selenium}
     * @return {@link EleHandleDto}
     */
    public EleHandleDto buildDto(Selenium selenium) {
        ActionEnum action = selenium.getElementAction();
        EleHandleDto eleHandleDto = buildCommon(selenium);
        switch (action) {
            case CLICK:
                return buildClick(selenium.getClickAction(), eleHandleDto);
            case CLEAR:
                return buildClear(eleHandleDto);
            case SEND_KEYS:
                return buildSendKeys(selenium.getExt(), eleHandleDto);
            case HOVER:
                return buildHover(eleHandleDto);
            case GET_TEXT:
                return buildGetText(eleHandleDto);
            case SWITCH_TO_FRAME:
                return buildSwitchToFrame(eleHandleDto);
            default:
                throw new BizException("找不到对应的构建器!");
        }
    }

    /**
     * 构建click所需要的参数
     *
     * @param clickAction {@link ClickActionEnum}
     * @return {@link EleHandleDto}
     */
    public EleHandleDto buildClick(ClickActionEnum clickAction, EleHandleDto eleHandleDto) {
        eleHandleDto.setActionExecuteMethod(clickAction).setActionEnum(ActionEnum.CLICK);
        return eleHandleDto;
    }

    /**
     * 构建sendKeys所需要的参数
     *
     * @param keys         要输入的文字
     * @param eleHandleDto {@link EleHandleDto}
     * @return {@link EleHandleDto}
     */
    public EleHandleDto buildSendKeys(String keys, EleHandleDto eleHandleDto) {
        eleHandleDto.setKeys(keys).setActionEnum(ActionEnum.SEND_KEYS);
        return eleHandleDto;
    }

    /**
     * 构建clear所需要的参数
     *
     * @param eleHandleDto {@link EleHandleDto}
     * @return {@link EleHandleDto}
     */
    public EleHandleDto buildClear(EleHandleDto eleHandleDto) {
        eleHandleDto.setActionEnum(ActionEnum.CLEAR);
        return eleHandleDto;
    }

    /**
     * 构建switchToFrame所需要的参数
     *
     * @param eleHandleDto {@link EleHandleDto}
     * @return {@link EleHandleDto}
     */
    public EleHandleDto buildSwitchToFrame(EleHandleDto eleHandleDto) {
        eleHandleDto.setActionEnum(ActionEnum.SWITCH_TO_FRAME);
        return eleHandleDto;
    }


    /**
     * 构建hover所需要的参数
     *
     * @param eleHandleDto {@link EleHandleDto}
     * @return {@link EleHandleDto}
     */
    public EleHandleDto buildHover(EleHandleDto eleHandleDto) {
        eleHandleDto.setActionEnum(ActionEnum.HOVER);
        return eleHandleDto;
    }

    /**
     * 构建getText所需要的参数
     *
     * @param eleHandleDto {@link EleHandleDto}
     * @return {@link EleHandleDto}
     */
    public EleHandleDto buildGetText(EleHandleDto eleHandleDto) {
        eleHandleDto.setActionEnum(ActionEnum.GET_TEXT);
        return eleHandleDto;
    }


    /**
     * 通用的构建
     *
     * @param by 要查询的节点
     * @return 返回公用的部分
     */
    public EleHandleDto buildCommon(By by) {
        return EleHandleDto.builder().by(by).build();
    }

    /**
     * 通用的构建
     *
     * @param selenium {@link Selenium}
     * @return 返回公用的部分
     */
    private EleHandleDto buildCommon(Selenium selenium) {
        By by = SelectFactory.getSelector(selenium);
        return EleHandleDto.builder().by(by).retry(selenium.getRetry()).waitTime(selenium.getWait()).build();
    }
}
