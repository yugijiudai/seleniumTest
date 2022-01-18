package com.lml.selenium.util;

import lombok.experimental.UtilityClass;

import java.util.regex.Pattern;

/**
 * @author yugi
 * @apiNote 参数转换的工具类
 * @since 2019-04-12
 */
@UtilityClass
public class ParamUtil {

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


}
