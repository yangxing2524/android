package com.growalong.android.account;

/**
 * Created by gangqing on 2016/7/18.
 */
public class AccountInfo {
    private String user_id;
    private String user_name;
    private String user_head;
    private String phone_number;
    private String sessionId;

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setUserId(String userId) {
        this.user_id = userId;
    }

    public String getUserId() {
        return this.user_id;
    }

    public void setUserName(String userName) {
        this.user_name = userName;
    }

    public String getUserName() {
        return user_name;
    }

    public void setUserHead(String userHead) {
        this.user_head = userHead;
    }

    public String getUserHead() {
        return user_head;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phone_number = phoneNumber;
    }

    public String getPhoneNumber() {
        return phone_number;
    }
}
