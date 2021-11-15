package com.lml.selenium.vo;

import cn.hutool.json.JSONObject;
import lombok.Data;
import lombok.experimental.Accessors;


/**
 * @author yugi
 * @apiNote chrome代理返回的响应值
 * @since 2021-11-12
 */
@Data
@Accessors(chain = true)
public class ChromeResponseVo {


    /**
     * 响应头
     */
    private JSONObject header;

    /**
     * 请求连接
     */
    private String url;

    /**
     * 请求返回的body
     */
    private Object body;

    /**
     * 是否用base64
     */
    private Boolean base64Encoded;
}
