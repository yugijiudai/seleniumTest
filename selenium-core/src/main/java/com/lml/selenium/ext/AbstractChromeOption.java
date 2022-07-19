package com.lml.selenium.ext;

import cn.hutool.core.lang.Pair;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.Map;

/**
 * @author yugi
 * @apiNote
 * @since 2022-07-19
 */
public interface AbstractChromeOption {

    /**
     * 默认设置相关的options,后续可以通过实现此类覆盖此方法来扩展或者自己按需定义
     *
     * @return 左边是ChromeOptions, 右边是prefs, 用于后续自己加属性扩展
     */
    Pair<ChromeOptions, Map<String, Object>> createChromeOption();

}
