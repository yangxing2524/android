package com.growalong.android.ui.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.growalong.android.R;
import com.growalong.android.account.AccountManager;
import com.growalong.android.app.AppManager;
import com.growalong.android.model.ApiException;
import com.growalong.android.model.UserInfoModel;
import com.growalong.android.present.CommSubscriber;
import com.growalong.android.present.UserPresenter;
import com.growalong.android.ui.LoginMainActivity;
import com.growalong.android.ui.MyCollectActivity;
import com.growalong.android.ui.MyDownloadActivity;
import com.growalong.android.ui.SettingActivity;
import com.growalong.android.util.Utils;

import butterknife.BindView;

/**
 * Created by yangxing on 2018/11/12.
 */
public class MyFragment extends NewBaseFragment implements View.OnClickListener {
    @BindView(R.id.headview)
    public ImageView headView;
    @BindView(R.id.name)
    public TextView name;
    @BindView(R.id.interest)
    public TextView interest;
    @BindView(R.id.location)
    public TextView location;
    @BindView(R.id.top)
    public View top;

    @BindView(R.id.study_level)
    public View study_level;
    @BindView(R.id.my_download)
    public View my_download;
    @BindView(R.id.collect)
    public View collect;

    private UserInfoModel mUserInfoModel;
    private UserPresenter userPresenter = new UserPresenter();

    @Override
    protected void initEventAndData(Bundle savedInstanceState, View view) {
        initData();
    }

    private void initData() {
        study_level.setOnClickListener(MyFragment.this);
        interest.setOnClickListener(MyFragment.this);
        collect.setOnClickListener(MyFragment.this);
        my_download.setOnClickListener(MyFragment.this);
        mUserInfoModel = AppManager.getInstance().getUserInfoModel();
        if (mUserInfoModel == null) {
            userPresenter.getUserInfo(AccountManager.getInstance().getAccountInfo().getUserId(), "c").subscribe(new CommSubscriber<UserInfoModel>() {
                @Override
                public void onSuccess(UserInfoModel userInfoModel) {
                    mUserInfoModel = userInfoModel;
                    updateInfo();

                }

                @Override
                public void onFailure(Throwable e) {
                    super.onFailure(e);
                    if (e instanceof ApiException) {
                        ApiException apiException = (ApiException) e;
                        if (apiException.getStatus() == 10001) {
                            AccountManager.getInstance().logout();
                            LoginMainActivity.startThis(activity);
                            activity.finish();
                        }
                    }
                }
            });
        }
        headView = getView().findViewById(R.id.headview);
        top.setOnClickListener(this);
    }

    private void updateInfo() {
        if (getView() == null || mUserInfoModel == null) {
            return;
        }
        AppManager.getInstance().setUserInfoModel(mUserInfoModel);
        String nameStr = Utils.getName(mUserInfoModel);
        name.setText(nameStr);
        interest.setText(mUserInfoModel.getHobby());
        location.setText(mUserInfoModel.getAddress());
        Glide.with(activity).load(mUserInfoModel.getHeadImgUrl()).asBitmap().into(headView);
        Drawable drawable;
        if (mUserInfoModel.getGender() == 1) {
            //男
            drawable = getResources().getDrawable(R.mipmap.man);
        } else {
            drawable = getResources().getDrawable(R.mipmap.women);
        }
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        name.setCompoundDrawables(null, null, drawable, null);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateInfo();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mine;
    }

    @Override
    public void onClick(View v) {
        if (mUserInfoModel == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.top:
                SettingActivity.startThisForResult(activity, mUserInfoModel, true, true);
                break;
            case R.id.my_download:
                MyDownloadActivity.startThis(activity);
                break;
            case R.id.study_level:
                //跳转到对应的webview
                break;
            case R.id.collect:
                MyCollectActivity.startThis(activity);
                break;
        }
    }
}
