package com.growalong.android.ui.fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.growalong.android.R;
import com.growalong.android.account.AccountManager;
import com.growalong.android.model.UserInfoModel;
import com.growalong.android.present.CommSubscriber;
import com.growalong.android.present.UserPresenter;
import com.growalong.android.ui.MyCollectActivity;
import com.growalong.android.ui.SettingActivity;

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

        userPresenter.getUserInfo(AccountManager.getInstance().getAccountInfo().getUserId(), "c").subscribe(new CommSubscriber<UserInfoModel>() {
            @Override
            public void onSuccess(UserInfoModel userInfoModel) {
                mUserInfoModel = userInfoModel;
                name.setText(userInfoModel.getName());
                interest.setText(userInfoModel.getHobby());
                location.setText(userInfoModel.getFamilyInfo());
                Glide.with(activity).load(userInfoModel.getHeadImgUrl()).asBitmap().into(headView);
                Drawable drawable;
                if (userInfoModel.getGender() == 1) {
                    //男
                    drawable = getResources().getDrawable(R.mipmap.man);
                } else {
                    drawable = getResources().getDrawable(R.mipmap.women);
                }
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                name.setCompoundDrawables(drawable, null, null, null);

                //获取到的个人信息之后才添加点击事件
                study_level.setOnClickListener(MyFragment.this);
                interest.setOnClickListener(MyFragment.this);
                collect.setOnClickListener(MyFragment.this);
            }

            @Override
            public void onFailure(Throwable e) {
                super.onFailure(e);
            }
        });
        headView = getView().findViewById(R.id.headview);
        top.setOnClickListener(this);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mine;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.top:
                SettingActivity.startThisForResult(activity, mUserInfoModel);
                break;
            case R.id.my_download:

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
