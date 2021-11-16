package com.lml.selenium.proxy;

import com.google.common.collect.Lists;
import com.lml.selenium.util.ParamUtil;
import com.lml.selenium.vo.ChromeResponseVo;
import lombok.Data;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.core.har.HarEntry;
import net.lightbody.bmp.core.har.HarRequest;
import net.lightbody.bmp.proxy.CaptureType;
import org.openqa.selenium.Proxy;

import java.util.List;

/**
 * @author yugi
 * @apiNote 基础的请求代理类, 用来获取请求参数和响应内容等信息
 * @since 2021-11-16
 */
@Data
public class RequestProxy {

    /**
     * bmp代理
     */
    private BrowserMobProxy browserMobProxy;

    private List<ChromeResponseVo> voList = Lists.newArrayList();


    public RequestProxy(BrowserMobProxy browserMobProxy) {
        this.browserMobProxy = browserMobProxy;
    }

    /**
     * 生成selenium的代理
     */
    public Proxy createProxy() {
        this.browserMobProxy.start();
        // this.browserMobProxy.setHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT);
        this.browserMobProxy.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT);
        this.browserMobProxy.newHar("radar");
        return ClientUtil.createSeleniumProxy(this.browserMobProxy);
    }


    /**
     * 请求过后的后置操作
     */
    public void afterRequest() {
        // Har har = this.browserMobProxy.getHar();
        // List<HarEntry> entries = har.getLog().getEntries();
        // for (HarEntry harEntry : entries) {
        //     HarRequest request = harEntry.getRequest();
        //     String url = harEntry.getRequest().getUrl();
        //     if (request.getMethod().equals("POST")) {
        //         System.out.println("url:{}" + url + "{}" + harEntry.getRequest().getPostData().getText());
        //     }
        // }
    }

    public void addFilter() {
        // 监听网络请求
        browserMobProxy.addRequestFilter((request, contents, messageInfo) -> {
            String url = messageInfo.getUrl();
            if ("application/json".equals(contents.getContentType()) && !ParamUtil.isStaticResource(url)) {
                System.out.println("请求:" + url);
                // ChromeResponseVo chromeResponseVo = new ChromeResponseVo();
                // chromeResponseVo.setUrl(request.uri()).setRequestParam(contents.getTextContents());
                // voList.add(chromeResponseVo);
            }
            return null;
        });


        browserMobProxy.addResponseFilter((response, contents, messageInfo) -> {
            String url = messageInfo.getUrl();
            if (!ParamUtil.isStaticResource(url)) {
                System.out.println("响应:" + url);
            }
        });


    }

}
