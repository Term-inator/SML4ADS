package com.ecnu.adsmls.utils.register.exception;

public class RequirementException extends Exception {
    public RequirementException() {
        super();
    }

    public RequirementException(String message) {
        super(message);
    }

    public RequirementException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequirementException(Throwable cause) {
        super(cause);
    }

    public RequirementException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
