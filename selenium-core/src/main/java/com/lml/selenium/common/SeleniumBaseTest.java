package com.lml.selenium.common;

import com.lml.selenium.factory.SeleniumFactory;
import com.lml.selenium.util.BizUtil;
import com.lml.selenium.util.UserUtil;
import com.lml.selenium.util.WebUtil;
import org.apache.commons.lang3.tuple.Pair;

/**
 * @author yugi
 * @apiNote 测试的基类
 * @since 2021-01-24
 */
public abstract class SeleniumBaseTest {


    /**
     * 操作完等待看效果
     */
    protected static final int WAIT = 1000;

    /**
     * 关闭驱动
     */
    protected void quitDriver() {
        SeleniumFactory.quitDriver();
        // 退出登录,清除登陆了用户
        UserUtil.deleteUser();
    }


    /**
     * 初始化
     */
    protected void initWebDriver() {
        SeleniumFactory.initWebDriver();
    }

    /**
     * 用例的模板名字,可以是数据库的表或者excel
     *
     * @return 返回模板的名字
     */
    public abstract String getCaseTemplate();

    /**
     * 根据传进来的步骤来运行用例,支持数据或者excel
     *
     * @param step 左边是开始的步骤,右边是结束的步骤,如果右边小于等于,则直接从左边的步骤运行到结束
     */
    protected void doHandle(Pair<Integer, Integer> step) {
        BizUtil.doHandle(this.getCaseTemplate(), step);
        WebUtil.doWait(WAIT);
    }

    /**
     * 根据传进来的用例模块数组来执行用例
     *
     * @param modelArray 要执行的模块,暂时不支持excel
     */
    protected void doHandleByModel(String[] modelArray) {
        BizUtil.doHandle(this.getCaseTemplate(), modelArray);
        WebUtil.doWait(WAIT);
    }

}
