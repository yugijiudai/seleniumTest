package com.lml.selenium.enums;

import lombok.Getter;

/**
 * @author yugi
 * @apiNote js等待方式的枚举类
 * @since 2022-08-05
 */
@Getter
public enum JsWaitEnum {


    /**
     * 通过css选择器查找
     */
    SCHEDULED("scheduled", "通过java定时任务方式等待"),

    LOOP("loop", "通过轮询的方式等待"),

    /**
     * 这个是通过用字符串脚本的方法注入，并不是所有的网页都能支持这方法，例如chrome的chrome://download页面
     */
    JS("js", "通过js原生的轮询等待方式等待"),

    ;

    /**
     * 对应的类型
     */
    private String type;

    /**
     * 相关描述
     */
    private String info;

    JsWaitEnum(String type, String info) {
        this.type = type;
        this.info = info;
    }

    public static JsWaitEnum parse(String type) {
        for (JsWaitEnum value : values()) {
            if (value.getType().equals(type)) {
                return value;
            }
        }
        throw new EnumConstantNotPresentException(JsWaitEnum.class, "找不到type:" + type);
    }


}
