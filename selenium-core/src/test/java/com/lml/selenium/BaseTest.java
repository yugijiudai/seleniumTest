package com.lml.selenium;

import com.lml.selenium.factory.SeleniumFactory;
import com.lml.selenium.util.BizUtil;
import com.lml.selenium.util.UserUtil;
import com.lml.selenium.util.WebUtil;

/**
 * @author yugi
 * @apiNote 测试的基类
 * @since 2019-04-30
 */
public abstract class BaseTest {


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
     * 根据传进来的二维数组来执行用例
     *
     * @param stepArray 要执行的二维数组,这个是用例执行的步骤,支持数据或者excel
     */
    protected void doHandle(Integer[][] stepArray) {
        BizUtil.doHandle(this.getCaseTemplate(), stepArray);
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
