package com.growalong.android.net.retrofit;

/**
 * Created by gangqing on 2016/7/18.
 */
public class HttpStatusCode {
    //请求失败-仿问服务器失败
    public static final int STATUS_REQUEST_ERROR = -402;
    //bean转换失败
    public static final int STATUS_TRANSFORM_ERROR = -100;
    //请求的json为空
    public static final int STATUS_JSON_EMPTY_ERROR = -101;
    //微信授权失败,获取到的微信token为空
    public static final int STATUS_WX_LOGIN_ERROR = -102;
    //请求成功
    public static final int STATUS_SUCCESS = 0;
    //Token失效
    public static final int STATUS_TOKEN_LOST_ERROR = 110;

    public static final int ERROR_TYPE_DATA = -105;

    /**
     * 没有权限
     **/
    public static final int NO_AUTHORITY = 10013;

    public static final int STATUS_NO_LOGIN = 20004;

    public static final int STATUS_ALREADY_LOGIN = 20005;

    public static final int STATUS_SID_LOST = 20006;

    public static final int STATUS_CLOSE = 50001;
}
