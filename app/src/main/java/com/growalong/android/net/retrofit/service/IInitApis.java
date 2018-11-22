package com.growalong.android.net.retrofit.service;

import com.growalong.android.model.BaseGenericModel;
import com.growalong.android.model.BaseParams;
import com.growalong.android.model.NetDataListString;
import com.growalong.android.net.retrofit.ApiConstants;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by yangxing on 2018/11/21.
 */
public interface IInitApis {

    @POST(ApiConstants.getRoom)
    Observable<BaseGenericModel<NetDataListString>> getRoom(@Body BaseParams baseParams);
}
