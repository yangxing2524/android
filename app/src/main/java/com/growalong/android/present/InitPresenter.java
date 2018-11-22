package com.growalong.android.present;

import com.growalong.android.model.BaseParams;
import com.growalong.android.model.NetDataListString;
import com.growalong.android.model.NoDataParams;
import com.growalong.android.net.retrofit.BaseRetrofitClient;
import com.growalong.android.net.retrofit.service.IInitApis;
import com.growalong.android.util.RxUtil;

import java.util.List;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by yangxing on 2018/11/21.
 */
public class InitPresenter {
    private final IInitApis iInitApis;

    public InitPresenter() {
        iInitApis = BaseRetrofitClient.getInstance().create(IInitApis.class);
    }

    public Observable<List<String>> getRoom() {
        BaseParams<NoDataParams> baseParams = new BaseParams<>(new NoDataParams());
        return iInitApis.getRoom(baseParams).compose(RxUtil.<NetDataListString>handleResult())
                .map(new Func1<NetDataListString, List<String>>() {
                    @Override
                    public List<String> call(NetDataListString tabListModel) {
                        return tabListModel.getDataList();
                    }
                })
                .compose(NewBasePresenter.<List<String>>asyAndMainResponseTransformer());//网络操作在异步线程，观察者在主线程;
    }

}
