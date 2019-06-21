package com.lml.selenium.enums;

import lombok.Getter;

/**
 * @author yugi
 * @apiNote 切换frame的方式
 * @since 2019-04-30
 */
@Getter
public enum SwitchFrameActionEnum {

    /**
     * 切换到父级frame
     */
    PARENT("parent", "切换到父级frame"),

    /**
     * 切到default
     */
    DEFAULT("default", "切到default"),

    /**
     * 切到指定的frame
     */
    SELF("self", "切到指定的frame"),

    ;

    /**
     * 对应的code
     */
    private String code;

    /**
     * 相关描述
     */
    private String desc;


    SwitchFrameActionEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static SwitchFrameActionEnum parse(String code) {
        for (SwitchFrameActionEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        throw new EnumConstantNotPresentException(SwitchFrameActionEnum.class, "找不到code:" + code);
    }


}