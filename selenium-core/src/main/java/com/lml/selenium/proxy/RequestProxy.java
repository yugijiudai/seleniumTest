package com.lml.selenium.proxy;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.lml.selenium.util.ParamUtil;
import com.lml.selenium.vo.BrowserVo;
import io.netty.handler.codec.http.HttpMethod;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
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
@Slf4j
@UtilityClass
public class RequestProxy {

    /**
     * bmp代理
     */
    private BrowserMobProxy browserMobProxy;

    /**
     * 生成selenium的代理
     */
    public Proxy createProxy() {
        // 每次创建都需要新建代理服务器,不然会start不了
        browserMobProxy = new BrowserMobProxyServer();
        browserMobProxy.start();
        browserMobProxy.setHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT, CaptureType.REQUEST_HEADERS);
        browserMobProxy.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT, CaptureType.REQUEST_HEADERS);
        return ClientUtil.createSeleniumProxy(browserMobProxy);
    }

    /**
     * 停止代理
     */
    public void closeProxy() {
        browserMobProxy.stop();
    }

    /**
     * <ol>
     *     <li>请求过后的后置操作,目前只记录get和post的方法,并且只会记录响应是json格式的数据</li>
     *     <li>此方法调用一次就会获取当前的har然后把里面的结果全部提取出来,最后会调用清空的方法，如果想再次调用这个方法需要调用this.browserMobProxy.newHar()创建新的har</li>
     *     <li>如果请求过多har文件会过大,请根据实际情况来判断是否一次获取还是分批获取</li>
     * </ol>
     *
     * @return 返回har文件和结果集，左边是结果集，右边是har文件
     */
    public Pair<List<BrowserVo>, Har> captureRequest() {
        Har har = browserMobProxy.getHar();
        List<HarEntry> entries = har.getLog().getEntries();
        List<BrowserVo> resultList = Lists.newLinkedList();
        for (HarEntry harEntry : entries) {
            HarRequest request = harEntry.getRequest();
            HarResponse response = harEntry.getResponse();
            // 这里用包含json来判断，因为有些content-type不按正常格式来写
            if (!response.getContent().getMimeType().contains("json") || ParamUtil.isStaticResource(request.getUrl())) {
                continue;
            }
            handleRequestByMethod(resultList, request, response);
        }
        browserMobProxy.endHar();
        log.info("总请求{}", resultList.size());
        return Pair.of(resultList, har);
    }

    /**
     * 给bmp代理创建har
     *
     * @param harName har的名字
     */
    public void newHar(String harName) {
        browserMobProxy.newHar(harName);
    }


    /**
     * 根据requestMethod来设置请求参数和响应体,目前只获取post和get的
     *
     * @param resultList 请求的结果集
     * @param request    请求
     * @param response   响应体
     */
    private void handleRequestByMethod(List<BrowserVo> resultList, HarRequest request, HarResponse response) {
        String method = request.getMethod();
        BrowserVo browserVo = new BrowserVo().setUrl(request.getUrl()).setResponseBody(JSONUtil.parseObj(response.getContent().getText()));
        if (HttpMethod.POST.name().equals(method)) {
            HarPostData postData = request.getPostData();
            browserVo.setRequestParam(postData == null ? null : JSONUtil.parseObj(postData.getText()));
            resultList.add(browserVo.setMethod(method));
        }
        else if (HttpMethod.GET.name().equals(method)) {
            List<HarNameValuePair> param = request.getQueryString();
            browserVo.setRequestParam((CollectionUtil.isEmpty(param) ? null : JSONUtil.parseArray(param)));
            resultList.add(browserVo.setMethod(method));
        }
    }

    /**
     * 输出文件
     *
     * @param list get或者post的请求list
     * @param file 文件的路径
     */
    public void outPutFile(List<BrowserVo> list, String file) {
        FileUtil.writeString(JSONUtil.toJsonStr(list), FileUtil.file(file), Charset.defaultCharset());
    }


}
