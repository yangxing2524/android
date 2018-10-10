package com.growalong.android.trtc.model;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Created by tencent on 2018/8/30.
 */
public class ConfigInfo {
    private static ConfigInfo instance = null;

    private boolean mConfigLoad;
    private int mSdkAppId;
    private HashMap<String, String> mUserMap;
    private HashMap<String, String> mRoleMap;

    private ConfigInfo(){
        mConfigLoad = false;
        mSdkAppId = 0;
        mUserMap = new HashMap<>();
        mRoleMap = new HashMap<>();
    }

    public static ConfigInfo getInstance(){
        if (null == instance){
            instance = new ConfigInfo();
        }
        return instance;
    }

    /*** 加载配置文件 */
    public void loadConfig(Context context, int resId) throws Exception {
        InputStream is = context.getResources().openRawResource(resId);
        String jsonData = readTextFromInputStream(is);
        loadJsonData(jsonData);
        mConfigLoad = true;
    }

    /** 判断配置是否加载成功 */
    public boolean isConfigLoad() {
        return mConfigLoad;
    }

    public void setmConfigLoad(boolean mConfigLoad) {
        this.mConfigLoad = mConfigLoad;
    }

    /** 在配置文件中获取SDKAPPID */
    public int getSdkAppId() {
        return mSdkAppId;
    }

    /** 在配置文件中获取用户登录信息 */
    public HashMap<String, String> getUserMap() {
        return mUserMap;
    }

    /** 在配置文件中获取角色信息 */
    public HashMap<String, String> getRoleMap() {
        return mRoleMap;
    }

    /** 读取资源文件 */
    private String readTextFromInputStream(InputStream is) throws Exception {
        InputStreamReader reader = new InputStreamReader(is);
        BufferedReader bufferedReader = new BufferedReader(reader);
        StringBuffer buffer = new StringBuffer("");
        String str;
        while (null != (str = bufferedReader.readLine())){
            buffer.append(str);
            buffer.append("\n");
        }
        return buffer.toString();
    }

    /** 解析JSON配置文件 */
    private void loadJsonData(String jsonData) throws Exception {
        JSONTokener jsonTokener = new JSONTokener(jsonData);
        JSONObject msgJson = (JSONObject) jsonTokener.nextValue();
        mSdkAppId = msgJson.getInt("sdkappid");
        JSONArray jsonUsersArr = msgJson.getJSONArray("users");
        if (null != jsonUsersArr) {
            for (int i = 0; i < jsonUsersArr.length(); i++) {
                JSONObject jsonUser = jsonUsersArr.getJSONObject(i);
                mUserMap.put(jsonUser.getString("userId"),
                        jsonUser.getString("userToken"));
            }
        }
        JSONArray jsonRoleArr = msgJson.getJSONArray("roles");
        if (null != jsonRoleArr) {
            for (int i = 0; i < jsonRoleArr.length(); i++) {
                JSONObject jsonRole = jsonRoleArr.getJSONObject(i);
                mRoleMap.put(jsonRole.getString("name"),
                        jsonRole.getString("value"));
            }
        }
    }
}
