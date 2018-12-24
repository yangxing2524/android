package com.growalong.android.present;

import com.growalong.android.model.BaseParams;
import com.growalong.android.model.CourseDetailInfoModel;
import com.growalong.android.model.CourseListItemModel;
import com.growalong.android.model.CourseMaterialModel;
import com.growalong.android.model.NetCourseMaterialModel;
import com.growalong.android.model.NetCourseModel;
import com.growalong.android.model.request.CourseIdParams;
import com.growalong.android.model.request.CourseMaterialParams;
import com.growalong.android.model.request.RequestCourseItemList;
import com.growalong.android.net.retrofit.BaseRetrofitClient;
import com.growalong.android.net.retrofit.service.ICourseApis;
import com.growalong.android.util.RxUtil;

import java.util.List;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Func1;

/**
 */
public class CoursePresenter {
    private final ICourseApis iCourseApis;

    public CoursePresenter() {
        iCourseApis = BaseRetrofitClient.getInstance().create(ICourseApis.class);
    }

    public Observable<List<CourseListItemModel>> getCourList(int status, int page, Action0 showWaiting, Action0 hideWaiting) {
        BaseParams<RequestCourseItemList> baseParams = new BaseParams<>(new RequestCourseItemList(status, page));
        return iCourseApis.getCourseList(baseParams).compose(RxUtil.<NetCourseModel>handleResult())
                .map(new Func1<NetCourseModel, List<CourseListItemModel>>() {
                    @Override
                    public List<CourseListItemModel> call(NetCourseModel tabListModel) {
                        return tabListModel.getDataList();
                    }
                })
                .compose(NewBasePresenter.<List<CourseListItemModel>>showWaitingTransformer(showWaiting, hideWaiting))//显示loading转圈圈
                .compose(NewBasePresenter.<List<CourseListItemModel>>asyAndMainResponseTransformer());//网络操作在异步线程，观察者在主线程;
    }

    public Observable<CourseDetailInfoModel> getCourseDetail(long courseId) {
        BaseParams<CourseIdParams> baseParams = new BaseParams<>(new CourseIdParams(courseId));
        return iCourseApis.getCourseDetail(baseParams).compose(RxUtil.<CourseDetailInfoModel>handleResult())
                .compose(NewBasePresenter.<CourseDetailInfoModel>asyAndMainResponseTransformer());//网络操作在异步线程，观察者在主线程;
    }

    public Observable<List<CourseMaterialModel>> getCourseMaterial(long courseId, int type) {
        BaseParams<CourseMaterialParams> baseParams = new BaseParams<>(new CourseMaterialParams(courseId, type));
        return iCourseApis.getCourseMaterail(baseParams).compose(RxUtil.<NetCourseMaterialModel>handleResult())
                .map(new Func1<NetCourseMaterialModel, List<CourseMaterialModel>>() {
                    @Override
                    public List<CourseMaterialModel> call(NetCourseMaterialModel netCourseMaterialModel) {
                        return netCourseMaterialModel.getDataList();
                    }
                })
                .compose(NewBasePresenter.<List<CourseMaterialModel>>asyAndMainResponseTransformer());//网络操作在异步线程，观察者在主线程;
    }


}
