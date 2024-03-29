package com.lml.selenium.dto;

import lombok.Data;

/**
 * @author yugi
 * @apiNote 用来存放配置文件的属性
 * @since 2019-04-30
 */
@Data
public class SetDto {

    /**
     * 要打开的地址
     */
    private String baseUrl;


    /**
     * 驱动文件的地址
     */
    private String driverPath;


    /**
     * 等待的默认时间(单位:毫秒)
     */
    private Integer doWait;

    /**
     * 等待页面或者js脚本加载完成的最长时间(单位:毫秒)
     */
    private Long maxWaitTime;


    /**
     * 等待页面加载每次轮询间隔的时间(单位:毫秒)
     */
    private Integer interval;

    /**
     * fluentWaitUntil,显式等待超时时间(单位:秒)
     */
    private Integer timeOutInSeconds;

    /**
     * fluentWaitUntil显式等待时间(单位:毫秒)
     */
    private Long sleepInMillis;

    /**
     * 查找元素最大尝试次数
     */
    private Integer attemptsTime;

    /**
     * jq的地址
     */
    private String jqueryUrl;

    /**
     * 用例是从db加载还是从excel加载
     */
    private Boolean useDb;

    /**
     * 是否调试模式
     */
    private Boolean debugMode;

    /**
     * api接口地址
     */
    private String apiUrl;

    /**
     * 执行报错屏幕截图保存的地址
     */
    private String errorPic;
}
