package com.lml.selenium.enums;

import lombok.Getter;

/**
 * @author yugi
 * @apiNote 查找节点的方式
 * @since 2019-05-05
 */
@Getter
public enum FindTypeEnum {

    /**
     * 通过id
     */
    ID("id", "id查找"),

    /**
     * 通过name查找
     */
    NAME("name", "name查找"),

    /**
     * 通过className查找
     */
    CLASS_NAME("className", "className查找"),

    /**
     * 通过标签查找
     */
    TAG_NAME("tagName", "tagName查找"),

    /**
     * 通过xpath查找
     */
    XPATH("xpath", "xpath查找"),

    /**
     * 通过linkText查找
     */
    LINK_TEXT("linkText", "linkText查找"),

    /**
     * 通过css选择器查找
     */
    CSS_SELECTOR("cssSelector", "cssSelector查找"),

    ;

    /**
     * 对应的code
     */
    private String code;

    /**
     * 相关描述
     */
    private String desc;

    FindTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static FindTypeEnum parse(String code) {
        for (FindTypeEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        throw new EnumConstantNotPresentException(FindTypeEnum.class, "找不到code:" + code);
    }

}
