package com.lml.selenium.util;

import lombok.experimental.UtilityClass;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

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
     * 解析url的参数,生成MultiValueMap
     *
     * @param url 要解析的url
     * @return {@link MultiValueMap}
     */
    public MultiValueMap<String, String> urlToMultiValueMap(String url) {
        return UriComponentsBuilder.fromHttpUrl(url).build().getQueryParams();
    }


    /**
     * 判断url是否静态资源
     *
     * @param url 请求的url
     * @return true表示静态资源
     */
    public boolean isStaticResource(String url) {
        if (!url.startsWith(STATIC_PREFFIX)) {
            return false;
        }
        return Pattern.matches(STATIC_REX, url);
    }


}
