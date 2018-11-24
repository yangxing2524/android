package com.growalong.android.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.growalong.android.R;
import com.growalong.android.app.AppManager;
import com.growalong.android.app.MyApplication;
import com.growalong.android.model.rxevent.NetInfoEvent;
import com.growalong.android.ui.dialog.LoadingDialog;
import com.growalong.android.ui.widget.StatusBarCompat;
import com.growalong.android.util.PermissionUtils;
import com.orhanobut.logger.Logger;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by murphy on 10/9/16.
 */

public abstract class QLActivity extends AppCompatActivity {
    private LoadingDialog mDialog;
    protected Activity mContext;
    private Unbinder mUnBinder;
    public CompositeSubscription mCompositeSubscription;
    private ConnectivityBroadcastReceiver netWorkStateReceiver;//监听网络变化
    /*根视图*/
    public View view;
    private FrameLayout contentLayout;
    //    private RelativeLayout rl_loading;
    private TextView tv_no_net;//全局的无网络提示

    public final Action0 doOnTerminate = new Action0() {
        @Override
        public void call() {
            hideLoadingDialog();
        }
    };

    public final Action0 doOnSubscribe = new Action0() {
        @Override
        public void call() {
            showLoadingDialog("加载中...");
            MyApplication.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    if (isRunningApp(QLActivity.this, this.getClass().getName()) && mDialog != null && mDialog.isShow()) {
                        mDialog.setText("网速有点慢，努力加载中");
                    }
                }
            }, 10000);
        }
    };

    @Override
    protected final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doBeforeSetContentView();
        onInit(savedInstanceState);
        mContext = this;
    }


    @Override
    public void setContentView(View view) {
        ViewGroup root = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.activity_base, null, false);
        /*主内容*/
        contentLayout = (FrameLayout) root.findViewById(R.id.contentLayout);
        tv_no_net = (TextView) root.findViewById(R.id.tv_no_net);
        tv_no_net.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                startActivity(intent);
            }
        });
        contentLayout.addView(view);
        super.setContentView(root);
    }

    protected void onInit(Bundle savedInstanceState) {

        View showView = LayoutInflater.from(this).inflate(getLayoutId(), null);
        mCompositeSubscription = new CompositeSubscription();
        this.setContentView(showView);
        mUnBinder = ButterKnife.bind(this);
        onCreateBaseView(savedInstanceState);

    }

    /**
     * 设置layout前配置
     */
    protected void doBeforeSetContentView() {
        //设置昼夜主题
        initTheme();
        // 把actvity放到application栈中管理
        AppManager.getInstance().addActivity(this);
        // 无标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 设置竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // 默认着色状态栏
//        SetStatusBarColor();
        // create our manager instance after the content view is set
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        // enable status bar tint
        tintManager.setStatusBarTintEnabled(true);
        // enable navigation bar tint
        tintManager.setNavigationBarTintEnabled(true);

    }

//    @Override
//    public final void setContentView(int layoutResID) {
//        super.setContentView(layoutResID);
//    }


    @Override
    protected void onResume() {
        if (netWorkStateReceiver == null) {
            netWorkStateReceiver = new ConnectivityBroadcastReceiver();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netWorkStateReceiver, filter);
        super.onResume();

        MobclickAgent.onResume(this);
        write();
        if (mDialog != null)
            mDialog.showLoadingAnimation(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mDialog != null)
            mDialog.showLoadingAnimation(false);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(netWorkStateReceiver);
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUnBinder != null)
            mUnBinder.unbind();
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        AppManager.getInstance().finishActivity(this);
        mCompositeSubscription.unsubscribe();
    }

    public void addSubscribe(Subscription subscribe) {
        if (mCompositeSubscription != null && !mCompositeSubscription.isUnsubscribed())
            mCompositeSubscription.add(subscribe);
    }

    protected void registerEventBus() {
        EventBus.getDefault().register(this);
    }


    private void showLoadingDialogImp(String msg) {
        if (msg == null || msg.length() == 0)
            msg = "加载中...";
        if (isRunningApp(this, this.getClass().getName())) {
            if (mDialog != null) {
                mDialog.setText(msg);
            } else {
                mDialog = new LoadingDialog(this);
                mDialog.show(msg);
            }
        }
    }
    public void showLoadingDialog(final String msg) {
        try {
            if(Thread.currentThread() == Looper.getMainLooper().getThread()) {
                showLoadingDialogImp(msg);
            }else{
                MyApplication.getInstance().runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        showLoadingDialogImp(msg);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void hideLoadingDialog() {
        if (mDialog != null) {
            try {
                if(Thread.currentThread() == Looper.getMainLooper().getThread()) {
                    mDialog.dismiss();
                    mDialog = null;
                }else{
                    MyApplication.getInstance().runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            mDialog.dismiss();
                            mDialog = null;
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isRunningApp(Context context, String packageName) {
        boolean isAppRunning = false;
//        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        return AppManager.getInstance().isExistActivity(this);
//        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
//        for (ActivityManager.RunningTaskInfo info : list) {
//            if (info.topActivity.getClassName().equals(packageName)) {
//                isAppRunning = true;
//                // find it, break
//                break;
//            }
//        }
//        return isAppRunning;
    }

    protected abstract void onCreateBaseView(@Nullable Bundle savedInstanceState);

    protected abstract int getLayoutId();

    /**
     * 设置主题
     */
    private void initTheme() {
        this.setTheme(R.style.AppTheme);
    }

    /**
     * 着色状态栏（4.4以上系统有效）
     */
    protected void SetStatusBarColor() {
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this, R.color.tool_bar_bg));
    }

    public void onPostEvent(String event) {
        MobclickAgent.onEvent(this, event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (PermissionUtils.requestPermissionsResult(this, permissions, grantResults)) {
            requestPermissionsSuccess(requestCode);
        }
    }

    protected static <P> Observable.Transformer<P, P> showWaitingTransformer(final Action0 showWaiting, final Action0 hideWaiting) {
        return new Observable.Transformer<P, P>() {
            @Override
            public Observable<P> call(Observable<P> observable) {
                if (showWaiting == null || hideWaiting == null) {
                    return observable;
                }
                return observable.doOnSubscribe(showWaiting)
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .doOnTerminate(hideWaiting);
            }
        };
    }

    /**
     * 所有权限请求通过
     *
     * @param requestCode
     */
    public void requestPermissionsSuccess(int requestCode) {

    }

    private void write() {
//        LogManager.getInstance()
//                .setBusinessType("")
//                .setBusinessId("")
//                .setPage(this.getClass().getSimpleName())
//                .setRegion("")
//                .setName("")
//                .setAction("")
//                .setCategory("")
//                .build(4, this);
    }

    //3.9.7 添加全局监听网络
    class ConnectivityBroadcastReceiver extends BroadcastReceiver {

        private ConnectivityManager mConnectivityManager;
        private NetworkInfo netInfo;

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                netInfo = mConnectivityManager.getActiveNetworkInfo();
                netStateReceive(netInfo);
            }

        }

    }

    //重写 要非空判断
    public void netStateReceive(NetworkInfo netInfo) {
        if (netInfo != null && netInfo.isAvailable()) {
//                if(netInfo.getType() == ConnectivityManager.TYPE_WIFI){
//                    /////WiFi网络
//                }else if(netInfo.getType() == ConnectivityManager.TYPE_ETHERNET){
//                    /////有线网络
//                }else if(netInfo.getType() == ConnectivityManager.TYPE_MOBILE){
//                    /////////3g网络
//                }
            EventBus.getDefault().post(new NetInfoEvent(true));//此处推送 为保留原来的代码
            Logger.i("网络连接成功");
            if (tv_no_net != null)
                tv_no_net.setVisibility(View.GONE);
        } else {
            EventBus.getDefault().post(new NetInfoEvent(false));
            Logger.i("网络断开");
            if (tv_no_net != null)
                tv_no_net.setVisibility(View.VISIBLE);
        }
    }
}
