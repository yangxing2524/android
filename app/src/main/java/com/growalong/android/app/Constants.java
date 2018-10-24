package com.growalong.android.app;

import java.io.File;

/**
 */
public class Constants {
    public static String baseHttp = "http://app.grow-along.com";
    public static final String PATH_DATA = MyApplication.getInstance().context.getCacheDir().getAbsolutePath() + File.separator + "data";
    public static final String PATH_CACHE = PATH_DATA + "/NetCache";
}
