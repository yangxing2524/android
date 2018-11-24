package com.growalong.android.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.growalong.android.R;

/**
 */
public class LoginMainActivity extends QLActivity {
    public static final String KEY_IS_START_APP = "is_start_app";

    public static void startThis(Activity activity) {
        activity.startActivity(new Intent(activity, LoginMainActivity.class));
    }

    @Override
    protected void onCreateBaseView(@Nullable Bundle savedInstanceState) {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login_main;
    }
}
