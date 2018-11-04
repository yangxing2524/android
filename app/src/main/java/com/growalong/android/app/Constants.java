package com.growalong.android.app;

import com.growalong.android.BuildConfig;

import java.io.File;

/**
 */
public class Constants {
    public static final boolean isOnline = !BuildConfig.DEVELOPMENT_ENV;

    public static String baseHttp = "http://app.grow-along.com";
    public static final String PATH_DATA = MyApplication.getInstance().context.getCacheDir().getAbsolutePath() + File.separator + "data";
    public static final String PATH_CACHE = PATH_DATA + "/NetCache";
    public static final String endpoint = "https://oss-cn-beijing.aliyuncs.com";
    public static final String bucketName = "growalong-oss";
    public static String private_key = "846d2cb0c7f09c3ae582c421696d308c";
}
