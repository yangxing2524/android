package com.growalong.android.present;

import rx.Subscriber;

/**
 */

public abstract class CommSubscriber<T> extends Subscriber<T> {
    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        try {
            onFailure(e);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        e.printStackTrace();
    }

    @Override
    public void onNext(T t) {
        try {
            onSuccess(t);
        } catch (Exception e) {
            e.printStackTrace();
            onFailure(e);
        }
    }

    public abstract void onSuccess(T t);

    public void onFailure(Throwable e) {
    }
}
