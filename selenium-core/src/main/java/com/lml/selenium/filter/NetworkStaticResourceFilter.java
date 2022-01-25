package com.lml.selenium.filter;

import cn.hutool.json.JSONObject;
import com.lml.selenium.util.NetUtil;

/**
 * @author yugi
 * @apiNote 网络静态资源过滤器
 * @since 2022-01-25
 */
public class NetworkStaticResourceFilter implements NetworkFilter {

    @Override
    public boolean filterFunc(JSONObject harEntry) {
        JSONObject request = harEntry.getJSONObject("request");
        return NetUtil.isStaticResource(request.getStr("url"));
    }
}
