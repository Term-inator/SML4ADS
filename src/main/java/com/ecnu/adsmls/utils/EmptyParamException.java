package com.ecnu.adsmls.utils;

public class EmptyParamException extends Exception {
    public EmptyParamException() {
        super();
    }

    public EmptyParamException(String message) {
        super(message);
    }

    public EmptyParamException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmptyParamException(Throwable cause) {
        super(cause);
    }

    public EmptyParamException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
