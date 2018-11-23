package com.growalong.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.growalong.android.R;
import com.growalong.android.model.UserInfoModel;

/**
 * Created by yangxing on 2018/11/21.
 */
public class SettingActivity extends QLActivity {
    public static final int IS_CHANGE = 22;

    public static void startThisForResult(QLActivity activity, UserInfoModel mUserInfoModel) {
        Intent intent = new Intent(activity, SettingActivity.class);
        intent.putExtra("user", mUserInfoModel);
        activity.startActivityForResult(intent, IS_CHANGE);
    }

    @Override
    protected void onCreateBaseView(@Nullable Bundle savedInstanceState) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SettingFragment fragment = (SettingFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        fragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }
}
