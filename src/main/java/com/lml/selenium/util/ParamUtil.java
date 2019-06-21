package com.lml.selenium.util;

import lombok.experimental.UtilityClass;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author yugi
 * @apiNote 参数转换的工具类
 * @since 2019-04-12
 */
@UtilityClass
public class ParamUtil {


    /**
     * 解析url的参数,生成MultiValueMap
     *
     * @param url 要解析的url
     * @return {@link MultiValueMap}
     */
    public MultiValueMap<String, String> urlToMultiValueMap(String url) {
        return UriComponentsBuilder.fromHttpUrl(url).build().getQueryParams();
    }

}
