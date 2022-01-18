package com.lml.selenium.exception;

/**
 * @author yugi
 * @apiNote 业务异常
 * @since 2019-05-06
 */
public class BizException extends RuntimeException {


    public BizException(String message) {
        super(message);
    }

    public BizException(String message, Throwable cause) {
        super(message, cause);
    }


    public BizException(Throwable cause) {
        super(cause);
    }


}
