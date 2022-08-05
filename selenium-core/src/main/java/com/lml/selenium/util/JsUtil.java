package com.lml.selenium.util;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.lml.selenium.dto.SetDto;
import com.lml.selenium.exception.InitException;
import com.lml.selenium.factory.SeleniumFactory;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * @author yugi
 * @apiNote js工具类
 * @since 2019-05-15
 */
@UtilityClass
@Slf4j
public class JsUtil {

    /**
     * 缓存过期时间(单位:毫秒)
     */
    private final static Long TIME_OUT = 1000 * 60L * 60L;

    /**
     * 放通用js的文件夹名字
     */
    private final static String COMMON = "common";

    /**
     * 放业务js的文件夹名字
     */
    private final static String BIZ = "biz";

    /**
     * 业务js脚本
     */
    public final static String BIZ_SCRIPT = "bizHelper";

    /**
     * dom的js脚本
     */
    public final static String DOM_SCRIPT = "domHelper";

    /**
     * frame的js脚本
     */
    public final static String FRAME_SCRIPT = "frameHelper";

    /**
     * 等待js加载的最大时间(毫秒)
     */
    private static final Long WAIT_JS_TIME = 120000L;

    /**
     * 用于存放脚本的缓存,减少io流读取文件的开销
     */
    private final static TimedCache<String, String> SCRIPT_CACHE = CacheUtil.newTimedCache(TIME_OUT);


    /**
     * 根据名字加载通用的js脚本
     *
     * @param fileName 通用文件夹下的js脚本名字
     * @return 加载好的脚本
     */
    public String loadCommonScript(String fileName) {
        return loadScript(COMMON, fileName);
    }

    /**
     * 向页面直接注入通用的js脚本
     *
     * @param fileName 通用文件夹下的js脚本名字
     */
    public void addCommonScript(String fileName) {
        String domHelper = loadCommonScript(fileName);
        ((JavascriptExecutor) SeleniumFactory.getDriver()).executeScript(domHelper);
    }

    /**
     * 根据名字加载业务的js文件
     *
     * @param fileName 业务文件夹下要加载的业务js文件名字
     * @return 加载好的脚本
     */
    private String loadBizScript(String fileName) {
        return loadScript(BIZ, fileName);
    }

    /**
     * 向页面直接注入业务的js脚本
     *
     * @param fileName 业务文件夹下要加载的业务js文件名字
     */
    public void addBizScript(String fileName) {
        String bizHelper = loadBizScript(fileName);
        ((JavascriptExecutor) SeleniumFactory.getDriver()).executeScript(bizHelper);
    }

    /**
     * 向页面添加JQuery
     */
    public void addJquery() {
        String script = loadCommonScript("addJq");
        runJs(String.format(script, SeleniumFactory.getSetDto().getJqueryUrl()));
        WebUtil.doWait(100);
        if (!checkJqueryIsExist()) {
            throw new InitException("添加jquery失败!");
        }
        log.debug("网页添加jquery成功");
    }

    /**
     * 校验页面是否有jquery对象
     *
     * @return true表示有
     */
    private boolean checkJqueryIsExist() {
        return runJs("return typeof jQuery != 'undefined'");
    }


    /**
     * 等待页面加载完成,先等待页面的js加载完成,然后利用jquery的属性来判断ajax的请求是否加载完成,如果页面没有jquery对象,会尝试往里面添加,如果添加还是失败则会报异常
     *
     * @param requestWaitTime 请求最大的等待时间，如果没有，则使用等待页面脚本加载的时间
     */
    public static void waitPageLoad(Long requestWaitTime) {
        // 先等待JS加载完成
        waitUntilJsReady();
        WebUtil.doWait(100);
        if (JsUtil.checkJqueryIsExist()) {
            waitForJqueryLoad(requestWaitTime);
            return;
        }
        log.warn("没有jquery对象,尝试往页面添加");
        addJquery();
        waitForJqueryLoad(requestWaitTime);
    }


    /**
     * 利用jquery的active属性等待请求加载
     *
     * @param requestWaitTime 请求最大的等待时间，如果没有，则使用等待页面脚本加载的时间
     */
    private static void waitForJqueryLoad(Long requestWaitTime) {
        SetDto setDto = SeleniumFactory.getSetDto();
        requestWaitTime = requestWaitTime == null ? WAIT_JS_TIME : requestWaitTime;
        WaitUtl.waitLoadByJs("return jQuery.active==0", requestWaitTime, setDto.getInterval());
    }

    /**
     * Wait Until JS Ready
     */
    public static void waitUntilJsReady() {
        WaitUtl.waitLoadByJs("return document.readyState=='complete'", WAIT_JS_TIME, SeleniumFactory.getSetDto().getInterval());
    }

    /**
     * 根据自定义属性获取第一个dom,使用方法可以参考:
     *
     * @param fnName 添加的方法名字
     * @return 返回在页面生成js脚本的字符串
     * @see JsUtil#getCustomAttributeDom(String, boolean)
     */
    public String getFirstCustomAttributeDom(String fnName) {
        return getCustomAttributeDom(fnName, true);
    }


    /**
     * 执行domHelper的js
     *
     * @param call 要执行的脚本
     */
    private void runDomJs(String call) {
        addCommonScript(DOM_SCRIPT);
        runJs(call);
    }

    /**
     * 加载自己定义好的js脚本
     *
     * @param fileName js脚本的名字
     * @return 返回脚本内容
     */
    private String loadScript(String folder, String fileName) {
        String file = String.format("script/%s/%s.js", folder, fileName);
        // 先从缓存中读取
        String cache = SCRIPT_CACHE.get(file);
        if (StrUtil.isNotBlank(cache)) {
            return cache;
        }
        String script = ResourceUtil.readUtf8Str(file);
        // 放入缓存
        SCRIPT_CACHE.put(file, script);
        return script;
    }


    /**
     * 根据自定义属性获取对应的dom,使用方法如下:
     * <p>
     * String script = getCustomAttributeDom("functionName", true);
     * </p>
     * <p>
     * script +=
     * " var typeDom = functionName('自定义属性所在的tag', '自定义属性的名字');return typeDom;"
     * </p>
     * <p>
     * Object dom = runJavaScript(script);"
     * </p>
     *
     * @param fnName   添加的方法名字
     * @param getFirst 是否取第一个节点
     * @return 返回添加好的脚本
     */
    private String getCustomAttributeDom(String fnName, boolean getFirst) {
        StringBuilder customAttributeFn = new StringBuilder()
                .append(String.format("function %s (tagName, attr){", fnName))
                .append("var tagNames = document.getElementsByTagName(tagName);")
                .append("var ar = [];")
                .append("for(var i = 0,len = tagNames.length;i < len;i++){")
                .append("var node = tagNames[i];")
                .append("if (node.getAttributeNode(attr)){");
        customAttributeFn.append(getFirst ? "return node;}}}" : "ar.push(node);}}return ar;}");
        return customAttributeFn.toString();
    }

    /**
     * 根据自定义属性获取对应的dom
     *
     * @param tagName 自定义属性的dom所在的tag名字
     * @param attr    自定义的属性名字
     * @return 返回找到的元素列表
     */
    private List<WebElement> getCustomAttributeDom(String tagName, String attr) {
        String script = loadCommonScript(DOM_SCRIPT);
        return runJs(String.format("%s return domHelper.domObj.getCustomAttributeDom(%s, %s);", script, tagName, attr));
    }

    /**
     * 运行js脚本(返回值是泛型)
     *
     * @param script 需要执行的js脚本
     * @return 需要返回的类型
     */
    @SuppressWarnings("unchecked")
    public <T> T runJs(String script) {
        log.debug("预执行脚本:\n{}", script);
        Object response = ((JavascriptExecutor) SeleniumFactory.getDriver()).executeScript(script);
        log.info("执行脚本成功:\n{}\n返回值是:{}", script, response);
        return (T) response;
    }

    /**
     * 运行js脚本
     *
     * @param script 要运行的js脚本
     * @param args   运行脚本需要的参数
     */
    @SuppressWarnings("unchecked")
    public <T> T runJs(String script, Object... args) {
        log.debug("预执行脚本:\n{}\n参数是:{}", script, args);
        Object response = ((JavascriptExecutor) SeleniumFactory.getDriver()).executeScript(script, args);
        log.info("执行脚本成功:\n{}\n参数是:{}\n返回值是:{}", script, Lists.newArrayList(args), response);
        return (T) response;
    }
}
