package com.lml.selenium.util;

import cn.hutool.core.util.ClassUtil;
import lombok.experimental.UtilityClass;

import java.util.Set;

/**
 * @author yugi
 * @apiNote 对象工具类
 * @since 2019-06-03
 */
@UtilityClass
public class ObjUtil {

    /**
     * 根据当前类所在包,获取这个类下的所有子类
     *
     * @param clz 父类
     * @return 子类集合
     */
    public Set<Class<?>> getSubclass(Class<?> clz) {
        return ClassUtil.scanPackageBySuper(clz.getPackage().getName(), clz);
    }
}
