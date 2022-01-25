package com.lml.selenium.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author yugi
 * @apiNote 用来存放请求的参数和响应等参数
 * @since 2021-11-16
 */
@Data
@Accessors(chain = true)
public class BrowserVo {

    /**
     * 请求体的参数
     */
    private Object requestParam;

    /**
     * 请求连接
     */
    private String url;

    /**
     * 响应体
     */
    private Object responseBody;

    /**
     * 请求的类型
     */
    private String method;
}