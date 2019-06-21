package com.lml.selenium.dto;

import com.lml.selenium.enums.ActionEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * @author yugi
 * @apiNote selenium数据传输抽象类
 * @since 2019-05-05
 */
@Setter
@Getter
public abstract class BaseSeleniumDto {

    /**
     * {@link ActionEnum}
     */
    protected ActionEnum actionEnum;
}
