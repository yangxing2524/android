package com.growalong.android.net.retrofit;

import android.text.TextUtils;

import com.growalong.android.account.AccountManager;
import com.growalong.android.app.Constants;
import com.growalong.android.util.NetWorkUtil;

import java.io.File;
import java.io.IOException;
import java.net.CookieManager;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by murphy on 8/10/16.
 */
public class BaseRetrofitClient {
    private static final long DEFAULT_CONECTION_MILLISECONDS = 20;
    private static BaseRetrofitClient mInstance;
    private Retrofit retrofit;
    private CookieManager cookieManager;
    public static final long DEFAULT_MILLISECONDS = 20;

    public static BaseRetrofitClient getInstance() {
        if (mInstance == null) {
            mInstance = new BaseRetrofitClient();
        }

        return mInstance;
    }

    private BaseRetrofitClient() {
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.baseHttp)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(newOkHttpClient())
                .build();
    }

    public Retrofit getOtherRetrofit(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl(Constants.baseHttp)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
    }

    private OkHttpClient newOkHttpClient() {


        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        okHttpClientBuilder
                .connectTimeout(DEFAULT_CONECTION_MILLISECONDS, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_MILLISECONDS, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_MILLISECONDS, TimeUnit.SECONDS)
                .cache(new Cache(new File(Constants.PATH_CACHE), 1024 * 1024 * 50))
                .retryOnConnectionFailure(true)
                .addNetworkInterceptor(new HttpCacheInterceptor())
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        //添加头
                        Request request = chain.request();

                        Request.Builder builder = request.newBuilder();
                        builder.addHeader("Content-Type", "application/json;charset=UTF-8");
                        if (AccountManager.getInstance().isLogin()) {
                            if (AccountManager.getInstance().getAccountInfo() != null && !TextUtils.isEmpty(AccountManager.getInstance().getAccountInfo().getUserId())) {
                                builder.addHeader("userId", AccountManager.getInstance().getAccountInfo().getUserId());
                            }
                        }
                        Response response = chain.proceed(builder.build());
                        return response;
                    }
                })
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String s, SSLSession sslSession) {
                        return true;
//                        boolean ret = false;
//                        String hostName = HttpDnsManager.newInstance().getHost();
//                        if (TextUtils.isEmpty(hostName)){
//                            return true;
//                        }
//                        return hostName.indexOf(s)>=0;
//                        List<String> list = InitParamManager.newInstance().getInitParams().getApp_support_httpdns_domains();
//                        if (list == null || list.size() == 0){
//                            return true;
//                        }
//                        for (String host : list) {
//                            if (host.equalsIgnoreCase(hostName)) {
//                                ret = true;
//                            }
//                        }
//                        return ret;
                    }
                });
        //添加日志输出
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        okHttpClientBuilder.addInterceptor(loggingInterceptor);

        //重试
//        RetryIntercepter retryIntercepter = new RetryIntercepter(2);
//        okHttpClientBuilder.addInterceptor(retryIntercepter);
        return okHttpClientBuilder.build();
    }

    /**
     * 重试拦截器
     */
    public class RetryIntercepter implements Interceptor {

        public int maxRetry;//最大重试次数
        private int retryNum = 0;//假如设置为3次重试的话，则最大可能请求4次（默认1次+3次重试）

        public RetryIntercepter(int maxRetry) {
            this.maxRetry = maxRetry;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Response response = null;
            Request request = null;
            try {
                request = chain.request();
                response = chain.proceed(request);
            } catch (Exception e) {
                e.printStackTrace();
            }

            while (response != null && request != null && !response.isSuccessful() && retryNum < maxRetry) {
                retryNum++;
                response = chain.proceed(request);
            }
            return response;
        }
    }

    public <T> T create(final Class<T> service) {
        return retrofit.create(service);
    }

    class HttpCacheInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (!NetWorkUtil.isNetworkConnected()) {
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build();
            }
            Response response = chain.proceed(request);
            if (NetWorkUtil.isNetworkConnected()) {
                int maxAge = 0;
                // 有网络时, 不缓存, 最大保存时长为0
                response.newBuilder()
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .removeHeader("Pragma")
                        .build();
            } else {
                // 无网络时，设置超时为4周
                int maxStale = 60 * 60 * 24 * 28;
                response.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .removeHeader("Pragma")
                        .build();
            }
            return response;
        }
    }

    public interface DownloadCallback {
        void onSuccess(String file, String url);

        void onFailed(String file, String url, Throwable e);
    }

}
