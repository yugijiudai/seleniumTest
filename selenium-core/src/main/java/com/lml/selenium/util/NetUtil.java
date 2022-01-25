package com.lml.selenium.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.lml.selenium.filter.NetworkFilter;
import com.lml.selenium.filter.NetworkStaticResourceFilter;
import com.lml.selenium.vo.BrowserVo;
import lombok.experimental.UtilityClass;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author yugi
 * @apiNote 网络工具类
 * @since 2022-01-21
 */
@UtilityClass
public class NetUtil {

    /**
     * 静态资源的前缀
     */
    private final String STATIC_PREFFIX = "http";

    /**
     * 静态资源正则
     */
    public final String STATIC_REX = ".*(css|ico|jpg|jpeg|png|gif|bmp|wav|js|woff2|woff|json|svg)(\\?.*)?$";


    /**
     * 判断url是否静态资源
     *
     * @param url 请求的url
     * @return true表示静态资源
     */
    public boolean isStaticResource(String url) {
        if (!url.startsWith(STATIC_PREFFIX)) {
            return true;
        }
        return Pattern.matches(STATIC_REX, url);
    }


    /**
     * 根据提供的har包解析里面的参数把对应的参数封装到networkVo类里,用户可以根据自己的需要定制对应的过滤器
     *
     * @param harFile         请求的har包路径
     * @param networkFilters  {@link NetworkFilter}
     * @param useStaticFilter 是否使用静态资源过滤器
     * @return {@link BrowserVo}
     */
    public List<BrowserVo> getNetwork(String harFile, List<NetworkFilter> networkFilters, boolean useStaticFilter) {
        if (CollectionUtils.isEmpty(networkFilters)) {
            networkFilters = Lists.newArrayList();
        }
        if (useStaticFilter) {
            networkFilters.add(new NetworkStaticResourceFilter());
        }
        String har = FileUtil.readUtf8String(FileUtil.file(harFile));
        JSONObject object = JSONUtil.parseObj(har);
        List<JSONObject> entries = ObjUtil.getBucketList(object, "$.log.entries");
        List<BrowserVo> list = Lists.newArrayList();
        for (JSONObject entry : entries) {
            JSONObject request = entry.getJSONObject("request");
            if (filterNetwork(networkFilters, entry)) {
                continue;
            }
            String method = request.getStr("method");
            if ("GET".equals(method)) {
                handleGet(request, list);
            }
            else if ("POST".equals(method)) {
                handlePost(request, list);
            }
        }
        return list;
    }

    /**
     * 遍历过滤器列表,判断是否需要过滤
     *
     * @param networkFilters {@link NetworkFilter}
     * @param entry          har文件的entry对象
     * @return true表示需要过滤
     */
    private boolean filterNetwork(List<NetworkFilter> networkFilters, JSONObject entry) {
        for (NetworkFilter networkFilter : networkFilters) {
            if (networkFilter.filterFunc(entry)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 处理post的请求
     *
     * @param request 请求体
     * @param list    结果集的list
     */
    private void handlePost(JSONObject request, List<BrowserVo> list) {
        JSONObject postData = request.getJSONObject("postData");
        JSONObject param = postData != null ? postData.getJSONObject("text") : null;
        setBrowserVo(request, list, param);
    }

    /**
     * 处理get的请求
     *
     * @param request 请求体
     * @param list    结果集的list
     */
    private void handleGet(JSONObject request, List<BrowserVo> list) {
        List<JSONObject> queryString = ObjUtil.getBucketList(request, "$.queryString");
        for (JSONObject jsonObject : queryString) {
            if ("json".equals(jsonObject.getStr("name"))) {
                JSONObject param = jsonObject.getJSONObject("value");
                setBrowserVo(request, list, param);
                return;
            }
        }
        setBrowserVo(request, list, null);
    }

    /**
     * 设置捕获到的请求对象兵器添加到list
     *
     * @param request 请求体
     * @param list    结果集的list
     * @param param   请求的参数
     */
    private void setBrowserVo(JSONObject request, List<BrowserVo> list, JSONObject param) {
        BrowserVo browserVo = new BrowserVo();
        browserVo.setRequestParam(param).setMethod(request.getStr("method")).setUrl(request.getStr("url"));
        List<JSONObject> headers = ObjUtil.getBucketList(request, "$.headers");
        Map<Object, Object> collect = headers.stream().collect(Collectors.toMap(s -> s.get("name"), s -> s.get("value")));
        browserVo.setRequestHeader(JSONUtil.parseObj(collect));
        list.add(browserVo);
    }

}
