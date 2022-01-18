package com.lml.selenium.exception;

/**
 * @author yugi
 * @apiNote 请求异常
 * @since 2019-06-04
 */
public class RestException extends RuntimeException {


    public RestException(String message) {
        super(message);
    }

    public RestException(String message, Throwable cause) {
        super(message, cause);
    }


    public RestException(Throwable cause) {
        super(cause);
    }


}
