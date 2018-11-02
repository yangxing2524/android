package com.growalong.android.net.retrofit.service;

import com.growalong.android.model.BaseGenericModel;
import com.growalong.android.model.BaseParams;
import com.growalong.android.net.retrofit.ApiConstants;
import com.growalong.android.upload.OosDataModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by murphy on 10/12/16.
 */

public interface IUploadApis {
    @POST(ApiConstants.imgUpload)
    Observable<BaseGenericModel<OosDataModel>> getUploadToken(@Body BaseParams baseParams);

    @POST(ApiConstants.imgUpload)
    Call<BaseGenericModel<OosDataModel>> getUploadParams(@Body BaseParams baseParams);
}
