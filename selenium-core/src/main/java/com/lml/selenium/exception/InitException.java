package com.lml.selenium.exception;

/**
 * @author yugi
 * @apiNote 初始化的异常
 * @since 2019-05-06
 */
public class InitException extends RuntimeException {


    public InitException(String message) {
        super(message);
    }


    public InitException(Throwable cause) {
        super(cause);
    }


    public InitException(String message, Throwable cause) {
        super(message, cause);
    }

}
