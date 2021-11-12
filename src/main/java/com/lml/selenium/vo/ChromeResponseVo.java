package com.lml.selenium.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author yugi
 * @apiNote chrome代理返回的响应值
 * @since 2021-11-12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChromeResponseVo {

    private String frameId;

    private String requestId;

    private String response;

    private String loaderId;

    private String type;

    private String timestamp;

    private String url;
}
