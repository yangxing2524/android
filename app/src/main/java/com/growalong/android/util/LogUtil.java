package com.growalong.android.util;

import android.util.Log;

import com.growalong.android.BuildConfig;

/**
 * Created by codeest on 2016/8/3.
 */
public class LogUtil {

    public static boolean isDebug = BuildConfig.DEBUG;
    private static final String TAG = "com.thinkwu.live";

    public static void e(String tag,String o) {
        if(isDebug) {
            Log.e(tag, o);
        }
    }

    public static void e(String o) {
        LogUtil.e(TAG,o);
    }

    public static void w(String tag,String o) {
        if(isDebug) {
            Log.w(tag, o);
        }
    }

    public static void w(String o) {
        Log.d(TAG,o);
    }

    public static void d(String msg) {
        if(isDebug) {
            Log.d(TAG, msg);
        }
    }

    public static void d(String tag, String msg) {
        if(isDebug) {
            Log.d(tag, msg);
        }
    }

    public static void i(String msg) {
        if(isDebug) {
            Log.d(TAG,msg);
        }
    }
}
