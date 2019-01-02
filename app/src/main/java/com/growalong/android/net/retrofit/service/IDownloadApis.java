package com.growalong.android.net.retrofit.service;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by yangxing on 2018/12/21.
 */
public interface IDownloadApis {
    @GET
    Call<ResponseBody> downloadFile(@Url String fileUrl);

    @Streaming
    @GET
    Observable<ResponseBody> downloadObsv(@Url String fileUrl);

    @Streaming
    @GET
    Observable<ResponseBody> downloadObsvResume(@Url String fileUrl,  @Header("RANGE") String range);
}
