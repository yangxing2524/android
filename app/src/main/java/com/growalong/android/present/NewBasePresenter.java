package com.growalong.android.present;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

public class NewBasePresenter {
    public static  int pageSize = 20;
    /**
     * 显示loading dialog和关闭loading dialog的
     *
     * @param showWaiting
     * @param hideWaiting
     * @param <P>
     * @return
     */
    public static <P> Observable.Transformer<P, P> showWaitingTransformer(final Action0 showWaiting, final Action0 hideWaiting) {
        return new Observable.Transformer<P, P>() {
            @Override
            public Observable<P> call(Observable<P> observable) {
                if (showWaiting == null || hideWaiting == null) {
                    return observable;
                }
                return observable.doOnSubscribe(showWaiting)
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .doOnTerminate(hideWaiting)
                        ;
            }
        };
    }

    public static <Q> Observable.Transformer<Q, Q> asyAndMainResponseTransformer() {
        return new Observable.Transformer<Q, Q>() {
            @Override
            public Observable<Q> call(Observable<Q> observable) {
                return observable.subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    public static <R> Observable.Transformer<R, R> asyAndAsyResponseTransformer() {
        return new Observable.Transformer<R, R>() {
            @Override
            public Observable<R> call(Observable<R> observable) {
                return observable.subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io());
            }
        };
    }
}
