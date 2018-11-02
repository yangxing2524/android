package com.growalong.android.model;

import com.growalong.android.account.AccountManager;
import com.growalong.android.app.Constants;
import com.growalong.android.app.PhoneSystemInfo;
import com.growalong.android.util.Md5Utils;
import com.growalong.android.util.VersionInfoUtil;

/**
 * Created by gangqing on 2016/10/19.
 */

public class BaseParams<T> {
    private static final String PRIVATE_KEY = Constants.private_key;   //私钥
    private long timestamp = System.currentTimeMillis();
    private String id;
    private String sign = getSign();
    private String encrypt = "md5";
    private ClientParams client;
    private String etag = "";
    private UserParams user;
    private T data;

    public BaseParams(T data) {
        this.data = data;
        this.id = assignId();
        this.sign = assignSign();
        if (AccountManager.getInstance().isLogin()) {
            user = new UserParams();
        }
        client = new ClientParams();
        client.ex.setImei(PhoneSystemInfo.imei);
        client.ex.setCh(PhoneSystemInfo.ch);
    }

    public void setData(T data) {
        this.data = data;
    }

    private String assignId() {
        StringBuffer id = new StringBuffer();
        id.append(getTimestamp());
        id.append((int) (Math.random() * 10));
        id.append((int) (Math.random() * 10));
        id.append((int) (Math.random() * 10));
        return id.toString();
    }

    public String getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    private String assignSign() {
        StringBuffer sign = new StringBuffer();
        sign.append(id);
        sign.append(":");
        sign.append(PRIVATE_KEY);
        sign.append(":");
        sign.append(getTimestamp());
        return Md5Utils.getMD5(sign.toString());
    }

    public String getSign() {
        return sign;
    }

    public String getEncrypt() {
        return encrypt;
    }

    public ClientParams getClient() {
        return client;
    }

    public String getEtag() {
        return etag;
    }

    public UserParams getUser() {
        return user;
    }

    public class UserParams {
        public String sid = AccountManager.getInstance().getAccountInfo().getSessionId();
        public String userId = AccountManager.getInstance().getAccountInfo().getUserId();
    }

    public class ClientParams {
        public String caller = "app";    //调用者，app或者web
        public String os = String.valueOf(android.os.Build.VERSION.SDK_INT);    //客户端操作系统版本
        public String platform = "android";    //平台，如android,ios
        public int ver = VersionInfoUtil.getVersionCode(); //客户端版本
        public Ex ex;

        public ClientParams() {
            ex = new Ex();
        }
    }

    public class Ex {
        public String imei;
        public String mac;
        public String ch;

        public void setImei(String imei) {
            this.imei = imei;
        }

        public void setCh(String ch) {
            this.ch = ch;
        }

        public void setMac(String mac) {
            this.mac = mac;
        }
    }
}
