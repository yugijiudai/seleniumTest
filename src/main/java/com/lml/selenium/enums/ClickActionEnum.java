package com.lml.selenium.enums;

import lombok.Getter;

/**
 * @author yugi
 * @apiNote 点击的方式，调用JS或者Selenium API, JS：通过js方式调用 API:通过API方式调用 BY_TAG_TYPE：根据标签类型决定
 * @since 2019-04-30
 */
@Getter
public enum ClickActionEnum {
    /**
     * 直接js方式
     */
    JS("js", "使用js来点击"),

    /**
     * 使用selenium原生的api来触发
     */
    API("API", "使用原生的方法来触发点击"),

    /**
     * 根据标签类型决定
     */
    BY_TAG_TYPE("byTagType", "根据标签类型决定"),

    /**
     * 双击
     */
    DOUBLE_CLICK("doubleClick", "双击"),

    /**
     * 右键
     */
    RIGHT_CLICK("rightClick", "右键"),

    ;

    /**
     * 对应的code
     */
    private String code;

    /**
     * 相关描述
     */
    private String desc;


    ClickActionEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}