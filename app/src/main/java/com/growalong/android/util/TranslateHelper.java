package com.growalong.android.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by yangxing on 2018/12/8.
 */
public class TranslateHelper {
    public static String TRANSLATE_ID = "20180922000210902";
    public static String TRANSLATE_KEY = "c0SBGb9wu9HSTC0chHhO";

    public static String TRANS_API_HOST = "http://api.fanyi.baidu.com/api/trans/vip/translate";

    public enum TrLanguage {
        zh,
        en
    }

    protected static final int SOCKET_TIMEOUT = 10000; // 10S
    protected static final String GET = "GET";


    private static void get(String host, Map<String, String> params, Callback callback) {
        String sendUrl = getUrlWithQueryString(host, params);
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(sendUrl)
                .build();

        Call call = client.newCall(request);

        call.enqueue(callback);
    }

    private static String getUrlWithQueryString(String url, Map<String, String> params) {
        if (params == null) {
            return url;
        }

        StringBuilder builder = new StringBuilder(url);
        if (url.contains("?")) {
            builder.append("&");
        } else {
            builder.append("?");
        }

        int i = 0;
        for (String key : params.keySet()) {
            String value = params.get(key);
            if (value == null) { // 过滤空的key
                continue;
            }

            if (i != 0) {
                builder.append('&');
            }

            builder.append(key);
            builder.append('=');
            builder.append(encode(value));

            i++;
        }

        return builder.toString();
    }

    protected static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 对输入的字符串进行URL编码, 即转换为%20这种形式
     *
     * @param input 原文
     * @return URL编码. 如果编码失败, 则返回原文
     */
    public static String encode(String input) {
        if (input == null) {
            return "";
        }

        try {
            return URLEncoder.encode(input, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return input;
    }


    public static void getTransResult(String query, String from, String to, Callback callback) {
        Map<String, String> params = buildParams(query, from, to);
        get(TRANS_API_HOST, params, callback);
    }

    private static Map<String, String> buildParams(String query, String from, String to) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("q", query);
        params.put("from", from);
        params.put("to", to);

        params.put("appid", TRANSLATE_ID);

        // 随机数
        String salt = String.valueOf(System.currentTimeMillis());
        params.put("salt", salt);

        // 签名
        String src = TRANSLATE_ID + query + salt + TRANSLATE_KEY; // 加密前的原文
        params.put("sign", Md5Utils.getMD5(src));

        return params;
    }
}
