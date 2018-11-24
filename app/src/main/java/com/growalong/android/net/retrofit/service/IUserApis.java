package com.growalong.android.net.retrofit.service;

import com.google.gson.JsonElement;
import com.growalong.android.model.BaseGenericModel;
import com.growalong.android.model.BaseParams;
import com.growalong.android.model.NetCollectModel;
import com.growalong.android.model.UserInfoModel;
import com.growalong.android.net.retrofit.ApiConstants;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by yangxing on 2018/11/14.
 */
public interface IUserApis {
    @POST(ApiConstants.getUserInfo)
    Observable<BaseGenericModel<UserInfoModel>> getUserInfo(@Body BaseParams baseParams);

    @POST(ApiConstants.updateUserInfo)
    Observable<BaseGenericModel<JsonElement>> updateUserInfo(@Body BaseParams baseParams);

    @POST(ApiConstants.getMyCollect)
    Observable<BaseGenericModel<NetCollectModel>> getMyCollect(@Body BaseParams baseParams);

    @POST(ApiConstants.addCollect)
    Observable<BaseGenericModel<JsonElement>> addCollect(@Body BaseParams baseParams);

    @POST(ApiConstants.logout)
    Observable<BaseGenericModel<JsonElement>> logout(@Body BaseParams baseParams);
}
