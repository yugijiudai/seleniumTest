package com.lml.selenium.util;

import cn.hutool.core.util.ClassLoaderUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.lml.selenium.dto.RunMethodDto;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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


    /**
     * 根据路径提取里面的数组,并且转换成List的json对象
     *
     * @param result   查询的结果
     * @param jsonPath 要提取的路径
     * @return 返回数组结果
     */
    public List<JSONObject> getBucketList(JSONObject result, String jsonPath) {
        Object pathObj = JSONUtil.getByPath(result, jsonPath);
        if (pathObj == null) {
            // 如果提取不到返回空列表
            return Lists.newArrayList();
        }
        JSONArray arr = (JSONArray) pathObj;
        return arr.stream().map(obj -> (JSONObject) obj).collect(Collectors.toList());
    }

    /**
     * 利用反射获取需要调用的方法的参数个数
     *
     * @param runMethodDto {@link RunMethodDto}
     * @return 方法的参数个数
     */
    public int getClassMethodParamCnt(RunMethodDto runMethodDto) {
        Method method = ReflectUtil.getMethodByName(ClassLoaderUtil.loadClass(runMethodDto.getClassName()), runMethodDto.getMethodName());
        return method.getParameterCount();
    }
}
