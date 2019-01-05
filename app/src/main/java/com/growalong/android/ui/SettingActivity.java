package com.growalong.android.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.growalong.android.R;
import com.growalong.android.model.UserInfoModel;
import com.growalong.android.util.Utils;
import com.tencent.qcloud.ui.TemplateTitle;

/**
 * Created by yangxing on 2018/11/21.
 */
public class SettingActivity extends QLActivity {
    public static final int IS_CHANGE = 22;//回调

    public static void startThisForResult(Context context, UserInfoModel mUserInfoModel, boolean isSelf, boolean canModify) {
        Intent intent = new Intent(context, SettingActivity.class);
        intent.putExtra("user", mUserInfoModel);
        intent.putExtra("isSelf", isSelf);
        intent.putExtra("canModify", canModify);
        if (context instanceof QLActivity) {
            QLActivity activity = (QLActivity) context;
            activity.startActivityForResult(intent, IS_CHANGE);
            return;
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreateBaseView(@Nullable Bundle savedInstanceState) {
        TemplateTitle templateTitle = findViewById(R.id.titleView);
        if (getIntent().getBooleanExtra("isSelf", true)) {
            templateTitle.setTitleText(getResources().getString(R.string.my_info));
        } else {
            UserInfoModel mUserInfoModel = getIntent().getParcelableExtra("user");
            String nameStr = Utils.getName(mUserInfoModel);
            templateTitle.setTitleText(nameStr);
        }
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
