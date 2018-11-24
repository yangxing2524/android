package com.growalong.android.net.retrofit.service;

import com.google.gson.JsonElement;
import com.growalong.android.model.BaseGenericModel;
import com.growalong.android.model.BaseParams;
import com.growalong.android.model.NetLoginBean;
import com.growalong.android.model.NetLoginIMBean;
import com.growalong.android.net.retrofit.ApiConstants;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by gangqing on 2016/10/9.
 */

public interface ILoginApis {

    @POST(ApiConstants.loginForPhoneNumber)
    Observable<BaseGenericModel<NetLoginBean>> loginForPassword(@Body BaseParams body);

    @POST(ApiConstants.loginForIM)
    Observable<BaseGenericModel<NetLoginIMBean>> loginForIM(@Body BaseParams body);

    @POST(ApiConstants.logout)
    Observable<BaseGenericModel<JsonElement>> logout(@Body BaseParams baseParams);

}

