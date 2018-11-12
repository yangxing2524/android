package com.growalong.android.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.growalong.android.ui.QLActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Subscription;
import rx.functions.Action0;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by yangxing on 2017/1/3.
 */

public abstract class NewBaseFragment extends Fragment {
    public CompositeSubscription mCompositeSubscription;
    protected QLActivity activity;
    public Unbinder mUnBinder;
    protected View mView;
    protected boolean isInit;
    public final int PAGE_SIZE = 20;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCompositeSubscription = new CompositeSubscription();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mView == null) {
            mView = inflater.inflate(getLayoutId(), container, false);
            isInit = false;
        }
        mUnBinder = ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!isInit) {
            isInit = true;
            initEventAndData(savedInstanceState, mView);
        } else if (null != view) {
            view.requestLayout();
            viewRefresh();
        }
    }

    protected void viewRefresh() {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    protected abstract void initEventAndData(Bundle savedInstanceState, View view);

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mUnBinder != null)
            mUnBinder.unbind();
    }

    protected abstract int getLayoutId();

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCompositeSubscription.unsubscribe();
    }

    protected void addSubscribe(Subscription subscribe) {
        if (mCompositeSubscription != null && !mCompositeSubscription.isUnsubscribed())
            mCompositeSubscription.add(subscribe);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (QLActivity) context;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    private boolean mIsShowCurrentFragment;
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        mIsShowCurrentFragment = isVisibleToUser;
        isShowThisFragment(mIsShowCurrentFragment);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (mIsShowCurrentFragment) {
            isShowThisFragment(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isShowThisFragment(false);
    }

    public void isShowThisFragment(boolean b) {
    }


    public final Action0 doOnTerminate = new Action0() {
        @Override
        public void call() {
            activity.hideLoadingDialog();
        }
    };

    public final Action0 doOnSubscribe = new Action0() {
        @Override
        public void call() {
            activity.showLoadingDialog("加载中...");
        }
    };

    public void showLoadingDialog(String msg) {
        if (activity != null) {
            this.activity.showLoadingDialog(msg);
        }
    }
    public void showLoadingDialog() {
        showLoadingDialog("");
    }

    public void hideLoadingDialog() {
        if (activity != null) {
            this.activity.hideLoadingDialog();
        }
    }
}
