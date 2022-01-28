package com.lml.selenium.factory;

import com.lml.selenium.dto.EleHandlerDto;
import com.lml.selenium.entity.Selenium;
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
public class EleHandlerDtoFactory {


    /**
     * 构建dto
     *
     * @param selenium {@link Selenium}
     * @return {@link EleHandlerDto}
     */
    public EleHandlerDto buildDto(Selenium selenium) {
        ActionEnum action = selenium.getElementAction();
        EleHandlerDto eleHandlerDto = buildCommon(selenium);
        switch (action) {
            case CLICK:
                return buildClick(selenium.getClickAction(), eleHandlerDto);
            case CLEAR:
                return buildClear(eleHandlerDto);
            case SEND_KEYS:
                return buildSendKeys(selenium.getExt(), eleHandlerDto);
            case HOVER:
                return buildHover(eleHandlerDto);
            case GET_TEXT:
                return buildGetText(eleHandlerDto);
            default:
                throw new BizException("找不到对应的构建器!");
        }
    }

    /**
     * 构建click所需要的参数
     *
     * @param clickAction {@link ClickActionEnum}
     * @return {@link EleHandlerDto}
     */
    public EleHandlerDto buildClick(ClickActionEnum clickAction, EleHandlerDto eleHandlerDto) {
        eleHandlerDto.setActionExecuteMethod(clickAction).setActionEnum(ActionEnum.CLICK);
        return eleHandlerDto;
    }

    /**
     * 构建sendKeys所需要的参数
     *
     * @param keys          要输入的文字
     * @param eleHandlerDto {@link EleHandlerDto}
     * @return {@link EleHandlerDto}
     */
    public EleHandlerDto buildSendKeys(String keys, EleHandlerDto eleHandlerDto) {
        eleHandlerDto.setKeys(keys).setActionEnum(ActionEnum.SEND_KEYS);
        return eleHandlerDto;
    }

    /**
     * 构建clear所需要的参数
     *
     * @param eleHandlerDto {@link EleHandlerDto}
     * @return {@link EleHandlerDto}
     */
    public EleHandlerDto buildClear(EleHandlerDto eleHandlerDto) {
        eleHandlerDto.setActionEnum(ActionEnum.CLEAR);
        return eleHandlerDto;
    }


    /**
     * 构建hover所需要的参数
     *
     * @param eleHandlerDto {@link EleHandlerDto}
     * @return {@link EleHandlerDto}
     */
    public EleHandlerDto buildHover(EleHandlerDto eleHandlerDto) {
        eleHandlerDto.setActionEnum(ActionEnum.HOVER);
        return eleHandlerDto;
    }

    /**
     * 构建getText所需要的参数
     *
     * @param eleHandlerDto {@link EleHandlerDto}
     * @return {@link EleHandlerDto}
     */
    public EleHandlerDto buildGetText(EleHandlerDto eleHandlerDto) {
        eleHandlerDto.setActionEnum(ActionEnum.GET_TEXT);
        return eleHandlerDto;
    }


    /**
     * 通用的构建
     *
     * @param by 要查询的节点
     * @return 返回公用的部分
     */
    public EleHandlerDto buildCommon(By by) {
        return EleHandlerDto.builder().by(by).build();
    }

    /**
     * 通用的构建
     *
     * @param selenium {@link Selenium}
     * @return 返回公用的部分
     */
    private EleHandlerDto buildCommon(Selenium selenium) {
        By by = SelectFactory.getSelector(selenium);
        return EleHandlerDto.builder().by(by).retry(selenium.getRetry()).waitTime(selenium.getWait()).build();
    }
}
