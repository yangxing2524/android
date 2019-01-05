package com.growalong.android.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.growalong.android.R;
import com.growalong.android.account.AccountManager;
import com.growalong.android.app.AppManager;
import com.growalong.android.app.MyApplication;
import com.growalong.android.app.VideoChatHelper;
import com.growalong.android.im.model.FriendshipInfo;
import com.growalong.android.im.model.GroupInfo;
import com.growalong.android.im.model.ImUserInfo;
import com.growalong.android.model.UserInfoModel;
import com.growalong.android.present.CommSubscriber;
import com.growalong.android.present.UserPresenter;
import com.growalong.android.ui.fragment.CourseMainFragment;
import com.growalong.android.ui.fragment.MyFragment;
import com.growalong.android.util.ToastUtil;
import com.tencent.imsdk.TIMManager;
import com.tencent.qcloud.presentation.event.MessageEvent;
import com.tencent.qcloud.tlslibrary.service.TlsBusiness;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Tab页主界面
 */
public class MainActivity extends QLActivity {
    public static final int KEY_DOWN_INTERVAL = 2000;
    private static final String TAG = MainActivity.class.getSimpleName();
    private LayoutInflater layoutInflater;
    private FragmentTabHost mTabHost;
    private final Class fragmentArray[] = {ConversationFragment.class, CourseMainFragment.class, MyFragment.class};
    private int mTitleArray[] = {R.string.conversation, R.string.course, R.string.my};
    private int mImageViewArray[] = {R.drawable.tab_conversation, R.drawable.tab_contact, R.drawable.tab_setting};
    private String mTextviewArray[] = {"contact", "conversation", "setting"};
    private ImageView msgUnread;

    //双击退出函数
    private static Boolean mIsExit = false;

    public static void startThis(FragmentActivity activity) {
        activity.startActivity(new Intent(activity, MainActivity.class));
    }


    @Override
    protected void onCreateBaseView(@Nullable Bundle savedInstanceState) {
        if (requestPermission()) {
            Intent intent = new Intent(MainActivity.this, SplashActivity.class);
            finish();
            startActivity(intent);
        } else {
            initView();
            Toast.makeText(this, getString(TIMManager.getInstance().getEnv() == 0 ? R.string.env_normal : R.string.env_test), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    private void initView() {
        layoutInflater = LayoutInflater.from(this);
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.contentPanel);
        int fragmentCount = fragmentArray.length;
        for (int i = 0; i < fragmentCount; ++i) {
            //为每一个Tab按钮设置图标、文字和内容
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mTextviewArray[i]).setIndicator(getTabItemView(i));
            //将Tab按钮添加进Tab选项卡中
            mTabHost.addTab(tabSpec, fragmentArray[i], null);
            mTabHost.getTabWidget().setDividerDrawable(null);
        }

        if (AppManager.getInstance().getUserInfoModel() == null) {
            UserPresenter userPresenter = new UserPresenter();
            userPresenter.getUserInfo(AccountManager.getInstance().getAccountInfo().getUserId(), "c").subscribe(new CommSubscriber<UserInfoModel>() {
                @Override
                public void onSuccess(UserInfoModel userInfoModel) {
                    AppManager.getInstance().setUserInfoModel(userInfoModel);
                }

                @Override
                public void onFailure(Throwable e) {
                    super.onFailure(e);
                }
            });
        }

        MyApplication.getInstance().initWorkerThread();

        VideoChatHelper.getInstance();

        AppManager.getInstance().finishActivity(SplashActivity.class);
        AppManager.getInstance().finishActivity(LoginMainActivity.class);
        AppManager.getInstance().finishActivity(LoginPasswordActivity.class);
    }

    private View getTabItemView(int index) {
        View view = layoutInflater.inflate(R.layout.home_tab, null);
        ImageView icon = (ImageView) view.findViewById(R.id.icon);
        icon.setImageResource(mImageViewArray[index]);
        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(mTitleArray[index]);
        if (index == 0) {
            msgUnread = (ImageView) view.findViewById(R.id.tabUnread);
        }
        return view;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CommonPhotoSelectorDialog.PHOTOREQUESTCODE || requestCode == CommonPhotoSelectorDialog.PHOTOREQUESTCODE1) {
            MyFragment fragment = (MyFragment) getSupportFragmentManager().findFragmentByTag(mTextviewArray[2]);
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void logout() {
        TlsBusiness.logout(ImUserInfo.getInstance().getId());
        ImUserInfo.getInstance().setId(null);
        ImUserInfo.getInstance().setUserSig(null);
        MessageEvent.getInstance().clear();
        FriendshipInfo.getInstance().clear();
        GroupInfo.getInstance().clear();
        Intent intent = new Intent(MainActivity.this, SplashActivity.class);
        finish();
        startActivity(intent);

    }


    /**
     * 设置未读tab显示
     */
    public void setMsgUnread(boolean noUnread) {
        msgUnread.setVisibility(noUnread ? View.GONE : View.VISIBLE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Timer tExit;
            if (!mIsExit) {
                mIsExit = true; // 准备退出
                ToastUtil.shortShow("再按一次退出程序");
                tExit = new Timer();
                tExit.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        mIsExit = false; // 取消退出
                    }
                }, KEY_DOWN_INTERVAL); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
            } else {
                AppManager.getInstance().appExit();
            }
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private boolean requestPermission() {
        if (afterM()) {
            final List<String> permissionsList = new ArrayList<>();
            if ((checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ||
                    (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)) {
                return true;
            }
        }
        return false;
    }

    private boolean afterM() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }


}
