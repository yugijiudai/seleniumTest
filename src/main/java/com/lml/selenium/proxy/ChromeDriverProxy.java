package com.lml.selenium.proxy;

import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lml.selenium.util.ParamUtil;
import com.lml.selenium.vo.ChromeResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.Command;
import org.openqa.selenium.devtools.v95.network.Network;
import org.openqa.selenium.devtools.v95.network.model.Cookie;
import org.openqa.selenium.devtools.v95.network.model.RequestId;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.Logs;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author yugi
 * @apiNote 扩展chrome的代理
 * @since 2021-11-12
 */

@Slf4j
public class ChromeDriverProxy extends ChromeDriver {
    /**
     * method --> Network.responseReceived
     */
    public static final String NETWORK_RESPONSE_RECEIVED = "Network.responseReceived";

    /**
     * 获取响应体的命令
     */
    private static final String NETWORK_RESPONSE_BODY_CMD = "Network.getResponseBody";

    /**
     * 必须固定端口，因为ChromeDriver没有实时获取端口的接口；
     */
    public static final int CHROME_DRIVER_PORT = 9102;


    public ChromeDriverProxy(ChromeDriverService driverService, ChromeOptions options) {
        super(driverService, options);
    }


    /**
     * 根据请求ID获取返回内容
     * http://localhost:%s/session/%s/goog/cdp/execute
     *
     * @param requestId 请求id
     * @return 返回的内容
     */
    public Map<String, Object> getResponseBody(String requestId) {
        // selenium4提供的新方法
        Command<Network.GetResponseBodyResponse> responseBody = Network.getResponseBody(new RequestId(requestId));
        return super.executeCdpCommand(responseBody.getMethod(), responseBody.getParams());
    }


    /**
     * 获取返回cookies
     * https://github.com/bayandin/chromedriver/blob/master/server/http_handler.cc
     * http://localhost:%s/session/%s/cookie
     *
     * @return cookie
     */
    public Map<String, Object> getCookies() {
        // CHROME_DRIVER_PORT chromeDriver提供的端口
        Command<List<Cookie>> allCookies = Network.getAllCookies();
        return super.executeCdpCommand(allCookies.getMethod(), Maps.newHashMap());
    }


    /**
     * 保存返回请求的内容
     *
     * @param driver 对应的驱动
     */
    public static List<ChromeResponseVo> saveHttpTransferDataIfNecessary(ChromeDriverProxy driver) {
        Logs logs = driver.manage().logs();
        Set<String> availableLogTypes = logs.getAvailableLogTypes();
        if (!availableLogTypes.contains(LogType.PERFORMANCE)) {
            log.warn("没有记录到相关的请求");
            return null;
        }
        LogEntries logEntries = logs.get(LogType.PERFORMANCE);
        List<ChromeResponseVo> responseVoList = Lists.newArrayList();
        for (LogEntry entry : logEntries) {
            JSONObject jsonObj = JSONUtil.parseObj(entry.getMessage()).getJSONObject("message");
            String method = jsonObj.getStr("method");
            if (!NETWORK_RESPONSE_RECEIVED.equals(method)) {
                continue;
            }
            JSONObject params = jsonObj.getJSONObject("params");
            String url = JSONUtil.getByPath(params, "$.response.url").toString();
            if (!ParamUtil.isStaticResource(url) && "XHR".equals(params.getStr("type"))) {
                ChromeResponseVo vo = new ChromeResponseVo();
                vo.setHeader(params.getJSONObject("response").getJSONObject("headers"));
                Map<String, Object> responseBody = driver.getResponseBody(params.getStr("requestId"));
                vo.setUrl(url).setBody(responseBody.get("body")).setBase64Encoded(MapUtil.getBool(responseBody, "base64Encoded"));
                responseVoList.add(vo);
            }
        }
        System.out.println(responseVoList.size());
        for (ChromeResponseVo chromeResponseVo : responseVoList) {
            log.info("{}", JSONUtil.toJsonStr(chromeResponseVo));
        }
        return responseVoList;
    }

}
