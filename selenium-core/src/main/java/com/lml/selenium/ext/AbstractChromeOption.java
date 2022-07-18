package com.lml.selenium.ext;

import org.openqa.selenium.chrome.ChromeOptions;

/**
 * @author yugi
 * @apiNote
 * @since 2022-07-19
 */
public interface AbstractChromeOption {

    /**
     * 默认设置相关的options,后续可以通过实现此类覆盖此方法来扩展或者自己按需定义
     *
     * @return {@link ChromeOptions}
     */
    ChromeOptions createChromeOption();

}
