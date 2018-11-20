package com.growalong.android.model.request;

/**
 * Created by yangxing on 2018/11/14.
 */
public class UserIdTypeParams {
    private String type;
    private long userId;

    public UserIdTypeParams(String type, long userId) {
        this.type = type;
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
