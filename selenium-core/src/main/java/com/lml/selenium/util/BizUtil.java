package com.lml.selenium.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ClassLoaderUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.lml.selenium.client.HandlerClient;
import com.lml.selenium.dto.RunMethodDto;
import com.lml.selenium.dto.SetDto;
import com.lml.selenium.entity.Selenium;
import com.lml.selenium.factory.SeleniumFactory;
import com.lml.selenium.handler.other.RunMethodHandler;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

/**
 * @author yugi
 * @apiNote 处理业务的统一工具类
 * @since 2019-05-22
 */
@UtilityClass
@Slf4j
public class BizUtil {


    /**
     * 传入需要处理的表和步骤来执行用例(灵活性低,必须保证要运行的步骤递增)
     *
     * @param name 要处理的表或者excel名字
     * @param step 左边是开始的步骤,右边是结束的步骤,如果右边小于等于,则直接从左边的步骤运行到结束
     */
    public void doHandle(String name, Pair<Integer, Integer> step) {
        List<Selenium> list = LoadTestCaseUtil.loadTestCase(name, step.getLeft(), step.getRight());
        handleSeleniumBoList(list);
    }

    /**
     * 传入需要处理的表和对应的模块来执行用例(灵活性高,优先推荐使用)
     *
     * @param name  要处理的表或者excel名字
     * @param array 要运行的模块
     */
    public void doHandle(String name, String[] array) {
        List<Selenium> list = LoadTestCaseUtil.loadDbCase(name, array);
        handleSeleniumBoList(list);
    }

    /**
     * 统一处理selenium的列表
     *
     * @param list selenium的列表
     */
    private void handleSeleniumBoList(List<Selenium> list) {
        for (Selenium selenium : list) {
            try {
                WebElement webElement = HandlerClient.doAction(selenium);
                String assertContent = selenium.getCallBack();
                if (StrUtil.isNotBlank(assertContent)) {
                    handleCallbackMethod(assertContent, webElement);
                }
                log.info("处理:{}成功", selenium);
            }
            catch (Throwable e) {
                log.error("处理元素:{}出错,错误原因如下:{}", selenium, e);
                BizUtil.savePic();
                Assert.fail();
            }
        }
    }

    /**
     * 屏幕截图
     */
    private void savePic() {
        SetDto setDto = SeleniumFactory.getSetDto();
        if (setDto.getDebugMode()) {
            return;
        }
        File file = ((TakesScreenshot) SeleniumFactory.getDriver()).getScreenshotAs(OutputType.FILE);
        String name = setDto.getErrorPic() + "_" + DateUtil.format(new Date(), "yyyy-MM-dd-hh-mm-ss") + ".jpg";
        File dest = new File(name);
        FileUtil.copy(file, dest, false);
    }

    /**
     * 处理回调的方法
     *
     * @param callBackContent 回调的参数,json格式
     */
    private void handleCallbackMethod(String callBackContent, WebElement webElement) {
        RunMethodDto runMethodDto = JSONUtil.toBean(callBackContent, RunMethodDto.class);
        Method method = ReflectUtil.getMethodByName(ClassLoaderUtil.loadClass(runMethodDto.getClassName()), runMethodDto.getMethodName());
        Object[] args = runMethodDto.getArgs();
        if (method.getParameterCount() > args.length) {
            // 利用反射获得需要调用的方法参数,如果参数比设定的要多,则默认在第一个参数补上查找到的元素,作为查找的元素返回给回调方法
            runMethodDto.setArgs(ArrayUtil.insert(args, 0, webElement));
        }
        RunMethodHandler runMethodHandler = new RunMethodHandler();
        runMethodHandler.invokeMethod(runMethodDto);
    }
}