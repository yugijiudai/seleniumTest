package com.lml.selenium.strategy;

import cn.hutool.core.util.StrUtil;
import com.lml.selenium.exception.FindElementException;
import com.lml.selenium.util.JsUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yugi
 * @apiNote 使用js自己的轮询方式等待, 这个是通过用字符串脚本的方法注入，并不是所有的网页都能支持这方法，例如chrome的chrome://download页面
 * @since 2022-08-09
 */
@Slf4j
public class JsWaitByNormalJsStrategy implements JsWaitStrategy {

    @Override
    public void waitLoadByJs(String script, long maxWaitTime, Integer interval) {
        JsUtil.addCommonScript("domHelper");
        String runScript = String.format("return domHelper.domObj.getWaitDomByTimerResult(`%s`, %s, %s)", script, maxWaitTime, interval);
        Object result = JsUtil.runJs(runScript);
        if (!"true".equals(result)) {
            String msg = StrUtil.format("脚本:【{}】, 等待失败,原因如下:{}", runScript, result);
            throw new FindElementException(msg);
        }
    }
}
