package com.growalong.android.im.model;

/**
 * 用户数据
 */
public class ImUserInfo {

    private String id;
    private String userSig;

    private static ImUserInfo ourInstance = new ImUserInfo();

    public static ImUserInfo getInstance() {
        return ourInstance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserSig() {
        return userSig;
    }

    public void setUserSig(String userSig) {
        this.userSig = userSig;
    }

}