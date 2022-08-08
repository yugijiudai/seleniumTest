package com.lml.selenium.util;

import cn.hutool.core.util.StrUtil;
import com.lml.selenium.dto.SetDto;
import com.lml.selenium.enums.JsWaitEnum;
import com.lml.selenium.exception.FindElementException;
import com.lml.selenium.factory.SeleniumFactory;
import lombok.Setter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author yugi
 * @apiNote 等待的工具类
 * @since 2022-08-05
 */
@UtilityClass
@Slf4j
public class WaitUtl {


    /**
     * js等待的默认方式
     */
    @Setter
    private static JsWaitEnum jsWaitEnum = JsWaitEnum.parse(SeleniumFactory.getSetDto().getJsWaitType());

    /**
     * 使用自定义js的方式来等待页面加载
     *
     * @param script 等待的脚本
     */
    public void waitLoadByJs(String script) {
        SetDto setDto = SeleniumFactory.getSetDto();
        waitLoadByJs(script, setDto.getMaxWaitTime(), setDto.getInterval());
    }

    /**
     * 使用自定义js的方式来等待页面加载
     *
     * @param script      等待的脚本
     * @param maxWaitTime 最长等待时间(毫秒)
     * @param interval    每次轮询间隔的时间(毫秒)
     */
    public void waitLoadByJs(String script, long maxWaitTime, Integer interval) {
        waitLoadByJs(script, maxWaitTime, interval, jsWaitEnum);
    }

    /**
     * 使用自定义js的方式来等待页面加载
     *
     * @param script      等待的脚本
     * @param maxWaitTime 最长等待时间(毫秒)
     * @param interval    每次轮询间隔的时间(毫秒)
     * @param waitType    等待的执行方式,loop:轮询,scheduled:定时任务,js:js自己的轮询方式
     */
    public void waitLoadByJs(String script, long maxWaitTime, Integer interval, JsWaitEnum waitType) {
        switch (waitType) {
            case LOOP:
                waitLoadByLoop(script, maxWaitTime, interval);
                break;
            case SCHEDULED:
                waitLoadBySchedule(script, maxWaitTime, interval);
                break;
            case JS:
                waitLoadByNormalJs(script, maxWaitTime, interval);
                break;
        }
    }

    /**
     * 使用js自己的轮询方式等待
     *
     * @param script      等待的脚本
     * @param maxWaitTime 最长等待时间(毫秒)
     * @param interval    每次轮询间隔的时间(毫秒)
     */
    private void waitLoadByNormalJs(String script, long maxWaitTime, Integer interval) {
        JsUtil.addCommonScript("domHelper");
        String runScript = String.format("return domHelper.domObj.getWaitDomByTimerResult(`%s`, %s, %s)", script, maxWaitTime, interval);
        Object result = JsUtil.runJs(runScript);
        if (!"true".equals(result)) {
            String msg = StrUtil.format("脚本:【{}】, 等待失败,原因如下:{}", runScript, result);
            throw new FindElementException(msg);
        }
    }

    /**
     * 等待页面的某些元素或者某些东西加载完成(通过使用脚本来判断这些是否加载完成)
     *
     * @param script      要执行判断的脚本
     * @param maxWaitTime 最长等待时间(毫秒)
     * @param interval    每次轮询间隔的时间(毫秒)
     */
    private void waitLoadByLoop(String script, long maxWaitTime, Integer interval) {
        long end = System.currentTimeMillis() + maxWaitTime;
        log.info("执行等待脚本:" + script);
        while (!(Boolean) JsUtil.runJs(script)) {
            if (System.currentTimeMillis() > end) {
                log.warn("脚本:【{}】, 超出最长等待时间{},跳出循环", script, maxWaitTime);
                throw new FindElementException("超出最长等待时间:" + maxWaitTime);
            }
            WebUtil.doWait(interval);
        }
    }


    /**
     * 使用java的定时任务来轮训页面的dom,直到找到或者超时为止
     *
     * @param script      要执行判断的脚本
     * @param maxWaitTime 最长等待时间(毫秒)
     * @param interval    每次轮询间隔的时间(毫秒)
     */
    private void waitLoadBySchedule(String script, long maxWaitTime, Integer interval) {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            if ((Boolean) JsUtil.runJs(script)) {
                scheduledExecutorService.shutdownNow();
            }
        }, 0, interval, TimeUnit.MILLISECONDS);
        try {
            if (!scheduledExecutorService.awaitTermination(maxWaitTime, TimeUnit.MILLISECONDS)) {
                log.warn("脚本:【{}】, 超出最长等待时间{},跳出循环", script, maxWaitTime);
                throw new FindElementException("超出最长等待时间:" + maxWaitTime);
            }
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


}
