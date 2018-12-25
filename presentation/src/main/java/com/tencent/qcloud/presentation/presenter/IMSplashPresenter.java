package com.tencent.qcloud.presentation.presenter;

import android.os.Handler;
import com.tencent.qcloud.presentation.viewfeatures.SplashView;


/**
 * 闪屏界面逻辑
 */
public class IMSplashPresenter {
    SplashView view;
    private static final String TAG = IMSplashPresenter.class.getSimpleName();

    public IMSplashPresenter(SplashView view) {
        this.view = view;
    }


    /**
     * 加载页面逻辑
     */
    public void start() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (view.isUserLogin()) {
                    view.navToHome();
                } else {
                    view.navToLogin();
                }
            }
        }, 500);
    }


}
