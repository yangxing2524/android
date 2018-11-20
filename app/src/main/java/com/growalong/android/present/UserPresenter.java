package com.growalong.android.present;

import com.growalong.android.model.BaseParams;
import com.growalong.android.model.CollectModel;
import com.growalong.android.model.NetCollectModel;
import com.growalong.android.model.UserInfoModel;
import com.growalong.android.model.request.CourseIdPageParams;
import com.growalong.android.model.request.PageParams;
import com.growalong.android.model.request.UserIdTypeParams;
import com.growalong.android.net.retrofit.BaseRetrofitClient;
import com.growalong.android.net.retrofit.service.IUserApis;
import com.growalong.android.util.RxUtil;

import java.util.List;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by yangxing on 2018/11/14.
 */
public class UserPresenter {
    private IUserApis iCourseApis;

    public UserPresenter() {
        iCourseApis = BaseRetrofitClient.getInstance().create(IUserApis.class);
    }

    public Observable<UserInfoModel> getCourseDetail(String userId, String type) {
        BaseParams<UserIdTypeParams> baseParams = new BaseParams<>(new UserIdTypeParams(type, Long.valueOf(userId)));
        return iCourseApis.getUserInfo(baseParams).compose(RxUtil.<UserInfoModel>handleResult())
                .compose(NewBasePresenter.<UserInfoModel>asyAndMainResponseTransformer());//网络操作在异步线程，观察者在主线程;
    }

    public Observable<List<CollectModel>> getMyCollect(int page) {
        BaseParams<CourseIdPageParams> baseParams = new BaseParams<>(new CourseIdPageParams(new PageParams(page, 20)));
        return iCourseApis.getMyCollect(baseParams).compose(RxUtil.<NetCollectModel>handleResult())
                .map(new Func1<NetCollectModel, List<CollectModel>>() {
                    @Override
                    public List<CollectModel> call(NetCollectModel netCollectModel) {
                        return netCollectModel.getDataList();
                    }
                })
                .compose(NewBasePresenter.<List<CollectModel>>asyAndMainResponseTransformer());//网络操作在异步线程，观察者在主线程;
    }
}
