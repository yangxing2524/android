package com.growalong.android.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


/**
 * Created by codeest on 16/8/31.
 */

public class SharedPreferenceUtil {

    private static SharedPreferences sp;
    private static String defaultFileName;

    public static SharedPreferenceUtil getInstance(Context context) {
        if(sp == null) {
            sp =  PreferenceManager.getDefaultSharedPreferences(context);
        }
        return new SharedPreferenceUtil();
    }

    public static SharedPreferenceUtil getInstance(Context context, String fileName) {
        if(defaultFileName == null || !defaultFileName.trim().equals(fileName)) {
            defaultFileName =  fileName;
            sp = context.getSharedPreferences(fileName, context.MODE_PRIVATE);
        }
        return new SharedPreferenceUtil();
    }

    public static boolean hasKey(final String key) {
        return sp.contains(key);
    }

    public String getString(String key, final String defaultValue) {
        return sp.getString(key, defaultValue);
    }

    public void setString(final String key, final String value) {
        sp.edit().putString(key, value).commit();
    }

    public boolean getBoolean(final String key, final boolean defaultValue) {
        return sp.getBoolean(key, defaultValue);
    }

    public void setBoolean(final String key, final boolean value) {
        sp.edit().putBoolean(key, value).commit();
    }

    public void setInt(final String key, final int value) {
        sp.edit().putInt(key, value).commit();
    }

    public int getInt(final String key, final int defaultValue) {
        return sp.getInt(key, defaultValue);
    }

    public void setFloat(final String key, final float value) {
        sp.edit().putFloat(key, value).commit();
    }

    public float getFloat(final String key, final float defaultValue) {
        return sp.getFloat(key, defaultValue);
    }

    public void setLong(final String key, final long value) {
        sp.edit().putLong(key, value).commit();
    }

    public long getLong(final String key, final long defaultValue) {
        return sp.getLong(key, defaultValue);
    }


}
