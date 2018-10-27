package com.growalong.android.app;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.util.DisplayMetrics;

import com.growalong.android.BuildConfig;
import com.growalong.android.agora.openvcall.model.AgoraCurrentUserSettings;
import com.growalong.android.agora.openvcall.model.AgoraWorkerThread;
import com.growalong.android.util.PackageUtil;
import com.orhanobut.logger.Logger;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.tinker.loader.app.ApplicationLifeCycle;
import com.tencent.tinker.loader.app.DefaultApplicationLike;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

//import com.tencent.mm.sdk.openapi.IWXAPI;
//import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * because you can not use any other class in your application, we need to
 * move your implement of Application to {@link ApplicationLifeCycle}
 * As Application, all its direct reference class should be in the main dex.
 * <p>
 * We use tinker-android-anno to make sure all your classes can be patched.
 * <p>
 * application: if it is startThisActivity with '.', we will add SampleApplicationLifeCycle's package name
 * <p>
 * flags:
 * TINKER_ENABLE_ALL: support dex, lib and resource
 * TINKER_DEX_MASK: just support dex
 * TINKER_NATIVE_LIBRARY_MASK: just support lib
 * TINKER_RESOURCE_MASK: just support resource
 * <p>
 * loaderClass: define the tinker loader class, we can just use the default TinkerLoader
 * <p>
 * loadVerifyFlag: whether check files' md5 on the load time, defualt it is false.
 * <p>
 * Created by zhangshaowen on 16/3/17.
 */
@SuppressWarnings("unused")
public class MyApplication extends DefaultApplicationLike {
    private static final String TAG = "MyApplication";
    public static final String HAS_UNREAD_FEED = "has_unread_feed";
    public static final String HAS_UNREAD_MSG = "has_unread_msg";
    private static final long DB_VERSION = 8;

    public static long sysTime = System.currentTimeMillis();
    private static MyApplication instance;
    public static volatile Handler applicationHandler;
    private static IWXAPI mApi;

    public static boolean canPlayAac = true;
    public static boolean canPlaym4a = true;
    public Context context;
    private DisplayMetrics displayMetrics = null;
    private static Realm realm;
    private RealmConfiguration realmConfiguration;
    private static List<String> qlLive;
    public static HashMap<String, Object> map = new HashMap<>();

    public MyApplication(Application application, int tinkerFlags, boolean tinkerLoadVerifyFlag,
                         long applicationStartElapsedTime, long applicationStartMillisTime, Intent tinkerResultIntent) {
        super(application, tinkerFlags, tinkerLoadVerifyFlag, applicationStartElapsedTime, applicationStartMillisTime, tinkerResultIntent);
    }

    public static synchronized MyApplication getInstance() {
        return instance;
    }

    public static Context getContext() {
        return getInstance().context;
    }

    private AgoraWorkerThread mWorkerThread;

    public synchronized void initWorkerThread() {
        if (mWorkerThread == null) {
            mWorkerThread = new AgoraWorkerThread(getApplicationContext());
            mWorkerThread.start();

            mWorkerThread.waitForReady();
        }
    }

    public synchronized AgoraWorkerThread getWorkerThread() {
        return mWorkerThread;
    }

    public synchronized void deInitWorkerThread() {
        mWorkerThread.exit();
        try {
            mWorkerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mWorkerThread = null;
    }

    public static final AgoraCurrentUserSettings mVideoSettings = new AgoraCurrentUserSettings();
    /**
     * install multiDex before install tinker
     * so we don't need to put the tinker lib classes in the main dex
     *
     * @param base
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onBaseContextAttached(Context base) {
        super.onBaseContextAttached(base);
        //you must install multiDex whatever tinker is installed!
        MultiDex.install(base);
        context = getApplication();
        Beta.installTinker(this);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void registerActivityLifecycleCallback(Application.ActivityLifecycleCallbacks callbacks) {
        getApplication().registerActivityLifecycleCallbacks(callbacks);
    }

    public static Realm getRealm() {
        if (realm == null) {
            instance.initRealm();
        }
        return realm;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(context);

        instance = this;
        if (BuildConfig.DEVELOPMENT_ENV) {
            Bugly.init(getApplication(), "5a5f00ba10", true);
        } else {
            Bugly.init(getApplication(), "8e5e9d7bfa", false);
        }

        // 获取当前进程名
        final String processName = PackageUtil.getProcessName(android.os.Process.myPid());
        // 获取当前包名
        final String packageName = context.getPackageName();
        applicationHandler = new Handler(context.getMainLooper());

        Realm.init(context);

        if (processName != null && !processName.equals(packageName)) {
            return;
        }

        runOnUIThread(new Runnable() {
            @Override
            public void run() {
                delayInit(processName, "", packageName);
            }
        }, 200);

    }

    private void delayInit(String processName, String channel, String packageName) {
        initRealm();

        init();
        //初始化错误收集
//        CrashHandler.getInstance(context).init();

//        FeedbackAPI.init(getApplication(), "23387584", "573f138afb91f724e33514107ca4edff");
        // 设置是否为上报进程
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
        strategy.setUploadProcess(processName == null || processName.equals(packageName));

        MobclickAgent.UMAnalyticsConfig c;
        CrashReport.setIsDevelopmentDevice(context, BuildConfig.DEBUG);
        if (BuildConfig.DEVELOPMENT_ENV) {
            c = new MobclickAgent.UMAnalyticsConfig(context, "5835dce8e88bad277c0022f1", "test");
//            CrashReport.initCrashReport(context, "5a5f00ba10", true, strategy);
            Bugly.init(getApplication(), "5a5f00ba10", false);
        } else {
            c = new MobclickAgent.UMAnalyticsConfig(context, "5735a17467e58ee723000aa4", channel);
//            CrashReport.initCrashReport(context, "8e5e9d7bfa", false, strategy);
            Bugly.init(getApplication(), "8e5e9d7bfa", false);
        }

        CrashReport.setAppChannel(context, channel);
        CrashReport.setAppPackage(context, context.getPackageName());
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            CrashReport.setAppVersion(context, pi.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        MobclickAgent.startWithConfigure(c);

    }

    private void initRealm() {
        if (realm == null) {
            realm = Realm.getInstance(getRealmConfiguration());
        }
    }

    private synchronized RealmConfiguration getRealmConfiguration() {
        if (realmConfiguration == null) {
            realmConfiguration = new RealmConfiguration.Builder()
                    .schemaVersion(DB_VERSION).migration(new QLMigration()).build();
        }
        return realmConfiguration;
    }

    public static Realm newRealm() {
        return Realm.getInstance(instance.getRealmConfiguration());
    }


    public static void init() {

        //初始化日志
        Logger.init(instance.context.getPackageName()).isShowThreadInfo();

    }



    public int getScreenHeight() {
        if (this.displayMetrics == null) {
            setDisplayMetrics(context.getResources().getDisplayMetrics());
        }
        return this.displayMetrics.heightPixels;
    }

    public int getScreenWidth() {
        if (this.displayMetrics == null) {
            setDisplayMetrics(context.getResources().getDisplayMetrics());
        }
        return this.displayMetrics.widthPixels;
    }

    public void setDisplayMetrics(DisplayMetrics DisplayMetrics) {
        this.displayMetrics = DisplayMetrics;
    }

    public static void runOnUIThread(Runnable runnable) {
        runOnUIThread(runnable, 0);
    }

    public static void runOnUIThread(Runnable runnable, long delay) {
        if (delay == 0) {
            applicationHandler.post(runnable);
        } else {
            applicationHandler.postDelayed(runnable, delay);
        }
    }

    public Context getApplicationContext() {
        return context;
    }
}
