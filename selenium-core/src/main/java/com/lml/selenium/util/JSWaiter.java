package com.lml.selenium.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;


/**
 * @author yugi
 * @apiNote 代码来源及使用说明：http://www.swtestacademy.com/selenium-wait-javascript-angular-ajax/
 * @since 2019-04-30
 */
@Slf4j
@UtilityClass
public class JSWaiter {

    private static WebDriver jsWaitDriver;

    private static WebDriverWait jsWait;

    private static JavascriptExecutor jsExec;

    public static void setDriver(WebDriver driver) {
        jsWaitDriver = driver;
        // js最大等待两分钟
        jsWait = new WebDriverWait(jsWaitDriver, Duration.ofSeconds(120));
        jsExec = (JavascriptExecutor) jsWaitDriver;
    }

    /**
     * Wait for JQuery Load
     */
    private static void waitForJQueryLoad() {
        //Wait for jQuery to load
        // Get JQuery is Ready
        boolean jqueryReady = (Boolean) jsExec.executeScript("return jQuery.active==0");
        //Wait JQuery until it is Ready!
        if (jqueryReady) {
            log.debug("JQuery is Ready!");
            return;
        }
        log.debug("JQuery is NOT Ready!");
        //Wait for jQuery to load
        jsWait.until(webDriver -> ((Long) ((JavascriptExecutor) jsWaitDriver).executeScript("return jQuery.active") == 0));
    }

    /**
     * Wait for Angular Load
     */
    private static void waitForAngularLoad() {
        JavascriptExecutor jsExec = (JavascriptExecutor) jsWaitDriver;
        String angularReadyScript = "return angular.element(document).injector().get('$http').pendingRequests.length === 0";
        //Wait for ANGULAR to load
        //Get Angular is Ready
        boolean angularReady = Boolean.parseBoolean(jsExec.executeScript(angularReadyScript).toString());
        //Wait ANGULAR until it is Ready!
        if (angularReady) {
            log.debug("ANGULAR is Ready!");
            return;
        }
        log.debug("ANGULAR is NOT Ready!");
        //Wait for Angular to load
        jsWait.until(driver -> Boolean.valueOf(((JavascriptExecutor) driver).executeScript(angularReadyScript).toString()));
    }

    /**
     * Wait Until JS Ready
     */
    private static void waitUntilJSReady() {
        JavascriptExecutor jsExec = (JavascriptExecutor) jsWaitDriver;
        //Wait for Javascript to load
        //Get JS is Ready
        boolean jsReady = "complete".equals(jsExec.executeScript("return document.readyState").toString());
        //Wait Javascript until it is Ready!
        if (jsReady) {
            log.debug("JS is Ready!");
            return;
        }
        log.debug("JS in NOT Ready!");
        //Wait for Javascript to load
        jsWait.until(driver -> "complete".equals(((JavascriptExecutor) jsWaitDriver).executeScript("return document.readyState").toString()));
    }

    /**
     * Wait Until JQuery and JS Ready
     */
    public static void waitUntilJQueryReady() {
        // 先等待JS加载完成
        waitUntilJSReady();
        JavascriptExecutor jsExec = (JavascriptExecutor) jsWaitDriver;
        //First check that JQuery is defined on the page. If it is, then waitTime AJAX
        Boolean jQueryDefined = (Boolean) jsExec.executeScript("return typeof jQuery != 'undefined'");
        if (jQueryDefined) {
            //Pre Wait for stability (Optional)
            WebUtil.doWait(100);
            //Wait JQuery Load
            waitForJQueryLoad();
            //Post Wait for stability (Optional)
            WebUtil.doWait(100);
        }
        log.debug("jQuery is not defined on this site!");
    }


    /**
     * Wait Until Angular and JS Ready
     */
    private static void waitUntilAngularReady() {
        JavascriptExecutor jsExec = (JavascriptExecutor) jsWaitDriver;
        //First check that ANGULAR is defined on the page. If it is, then waitTime ANGULAR
        Boolean angularUnDefined = (Boolean) jsExec.executeScript("return window.angular === undefined");
        if (angularUnDefined) {
            log.debug("Angular is not defined on this site!");
            return;
        }
        Boolean angularInjectorUnDefined = (Boolean) jsExec.executeScript("return angular.element(document).injector() === undefined");
        if (angularInjectorUnDefined) {
            log.debug("Angular injector is not defined on this site!");
            return;
        }
        //Pre Wait for stability (Optional)
        WebUtil.doWait(100);
        //Wait Angular Load
        waitForAngularLoad();
        //Wait JS Load
        waitUntilJSReady();
        //Post Wait for stability (Optional)
        WebUtil.doWait(100);
    }


    /**
     * Wait Until JQuery Angular and JS is ready
     */
    public static void waitJQueryAngular() {
        waitUntilJQueryReady();
        waitUntilAngularReady();
    }


}
