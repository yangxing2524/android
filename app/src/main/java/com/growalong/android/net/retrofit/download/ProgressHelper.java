package com.growalong.android.net.retrofit.download;

import com.orhanobut.logger.Logger;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

/**
 * Created by murphy on 14/11/2016.
 */

public class ProgressHelper {
    private static ProgressBean progressBean = new ProgressBean();
    private static ProgressHandler mProgressHandler;

    public static OkHttpClient.Builder addProgress(OkHttpClient.Builder builder) {

        if (builder == null) {
            builder = new OkHttpClient.Builder();
        }
        progressBean.setMax(100);
        final ProgressListener progressListener = new ProgressListener() {
            //该方法在子线程中运行
            @Override
            public void onProgress(long progress, long total, boolean done) {
                Logger.i("progress: " + progress + "__total: " + total);
                if (mProgressHandler == null) {
                    return;
                }
                int current = (int) ((100 * progress) / total);
                progressBean.setBytesRead(progress);
                progressBean.setContentLength(total);
                if (current == 100) {
                    progressBean.setDone(true);
                } else {
                    progressBean.setDone(false);
                }


                if (current > progressBean.getCurrent()) {
                    mProgressHandler.sendMessage(progressBean);
                }
                progressBean.setCurrent(current);
            }
        };

        //添加拦截器，自定义ResponseBody，添加下载进度
        builder.networkInterceptors().add(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                okhttp3.Response originalResponse = chain.proceed(chain.request());
                return originalResponse.newBuilder().body(
                        new ProgressResponseBody(originalResponse.body(), progressListener))
                        .build();

            }
        });

        return builder;
    }

    public static void setProgressHandler(ProgressHandler progressHandler) {
        mProgressHandler = progressHandler;
    }
}
