package com.ecnu.adsmls.model;

/**
 * For those model who cannot check() at Preprocessing stage
 */
public class AsyncError {
    protected String errMsg;

    public AsyncError(String errMsg) {
        this.errMsg = errMsg;
    }

    public AsyncError() {
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}
