package com.growalong.android.net.retrofit.service;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by yangxing on 2018/12/21.
 */
public interface IDownloadApis {
    @GET
    Call<ResponseBody> downloadFile(@Url String fileUrl);
}
