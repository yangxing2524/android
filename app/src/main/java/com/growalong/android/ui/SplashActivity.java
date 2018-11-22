package com.growalong.android.ui;

import android.Manifest;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.growalong.android.R;
import com.growalong.android.account.AccountManager;
import com.growalong.android.im.model.ImUserInfo;
import com.growalong.android.im.utils.PushUtil;
import com.growalong.android.model.BaseGenericModel;
import com.growalong.android.model.BaseParams;
import com.growalong.android.model.NetLoginIMBean;
import com.growalong.android.model.NoDataParams;
import com.growalong.android.net.retrofit.BaseRetrofitClient;
import com.growalong.android.net.retrofit.service.ILoginApis;
import com.growalong.android.util.RxUtil;
import com.growalong.android.util.ToastUtil;
import com.huawei.android.pushagent.PushManager;
import com.meizu.cloud.pushsdk.util.MzSystemUtils;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMConnListener;
import com.tencent.imsdk.TIMLogLevel;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMUserConfig;
import com.tencent.imsdk.TIMUserStatusListener;
import com.tencent.qcloud.presentation.business.InitBusiness;
import com.tencent.qcloud.presentation.business.LoginBusiness;
import com.tencent.qcloud.presentation.event.FriendshipEvent;
import com.tencent.qcloud.presentation.event.GroupEvent;
import com.tencent.qcloud.presentation.event.MessageEvent;
import com.tencent.qcloud.presentation.event.RefreshEvent;
import com.tencent.qcloud.presentation.presenter.SplashPresenter;
import com.tencent.qcloud.presentation.viewfeatures.SplashView;
import com.tencent.qcloud.tlslibrary.service.TLSService;
import com.tencent.qcloud.tlslibrary.service.TlsBusiness;
import com.tencent.qcloud.ui.NotifyDialog;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

public class SplashActivity extends QLActivity implements SplashView, TIMCallBack {
    private final int REQUEST_PHONE_PERMISSIONS = 0;

    SplashPresenter presenter;
    private int LOGIN_RESULT_CODE = 100;
    private static final String TAG = IMSplashActivity.class.getSimpleName();

    /**
     * 跳转到主界面
     */
    @Override
    public void navToHome() {
        //登录之前要初始化群和好友关系链缓存
        TIMUserConfig userConfig = new TIMUserConfig();
        userConfig.setUserStatusListener(new TIMUserStatusListener() {
            @Override
            public void onForceOffline() {
                Log.d(TAG, "receive force offline message");
                ToastUtil.shortShow("receive force offline message");
            }

            @Override
            public void onUserSigExpired() {
                //票据过期，需要重新登录
                new NotifyDialog().show(getString(R.string.tls_expire), getSupportFragmentManager(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(SplashActivity.this, LoginMainActivity.class);
                        startActivity(intent);
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
     * 跳转到登录界面
     */
    @Override
    public void navToLogin() {
        Intent intent = new Intent(getApplicationContext(), LoginMainActivity.class);
        startActivityForResult(intent, LOGIN_RESULT_CODE);
    }

    /**
     * 是否已有用户登录
     */
    @Override
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
                dialog.show(getString(R.string.kick_logout), getSupportFragmentManager(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        navToHome();
                    }
                });
                break;
            case 6200:
                Toast.makeText(this, getString(R.string.login_error_timeout), Toast.LENGTH_SHORT).show();
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

                                final String userId = AccountManager.getUserId(SplashActivity.this);
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
                                        Intent intent = new Intent(SplashActivity.this, LoginMainActivity.class);
                                        startActivity(intent);
                                    }
                                });

                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                Toast.makeText(SplashActivity.this, getString(R.string.login_error), Toast.LENGTH_SHORT).show();
                                navToLogin();
                            }
                        });
                addSubscribe(subscribe);
                break;
            default:
                Toast.makeText(this, getString(R.string.login_error), Toast.LENGTH_SHORT).show();
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
            MiPushClient.registerPush(getApplicationContext(), "2882303761517480335", "5411748055335");
        } else if (deviceMan.equals("HUAWEI")) {
            PushManager.requestToken(this);
        }

        //魅族推送只适用于Flyme系统,因此可以先行判断是否为魅族机型，再进行订阅，避免在其他机型上出现兼容性问题
        if (MzSystemUtils.isBrandMeizu(getApplicationContext())) {
            com.meizu.cloud.pushsdk.PushManager.register(this, "112662", "3aaf89f8e13f43d2a4f97a703c6f65b3");
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
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult code:" + requestCode);
        if (LOGIN_RESULT_CODE == requestCode) {
            Log.d(TAG, "login error no " + TLSService.getInstance().getLastErrno());
            if (0 == TLSService.getInstance().getLastErrno()) {
                String id = TLSService.getInstance().getLastUserIdentifier();
                ImUserInfo.getInstance().setId(id);
//                ImUserInfo.getInstance().setUserSig(TLSService.getInstance().getUserSig(id));
                navToHome();
            } else if (resultCode == RESULT_CANCELED) {
                finish();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PHONE_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    init();
                } else {
                    Toast.makeText(this, getString(R.string.need_permission), Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    /**
     * 判断小米推送是否已经初始化
     */
    private boolean shouldMiInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 清楚所有通知栏通知
     */
    private void clearNotification() {
        NotificationManager notificationManager = (NotificationManager) this
                .getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        MiPushClient.clearNotification(getApplicationContext());
    }

    @Override
    protected void onCreateBaseView(@Nullable Bundle savedInstanceState) {


        clearNotification();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        final List<String> permissionsList = new ArrayList<>();
//        if (ConnectionResult.SUCCESS != GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)){
//            Toast.makeText(this, getString(R.string.google_service_not_available), Toast.LENGTH_SHORT).show();
////            GoogleApiAvailability.getInstance().getErrorDialog(this, GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this),
////                    GOOGLE_PLAY_RESULT_CODE).show();
//        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.READ_PHONE_STATE);
            if ((checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED))
                permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionsList.size() == 0) {
                init();
            } else {
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_PHONE_PERMISSIONS);
            }
        } else {
            init();
        }
    }

    private void init() {

        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        int loglvl = pref.getInt("loglvl", TIMLogLevel.DEBUG.ordinal());
        //初始化IMSDK
        InitBusiness.start(getApplicationContext(), loglvl);
        //初始化TLS
        TlsBusiness.init(getApplicationContext());
        presenter = new SplashPresenter(this);
        presenter.start();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }


}
