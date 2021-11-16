package com.lml.selenium.proxy;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.lml.selenium.util.ParamUtil;
import com.lml.selenium.vo.BrowserVo;
import io.netty.handler.codec.http.HttpMethod;
import lombok.Data;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.core.har.HarEntry;
import net.lightbody.bmp.core.har.HarNameValuePair;
import net.lightbody.bmp.core.har.HarPostData;
import net.lightbody.bmp.core.har.HarRequest;
import net.lightbody.bmp.core.har.HarResponse;
import net.lightbody.bmp.proxy.CaptureType;
import org.openqa.selenium.Proxy;

import java.nio.charset.Charset;
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


    public RequestProxy(BrowserMobProxy browserMobProxy) {
        this.browserMobProxy = browserMobProxy;
    }

    /**
     * 生成selenium的代理
     */
    public Proxy createProxy() {
        this.browserMobProxy.start();
        this.browserMobProxy.setHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT, CaptureType.REQUEST_HEADERS);
        this.browserMobProxy.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT, CaptureType.REQUEST_HEADERS);
        this.browserMobProxy.newHar("radar");
        return ClientUtil.createSeleniumProxy(this.browserMobProxy);
    }

    /**
     * 请求过后的后置操作,目前只记录get和post的方法,并且只会记录响应是json格式的数据
     */
    public void afterRequest() {
        Har har = this.browserMobProxy.getHar();
        List<HarEntry> entries = har.getLog().getEntries();
        List<BrowserVo> getList = Lists.newArrayList();
        List<BrowserVo> postList = Lists.newArrayList();
        for (HarEntry harEntry : entries) {
            HarRequest request = harEntry.getRequest();
            HarResponse response = harEntry.getResponse();
            // 这里用包含json来判断，因为有些content-type不按正常格式来写
            if (!response.getContent().getMimeType().contains("json") || ParamUtil.isStaticResource(request.getUrl())) {
                continue;
            }
            this.handleRequestMethod(getList, postList, request, response);
        }
        Console.log("post请求了{}个", postList.size());
        Console.log("get请求了{}个", getList.size());
        this.outPutFile(getList, "Z:\\request.json");
        this.outPutFile(postList, "Z:\\post.json");
    }

    /**
     * 根据requestMethod来设置请求参数和响应体
     *
     * @param getList  get请求的结果集
     * @param postList post请求的结果集
     * @param request  请求
     * @param response 响应体
     */
    private void handleRequestMethod(List<BrowserVo> getList, List<BrowserVo> postList, HarRequest request, HarResponse response) {
        String method = request.getMethod();
        BrowserVo browserVo = new BrowserVo().setUrl(request.getUrl()).setResponseBody(JSONUtil.parseObj(response.getContent().getText()));
        if (HttpMethod.POST.name().equals(method)) {
            HarPostData postData = request.getPostData();
            browserVo.setRequestParam(postData == null ? null : JSONUtil.parseObj(postData.getParams()));
            postList.add(browserVo);
        }
        else if (HttpMethod.GET.name().equals(method)) {
            List<HarNameValuePair> param = request.getQueryString();
            browserVo.setRequestParam((CollectionUtil.isNotEmpty(param) ? null : param));
            getList.add(browserVo);
        }
    }

    /**
     * 输出文件
     *
     * @param list get或者post的请求list
     * @param file 文件的路径
     */
    private void outPutFile(List<BrowserVo> list, String file) {
        FileUtil.writeString(JSONUtil.toJsonStr(list), FileUtil.file(file), Charset.defaultCharset());
    }


    public void addFilter() {
        browserMobProxy.addRequestFilter((request, contents, messageInfo) -> null);
    }

}
