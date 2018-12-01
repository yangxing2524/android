package com.growalong.android.account;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.growalong.android.app.MyApplication;
import com.growalong.android.im.model.ImUserInfo;
import com.growalong.android.model.LoginBean;


/**
 * Created by gangqing on 2016/7/18.
 */
public class AccountManager {
    private static final String ACCOUNT_KEY = "account_key";
    private static final String SESSION_ID = "session_Id";
    private static final String USER_HEAD = "user_head";
    private static final String USER_NAME = "user_name";
    private static final String USER_ID = "user_id";
    private static final String PHONE_NUMBER = "phone_number";
    private static final String IM_USER_SIG = "im_user_sig";

    private static final String IS_VISITOR = "is_visitor";

    private static final String VISITOR_ACCOUNT_KEY = "visitor_account_key";
    private static final String VISITOR_SESSION_ID = "visitor_session_Id";
    private static final String VISITOR_USER_HEAD = "visitor_user_head";
    private static final String VISITOR_USER_NAME = "visitor_user_name";
    private static final String VISITOR_USER_ID = "visitor_user_id";
    private static final String VISITOR_PHONE_NUMBER = "visitor_phone_number";

    //    private ACache mCache;
    private SharedPreferences mSharedPreferences;
    private static AccountManager sInstance;
    private AccountInfo mAccountInfo;

    private AccountInfo mVisitorInfo;

    private boolean mIsVisitor; // 是否游客

    private AccountManager(Context context) {
//        mCache = ACache.get(context, ACCOUNT_KEY);
        mSharedPreferences = context.getSharedPreferences(ACCOUNT_KEY, Context.MODE_PRIVATE);
    }

    public static AccountManager getInstance() {
        if (sInstance == null) {
            sInstance = new AccountManager(MyApplication.getInstance().context);
        }
        return sInstance;
    }

    public boolean isVisitor() {
        getVisitorAccountInfoFormLocate();
        return mIsVisitor;
    }

    public void saveAccountInfoFormLocate(LoginBean data) {
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putString(USER_ID, data.getUserId());
        edit.putString(USER_NAME, data.getName());
        edit.putString(USER_HEAD, data.getHeadImgUrl());
        edit.putString(SESSION_ID, data.getSid());
        edit.putString(PHONE_NUMBER, data.getMobile());
        edit.apply();
        mAccountInfo = getAccountInfoFormLocate();
        savePhoneNumber(data.getMobile());
    }

    public void setVisitor(boolean visitor) {
        mIsVisitor = visitor;
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putBoolean(IS_VISITOR, mIsVisitor);
        edit.apply();
    }

    public void logout() {
//        mCache.put(SESSION_ID, "");

//        mCache.put(USER_ID, "");
//        mCache.put(USER_NAME, "");
//        mCache.put(USER_HEAD, "");
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putString(SESSION_ID, "");
        edit.putString(USER_ID, "");
        edit.putString(USER_NAME, "");
        edit.putString(USER_HEAD, "");
        edit.apply();
        savePhoneNumber("");
        mAccountInfo = null;
    }

    /**
     * 判断是否已经登录
     *
     * @return true:已经登录，false:未登录
     */
    public boolean isLogin() {
        return !TextUtils.isEmpty(getAccountInfo().getSessionId());
    }

    public boolean isVisitorLogin() {
        return !TextUtils.isEmpty(getVisitorInfo().getSessionId());
    }

    /**
     * 获取用户信息，包括userId,userName,UserHead,sessionId
     *
     * @return 这里返回的AccountInfo对象不可能为空
     */
    public AccountInfo getAccountInfo() {
        if (mAccountInfo == null || mAccountInfo.getSessionId() == null) {
            mAccountInfo = getAccountInfoFormLocate();
        }
        return mAccountInfo;
    }

    public AccountInfo getVisitorInfo() {
        if (mVisitorInfo == null || mVisitorInfo.getSessionId() == null) {
            getVisitorAccountInfoFormLocate();
        }

        return mVisitorInfo;
    }

    private AccountInfo getAccountInfoFormLocate() {
        mAccountInfo = new AccountInfo();
//        mAccountInfo.setUserId(mCache.getAsString(USER_ID));
//        mAccountInfo.setUserName(mCache.getAsString(USER_NAME));
//        mAccountInfo.setUserHead(mCache.getAsString(USER_HEAD));
//        mAccountInfo.setSessionId(mCache.getAsString(SESSION_ID));
//        mAccountInfo.setPhoneNumber(mCache.getAsString(PHONE_NUMBER));

        mAccountInfo.setUserId(mSharedPreferences.getString(USER_ID, ""));
        mAccountInfo.setUserName(mSharedPreferences.getString(USER_NAME, ""));
        mAccountInfo.setUserHead(mSharedPreferences.getString(USER_HEAD, ""));
        mAccountInfo.setSessionId(mSharedPreferences.getString(SESSION_ID, ""));
        mAccountInfo.setPhoneNumber(mSharedPreferences.getString(PHONE_NUMBER, ""));
        ImUserInfo.getInstance().setId(mSharedPreferences.getString(USER_ID, ""));
        ImUserInfo.getInstance().setUserSig(mSharedPreferences.getString(IM_USER_SIG, ""));

        return mAccountInfo;
    }

    private AccountInfo getVisitorAccountInfoFormLocate() {
        mVisitorInfo = new AccountInfo();
//        mAccountInfo.setUserId(mCache.getAsString(USER_ID));
//        mAccountInfo.setUserName(mCache.getAsString(USER_NAME));
//        mAccountInfo.setUserHead(mCache.getAsString(USER_HEAD));
//        mAccountInfo.setSessionId(mCache.getAsString(SESSION_ID));
//        mAccountInfo.setPhoneNumber(mCache.getAsString(PHONE_NUMBER));

        mVisitorInfo.setUserId(mSharedPreferences.getString(VISITOR_USER_ID, ""));
        mVisitorInfo.setUserName(mSharedPreferences.getString(VISITOR_USER_NAME, ""));
        mVisitorInfo.setUserHead(mSharedPreferences.getString(VISITOR_USER_HEAD, ""));
        mVisitorInfo.setSessionId(mSharedPreferences.getString(VISITOR_SESSION_ID, ""));
        mVisitorInfo.setPhoneNumber(mSharedPreferences.getString(VISITOR_PHONE_NUMBER, ""));
        ImUserInfo.getInstance().setUserSig(mSharedPreferences.getString(IM_USER_SIG, ""));
        mIsVisitor = mSharedPreferences.getBoolean(IS_VISITOR, false);
        return mVisitorInfo;
    }

    public AccountManager setUserHead(String url) {
        mAccountInfo.setUserHead(url);
//        mCache.put(USER_HEAD, url);
        mSharedPreferences.edit().putString(USER_HEAD, url).apply();
        return this;
    }

    public AccountManager setUserName(String name) {
        mAccountInfo.setUserName(name);
//        mCache.put(USER_NAME, name);
        mSharedPreferences.edit().putString(USER_NAME, name).apply();
        return this;
    }

    public void saveVisitorPhoneNumber(String phoneNumber) {
//        mCache.put(PHONE_NUMBER, phoneNumber);
        mSharedPreferences.edit().putString(VISITOR_PHONE_NUMBER, phoneNumber).apply();
        if (mAccountInfo == null) {
            mAccountInfo = getAccountInfoFormLocate();
        }
        mAccountInfo.setPhoneNumber(phoneNumber);
    }

    public void savePhoneNumber(String phoneNumber) {
//        mCache.put(PHONE_NUMBER, phoneNumber);
        mSharedPreferences.edit().putString(PHONE_NUMBER, phoneNumber).apply();
        if (mAccountInfo == null) {
            mAccountInfo = getAccountInfoFormLocate();
        }
        mAccountInfo.setPhoneNumber(phoneNumber);
    }


    public static String getUserId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(ACCOUNT_KEY, Context.MODE_PRIVATE);
        return sharedPreferences.getString(USER_ID, "");
    }

    public static String getSessionId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(ACCOUNT_KEY, Context.MODE_PRIVATE);
        return sharedPreferences.getString(SESSION_ID, "");
    }

    public void setIMUserSig(String string) {
        mSharedPreferences.edit().putString(IM_USER_SIG, string).apply();
    }
    public String getIMUserSig() {
       return mSharedPreferences.getString(IM_USER_SIG, "");
    }
}
