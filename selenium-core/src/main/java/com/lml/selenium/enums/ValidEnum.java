package com.lml.selenium.enums;

import lombok.Getter;

/**
 * @author yugi
 * @apiNote 是否有效
 * @since 2019-05-05
 */
@Getter
public enum ValidEnum {

    /**
     * 有效
     */
    Y("Y", "有效"),

    /**
     * 无效
     */
    N("N", "无效"),

    ;

    /**
     * 对应的code
     */
    private String code;

    /**
     * 相关描述o
     */
    private String desc;

    ValidEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
