package com.growalong.android.util;

import android.app.Activity;
import android.content.Intent;

import com.growalong.android.account.AccountManager;
import com.growalong.android.app.AppManager;
import com.growalong.android.model.ApiException;
import com.growalong.android.model.BaseGenericModel;
import com.growalong.android.net.retrofit.HttpStatusCode;
import com.growalong.android.ui.LoginMainActivity;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by chenjiawei on 2016/8/3.
 */
public class RxUtil {
    /**
     * 统一返回结果处理
     *
     * @param <T>
     * @return
     */
    public static <T> Observable.Transformer<BaseGenericModel<T>, T> handleResult() {   //compose判断结果
        return new Observable.Transformer<BaseGenericModel<T>, T>() {
            @Override
            public Observable<T> call(Observable<BaseGenericModel<T>> httpResponseObservable) {
                return httpResponseObservable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMap(new Func1<BaseGenericModel<T>, Observable<T>>() {
                            @Override
                            public Observable<T> call(BaseGenericModel<T> httpResponse) {
                                int stateCode = httpResponse.getState().getCode();
                                if (HttpStatusCode.STATUS_SUCCESS == stateCode) {
                                    return createData(httpResponse.getData());
                                } else if (HttpStatusCode.STATUS_NO_LOGIN == stateCode || HttpStatusCode.STATUS_SID_LOST == stateCode) {
                                    //没有登录
                                    startLogin();
                                    return Observable.error(new ApiException(httpResponse.getState().getMsg(), httpResponse.getState().getCode()));
                                } else if (HttpStatusCode.STATUS_ALREADY_LOGIN == stateCode) {
                                    //已在别的地方登录
                                    startLogin();
                                    AppManager.getInstance().finishAllActivity(LoginMainActivity.class);
                                    return Observable.error(new ApiException(httpResponse.getState().getMsg(), httpResponse.getState().getCode()));
                                } else {
                                    return Observable.error(new ApiException(httpResponse.getState().getMsg(), httpResponse.getState().getCode()));
                                }
                            }
                        });
            }
        };
    }

    /**
     * 统一返回结果处理
     *
     * @param <T>
     * @return
     */
    public static <T> Observable.Transformer<BaseGenericModel<T>, T> handleResultSync() {   //compose判断结果
        return new Observable.Transformer<BaseGenericModel<T>, T>() {
            @Override
            public Observable<T> call(Observable<BaseGenericModel<T>> httpResponseObservable) {
                return httpResponseObservable
                        .flatMap(new Func1<BaseGenericModel<T>, Observable<T>>() {
                            @Override
                            public Observable<T> call(BaseGenericModel<T> httpResponse) {
                                int stateCode = httpResponse.getState().getCode();
                                if (HttpStatusCode.STATUS_SUCCESS == stateCode) {
                                    return createData(httpResponse.getData());
                                } else if (HttpStatusCode.STATUS_NO_LOGIN == stateCode || HttpStatusCode.STATUS_SID_LOST == stateCode) {
                                    //没有登录
                                    startLogin();
                                    return Observable.error(new ApiException(httpResponse.getState().getMsg(), httpResponse.getState().getCode()));
                                } else if (HttpStatusCode.STATUS_ALREADY_LOGIN == stateCode) {
                                    //已在别的地方登录
                                    startLogin();
                                    AppManager.getInstance().finishAllActivity(LoginMainActivity.class);
                                    return Observable.error(new ApiException(httpResponse.getState().getMsg(), httpResponse.getState().getCode()));
                                } else {
                                    return Observable.error(new ApiException(httpResponse.getState().getMsg(), httpResponse.getState().getCode()));
                                }
                            }
                        });
            }
        };
    }

    /**
     * 生成Observable
     *
     * @param <T>
     * @return
     */
    public static <T> Observable<T> createData(final T t) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                try {
                    subscriber.onNext(t);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    private static void startLogin() {
        Activity activity = AppManager.getInstance().currentActivity();
        AccountManager.getInstance().logout();
        if (activity != null) {
            Intent intent = new Intent(activity, LoginMainActivity.class);
            intent.putExtra(LoginMainActivity.KEY_IS_START_APP, true);
            activity.startActivity(intent);
        }
    }
}
