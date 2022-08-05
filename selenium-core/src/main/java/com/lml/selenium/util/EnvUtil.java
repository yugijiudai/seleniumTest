package com.lml.selenium.util;

import lombok.experimental.UtilityClass;

/**
 * @author yugi
 * @apiNote 环境工具类
 * @since 2022-08-05
 */
@UtilityClass
public class EnvUtil {


    /**
     * 获取当前系统
     *
     * @return 当前系统
     */
    public String getOs() {
        return System.getProperty("os.name");
    }

    /**
     * 是否mac系统
     *
     * @return true表示是
     */
    public boolean isMac() {
        return getOs().contains("Mac");
    }

}
