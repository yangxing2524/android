package com.growalong.android.net.retrofit.service;

import com.growalong.android.model.BaseGenericModel;
import com.growalong.android.model.BaseParams;
import com.growalong.android.model.CourseDetailInfoModel;
import com.growalong.android.model.NetCourseMaterialModel;
import com.growalong.android.model.NetCourseModel;
import com.growalong.android.net.retrofit.ApiConstants;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

public interface ICourseApis {
    /**
     * 首页的课程列表
     */
    @POST(ApiConstants.courseList)
    Observable<BaseGenericModel<NetCourseModel>> getCourseList(@Body BaseParams baseParams);


    @POST(ApiConstants.courseDetailList)
    Observable<BaseGenericModel<CourseDetailInfoModel>> getCourseDetail(@Body BaseParams baseParams);

    @POST(ApiConstants.courseMaterialList)
    Observable<BaseGenericModel<NetCourseMaterialModel>> getCourseMaterail(@Body BaseParams baseParams);

}
