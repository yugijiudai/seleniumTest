package com.lml.selenium.enums;

import lombok.Getter;

/**
 * @author yugi
 * @apiNote 动作的枚举类
 * @since 2019-04-30
 */
@Getter
public enum ActionEnum {

    /**
     * 点击
     */
    CLICK("click", "点击", true),

    /**
     * 双击
     */
    DOUBLE_CLICK("doubleClick", "双击", true),

    /**
     * 右键
     */
    RIGHT_CLICK("rightClick", "右键", true),

    /**
     * 填写需要的东西
     */
    SEND_KEYS("sendKeys", "填写需要的东西", true),


    /**
     * 清空输入栏
     */
    CLEAR("clear", "清空输入栏", true),


    /**
     * 鼠标停留在控件上
     */
    HOVER("hover", "鼠标停留在控件上", true),


    /**
     * 获取文本框上的内容
     */
    GET_TEXT("getText", "获取文本框上的内容", true),

    /**
     * 拖拉
     */
    DRAG("drag", "拖拉", false),

    /**
     * 点击alert框
     */
    ALERT("alert", "点击alert框", false),

    /**
     * 显式等待
     */
    WAIT("waitTime", "显式等待", false),


    /**
     * 运行自己定义的脚本
     */
    RUN_SCRIPT("runScript", "运行自己定义的脚本", false),


    /**
     * 刷新页面
     */
    REFRESH("refresh", "刷新页面", false),

    /**
     * 通过执行指定的方法
     */
    RUN_METHOD("runMethod", "通过执行指定的方法", false),

    /**
     * 切换window
     */
    SWITCH_WINDOW("switchWindow", "切换window", false),

    /**
     * 切换到父级或者default的frame
     */
    SWITCH_MY_FRAME("switchMyFrame", "切换到父级或者default的frame", false),

    ;


    /**
     * 对应的code
     */
    private String code;

    /**
     * 相关描述
     */
    private String desc;

    /**
     * 是否需要查找节点
     */
    private boolean needToFindDom;

    ActionEnum(String code, String desc, boolean needToFindDom) {
        this.code = code;
        this.desc = desc;
        this.needToFindDom = needToFindDom;
    }
}
