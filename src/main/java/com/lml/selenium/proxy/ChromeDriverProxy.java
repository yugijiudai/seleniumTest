package com.lml.selenium.proxy;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.lml.selenium.vo.ChromeResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.Logs;

import java.util.ArrayList;
import java.util.List;
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
    public static final int CHROME_DRIVER_PORT = 9101;


    public ChromeDriverProxy(ChromeDriverService driverService, ChromeOptions options) {
        super(driverService, options);
    }


    /**
     * 根据请求ID获取返回内容
     *
     * @param requestId 请求id
     * @return 返回的内容
     */
    public String getResponseBody(String requestId) {
        try {
            // CHROME_DRIVER_PORT chromeDriver提供的端口
            String url = String.format("http://localhost:%s/session/%s/goog/cdp/execute", CHROME_DRIVER_PORT, this.getSessionId());
            JSONObject paramMap = JSONUtil.createObj();
            paramMap.set("cmd", NETWORK_RESPONSE_BODY_CMD).set("params", JSONUtil.createObj().set("requestId", requestId));
            String result = HttpRequest.post(url).body(JSONUtil.toJsonStr(paramMap)).execute().body();
            return this.getResponseValue(result);
        }
        catch (Exception e) {
            log.error("getResponseBody failed!", e);
        }
        return null;
    }


    /**
     * 根据请求ID获取返回cookies
     * https://github.com/bayandin/chromedriver/blob/master/server/http_handler.cc
     *
     * @param requestId 请求id
     * @return cookie
     */
    public JSONArray getCookies(String requestId) {
        try {
            // CHROME_DRIVER_PORT chromeDriver提供的端口
            String url = String.format("http://localhost:%s/session/%s/cookie", CHROME_DRIVER_PORT, getSessionId());
            String cookieStr = HttpRequest.get(url).execute().body();
            return JSONUtil.parseArray(getResponseValue(cookieStr));
        }
        catch (Exception e) {
            log.error("获取cookie失败!", e);
        }
        return null;
    }

    /**
     * 判断结果并且返回value的值
     *
     * @param data 有响应结果
     * @return 只返回value的值
     */
    private String getResponseValue(String data) {
        JSONObject object = JSONUtil.parseObj(data);
        if (0 != object.getInt("status")) {
            throw new RuntimeException(StrUtil.format("status error:{}", JSONUtil.toJsonStr(data)));
        }
        return object.getStr("value");
    }

    /**
     * 保存返回请求的内容
     *
     * @param driver 对应的驱动
     */
    public static void saveHttpTransferDataIfNecessary(ChromeDriverProxy driver) {
        Logs logs = driver.manage().logs();
        Set<String> availableLogTypes = logs.getAvailableLogTypes();
        if (availableLogTypes.contains(LogType.PERFORMANCE)) {
            LogEntries logEntries = logs.get(LogType.PERFORMANCE);
            List<ChromeResponseVo> responseReceivedEvents = new ArrayList<>();
            for (LogEntry entry : logEntries) {
                JSONObject jsonObj = JSONUtil.parseObj(entry.getMessage()).getJSONObject("message");
                String method = jsonObj.getStr("method");
                JSONObject params = jsonObj.getJSONObject("params");
                String url = params.getJSONObject("response").getStr("url");
                if (NETWORK_RESPONSE_RECEIVED.equals(method) && driver.isNeedSave(url)) {
                    ChromeResponseVo response = JSONUtil.toBean(params, ChromeResponseVo.class);
                    responseReceivedEvents.add(response);
                }
            }
            doSaveHttpTransferDataIfNecessary(driver, responseReceivedEvents);
        }
    }

    private boolean isNeedSave(String url) {
        boolean staticFiles = url.endsWith(".png")
                || url.endsWith(".jpg")
                || url.endsWith(".css")
                || url.endsWith(".ico")
                || url.endsWith(".js")
                || url.endsWith(".gif")
                || url.endsWith(".svg")
                || url.endsWith(".woff2");
        return !staticFiles && url.startsWith("http");
    }


    /**
     * 解析对应的返回内容列表，并且对其操作
     *
     * @param driver    对应的驱动
     * @param responses 返回的列表
     */
    private static void doSaveHttpTransferDataIfNecessary(ChromeDriverProxy driver, List<ChromeResponseVo> responses) {
        for (ChromeResponseVo chromeResponseVo : responses) {
            String url = JSONUtil.parseObj(chromeResponseVo.getResponse()).getStr("url");
            boolean staticFiles = url.endsWith(".png")
                    || url.endsWith(".jpg")
                    || url.endsWith(".css")
                    || url.endsWith(".ico")
                    || url.endsWith(".js")
                    || url.endsWith(".gif")
                    || url.endsWith(".svg")
                    || url.endsWith(".woff2");

            if (!staticFiles && url.startsWith("http")) {
                // 使用上面开发的接口获取返回数据
                String body = driver.getResponseBody(chromeResponseVo.getRequestId());
                log.info("url:{}\n body:{}", url, JSONUtil.parseObj(body));
                // JSONArray cookies = driver.getCookies(responseReceivedEvent.getRequestId());
                // System.out.println("url:" + url + " ,cookies-->" + cookies);
            }
        }
    }


}
