package com.lml.selenium.util;

import com.lml.selenium.dto.SetDto;
import com.lml.selenium.enums.JsWaitEnum;
import com.lml.selenium.exception.BizException;
import com.lml.selenium.exception.FindElementException;
import com.lml.selenium.factory.SeleniumFactory;
import com.lml.selenium.strategy.JsWaitByLoopStrategy;
import com.lml.selenium.strategy.JsWaitByNormalJsStrategy;
import com.lml.selenium.strategy.JsWaitBySeleniumStrategy;
import com.lml.selenium.strategy.JsWaitStrategy;
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
     * js的等待策略
     */
    private JsWaitStrategy jsWaitStrategy = defaultInitJsWaitStrategy();

    /**
     * 根据配置文件初始化默认的等待策略
     *
     * @return {@link JsWaitStrategy}
     */
    private JsWaitStrategy defaultInitJsWaitStrategy() {
        JsWaitEnum jsWaitEnum = JsWaitEnum.parse(SeleniumFactory.getSetDto().getJsWaitType());
        return buildWaitStrategy(jsWaitEnum);
    }

    /**
     * 构建等待的策略
     *
     * @param jsWaitEnum {@link  JsWaitEnum}
     * @return 对应的策略
     */
    private JsWaitStrategy buildWaitStrategy(JsWaitEnum jsWaitEnum) {
        switch (jsWaitEnum) {
            case JS:
                return new JsWaitByNormalJsStrategy();
            case SELENIUM:
                return new JsWaitBySeleniumStrategy();
            default:
                return new JsWaitByLoopStrategy();
        }
    }

    /**
     * 修改对应的策略
     *
     * @param jsWaitEnum {@link JsWaitEnum}
     */
    public void setWaitStrategy(JsWaitEnum jsWaitEnum) {
        jsWaitStrategy = buildWaitStrategy(jsWaitEnum);
    }

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
     * 等待url变化，可以配合抓包使用，有时候打开新网页，waitPageLoad()方法会秒过导致无法抓包，因此可以先用这个方法监听页面url变化，然后再去等待
     *
     * @param lastUrl url变化前的地址
     */
    public void waitUrlChange(String lastUrl) {
        String nowUrl = SeleniumFactory.getDriverHolder().getCurrentUrl();
        SetDto setDto = SeleniumFactory.getSetDto();
        long nowTime = System.currentTimeMillis();
        long maxWaitTime = setDto.getMaxWaitTime() + nowTime;
        while (lastUrl.equals(nowUrl)) {
            log.warn("页面url没有变化，等待变化");
            WebUtil.doWait(setDto.getInterval());
            nowUrl = SeleniumFactory.getDriverHolder().getCurrentUrl();
            if (System.currentTimeMillis() > maxWaitTime) {
                throw new BizException("超出最长等待时间:");
            }
        }
    }

    /**
     * 使用自定义js的方式来等待页面加载
     *
     * @param script      等待的脚本
     * @param maxWaitTime 最长等待时间(毫秒)
     * @param interval    每次轮询间隔的时间(毫秒)
     */
    public void waitLoadByJs(String script, long maxWaitTime, Integer interval) {
        jsWaitStrategy.waitLoadByJs(script, maxWaitTime, interval);
    }


    /**
     * 使用java的定时任务来轮训页面的dom,直到找到或者超时为止
     *
     * @param script      要执行判断的脚本
     * @param maxWaitTime 最长等待时间(毫秒)
     * @param interval    每次轮询间隔的时间(毫秒)
     * @deprecated 每次都要新建一个定时任务线程, 貌似会对性能有影响, 使用selenium自带的方式代替这个
     */
    @Deprecated
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
