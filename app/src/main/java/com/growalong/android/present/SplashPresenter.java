package com.growalong.android.present;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.growalong.android.R;
import com.growalong.android.account.AccountManager;
import com.growalong.android.app.AppManager;
import com.growalong.android.app.MyApplication;
import com.growalong.android.im.model.ImUserInfo;
import com.growalong.android.im.utils.PushUtil;
import com.growalong.android.model.BaseGenericModel;
import com.growalong.android.model.BaseParams;
import com.growalong.android.model.NetLoginIMBean;
import com.growalong.android.model.NoDataParams;
import com.growalong.android.net.retrofit.BaseRetrofitClient;
import com.growalong.android.net.retrofit.service.ILoginApis;
import com.growalong.android.ui.LoginMainActivity;
import com.growalong.android.ui.MainActivity;
import com.growalong.android.ui.QLActivity;
import com.growalong.android.util.RxUtil;
import com.huawei.android.pushagent.PushManager;
import com.meizu.cloud.pushsdk.util.MzSystemUtils;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMConnListener;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMUserConfig;
import com.tencent.imsdk.TIMUserStatusListener;
import com.tencent.qcloud.presentation.business.LoginBusiness;
import com.tencent.qcloud.presentation.event.FriendshipEvent;
import com.tencent.qcloud.presentation.event.GroupEvent;
import com.tencent.qcloud.presentation.event.MessageEvent;
import com.tencent.qcloud.presentation.event.RefreshEvent;
import com.tencent.qcloud.ui.NotifyDialog;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;


/**
 * 闪屏界面逻辑
 */
public class SplashPresenter implements TIMCallBack{
    private final QLActivity activity;
    private static final String TAG = SplashPresenter.class.getSimpleName();

    public SplashPresenter( QLActivity activity) {
        this.activity = activity;
    }

    /**
     * 跳转到主界面
     */
    public void navToHome() {
        MainActivity.startThis(activity);
        AppManager.getInstance().finishActivity(LoginMainActivity.class);
    }

    /**
     * 跳转到登录界面
     */
    public void navToLogin() {
        LoginMainActivity.startThis(activity);
        activity.finish();
    }

    /**
     * 是否已有用户登录
     */
    public boolean isUserLogin() {
//        return ImUserInfo.getInstance().getId() != null && (!TLSService.getInstance().needLogin(ImUserInfo.getInstance().getId()));
        return AccountManager.getInstance().isLogin() && ImUserInfo.getInstance().getId() != null && ImUserInfo.getInstance().getUserSig() != null;
    }

    /**
     * imsdk登录失败后回调
     */
    @Override
    public void onError(int i, String s) {
        Log.e(TAG, "login error : code " + i + " " + s);
        switch (i) {
            case 6208:
                //离线状态下被其他终端踢下线
                NotifyDialog dialog = new NotifyDialog();
                dialog.show(activity.getResources().getString(R.string.kick_logout), activity.getSupportFragmentManager(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        navToHome();
                    }
                });
                break;
            case 6200:
                Toast.makeText(activity, activity.getResources().getString(R.string.login_error_timeout), Toast.LENGTH_SHORT).show();
                navToLogin();
                break;
            case 70001:
                BaseParams<NoDataParams> baseParams = new BaseParams<>(new NoDataParams());
                ILoginApis iLoginApis = BaseRetrofitClient.getInstance().create(ILoginApis.class);
                Observable<BaseGenericModel<NetLoginIMBean>> observable = iLoginApis.loginForIM(baseParams);
                Subscription subscribe = observable.compose(RxUtil.<NetLoginIMBean>handleResult())
                        .subscribe(new Action1<NetLoginIMBean>() {
                            @Override
                            public void call(final NetLoginIMBean netLoginIMBean) {

                                final String userId = AccountManager.getUserId(activity);
                                TIMManager.getInstance().login(userId, netLoginIMBean.getUserSig(), new TIMCallBack() {
                                    @Override
                                    public void onError(int code, String desc) {
                                        //错误码 code 和错误描述 desc，可用于定位请求失败原因
                                        //错误码 code 列表请参见错误码表
                                        Log.d("im", "login failed. code: " + code + " errmsg: " + desc);
                                    }

                                    @Override
                                    public void onSuccess() {
                                        Log.d("im", "login succ");

                                        AccountManager.getInstance().setIMUserSig(netLoginIMBean.getUserSig());

                                        ImUserInfo.getInstance().setUserSig(netLoginIMBean.getUserSig());
                                        ImUserInfo.getInstance().setId(userId);
//                                        LoginMainActivity.startThis(SplashActivity.this);
                                        MainActivity.startThis(activity);
                                        activity.finish();
                                    }
                                });

                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                Toast.makeText(activity, activity.getString(R.string.login_error), Toast.LENGTH_SHORT).show();
                                navToLogin();
                            }
                        });
                activity.addSubscribe(subscribe);
                break;
            default:
                Toast.makeText(activity, activity.getResources().getString(R.string.login_error), Toast.LENGTH_SHORT).show();
                navToLogin();
                break;
        }

    }

    /**
     * imsdk登录成功后回调
     */
    @Override
    public void onSuccess() {

        //初始化程序后台后消息推送
        PushUtil.getInstance();
        //初始化消息监听
        MessageEvent.getInstance();
        String deviceMan = Build.MANUFACTURER;
        //注册小米和华为推送
        if (deviceMan.equals("Xiaomi") && shouldMiInit()) {
            MiPushClient.registerPush(activity.getApplicationContext(), "2882303761517480335", "5411748055335");
        } else if (deviceMan.equals("HUAWEI")) {
            PushManager.requestToken(activity);
        }

        //魅族推送只适用于Flyme系统,因此可以先行判断是否为魅族机型，再进行订阅，避免在其他机型上出现兼容性问题
        if (MzSystemUtils.isBrandMeizu(activity.getApplicationContext())) {
            com.meizu.cloud.pushsdk.PushManager.register(activity, "112662", "3aaf89f8e13f43d2a4f97a703c6f65b3");
        }

//        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
//        Log.d(TAG, "refreshed token: " + refreshedToken);
//
//        if(!TextUtils.isEmpty(refreshedToken)) {
//            TIMOfflinePushToken param = new TIMOfflinePushToken(169, refreshedToken);
//            TIMManager.getInstance().setOfflinePushToken(param, null);
//        }
//        MiPushClient.clearNotification(getApplicationContext());
        Log.d(TAG, "imsdk env " + TIMManager.getInstance().getEnv());
        MainActivity.startThis(activity);
        activity.finish();
    }


    public void initIMInfo() {
        //登录之前要初始化群和好友关系链缓存
        TIMUserConfig userConfig = new TIMUserConfig();
        userConfig.setUserStatusListener(new TIMUserStatusListener() {
            @Override
            public void onForceOffline() {
                Log.d(TAG, "receive force offline message");
                AppManager.getInstance().logout();
                LoginMainActivity.startThis(activity);
                MyApplication.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        AppManager.getInstance().finishAllActivity(LoginMainActivity.class);
                    }
                }, 200);

            }

            @Override
            public void onUserSigExpired() {
                //票据过期，需要重新登录
                new NotifyDialog().show(activity.getResources().getString(R.string.tls_expire), activity.getSupportFragmentManager(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LoginMainActivity.startThis(activity);
                        activity.finish();
                    }
                });
            }
        })
                .setConnectionListener(new TIMConnListener() {
                    @Override
                    public void onConnected() {
                        Log.i(TAG, "onConnected");
                    }

                    @Override
                    public void onDisconnected(int code, String desc) {
                        Log.i(TAG, "onDisconnected");
                    }

                    @Override
                    public void onWifiNeedAuth(String name) {
                        Log.i(TAG, "onWifiNeedAuth");
                    }
                });

        //设置刷新监听
        RefreshEvent.getInstance().init(userConfig);
        userConfig = FriendshipEvent.getInstance().init(userConfig);
        userConfig = GroupEvent.getInstance().init(userConfig);
        userConfig = MessageEvent.getInstance().init(userConfig);
        TIMManager.getInstance().setUserConfig(userConfig);
        LoginBusiness.loginIm(ImUserInfo.getInstance().getId(), ImUserInfo.getInstance().getUserSig(), this);
    }

    /**
     * 判断小米推送是否已经初始化
     */
    private boolean shouldMiInit() {
        ActivityManager am = ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = activity.getPackageName();
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 加载页面逻辑
     */
    public void start() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isUserLogin()) {
                    initIMInfo();
                } else {
                    navToLogin();
                }
            }
        }, 500);
    }


}
