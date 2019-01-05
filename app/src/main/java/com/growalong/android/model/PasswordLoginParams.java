package com.growalong.android.model;

/**
 * Created by gangqing on 2016/10/19.
 */

import com.growalong.android.app.MyApplication;

/**
 * 手机号码登录需要的参数
 */
public class PasswordLoginParams {
    private String mobile;
    private String password;
    private String type;

    public PasswordLoginParams(String mobile, String password) {
        this.mobile = mobile;
        this.password = password;
        this.type = MyApplication.TYPE;
    }
}
