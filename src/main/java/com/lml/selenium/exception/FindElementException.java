package com.lml.selenium.exception;

/**
 * @author yugi
 * @apiNote 查找元素异常
 * @since 2019-05-06
 */
public class FindElementException extends BizException {


    public FindElementException(String message) {
        super(message);
    }

    public FindElementException(String message, Throwable cause) {
        super(message, cause);
    }

    public FindElementException(Throwable cause) {
        super(cause);
    }


}
