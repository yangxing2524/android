package com.growalong.android.model;

/**
 * Created by chenjiawei on 2016/8/4.
 */
public class ApiException extends Exception {
    private int status;

    public ApiException(String msg, int status) {
        super(msg);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
