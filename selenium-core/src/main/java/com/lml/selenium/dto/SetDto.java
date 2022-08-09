package com.lml.selenium.dto;

import com.lml.selenium.enums.JsWaitEnum;
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
     * 显式等待的时间(单位:毫秒)
     */
    private Long maxWaitTime;

    /**
     * 等待次轮询间隔的时间,因为轮训不适宜设置过长，这里使用int(单位:毫秒)
     */
    private Integer interval;


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

    /**
     * 是否使用bmp代理来获取请求参数和响应结果
     */
    private Boolean useBmpProxy;

    /**
     * 窗口大小,格式如下:true,1920,1080
     * 第一个如果是true则表示全屏,后面两个不生效,当第一个为false的时候使用后面两个分辨率
     */
    private String windowSize;

    /**
     * 是否使用无头
     */
    private Boolean useNoHead;

    /**
     * 下载的时候是否开启弹窗
     */
    private Boolean promptForDownload;

    /**
     * 下载文件的默认路径
     */
    private String downloadPath;

    /**
     * 使用waitUtil进行等待的时候默认方式,分别有scheduled,loop,js三种,可以参考
     *
     * @see JsWaitEnum
     */
    private String jsWaitType;

}
