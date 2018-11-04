package com.growalong.android.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.growalong.android.R;

/**
 * Created by yangxing on 2018/11/3.
 */
public class LoginMainActivity extends QLActivity {
    public static final String KEY_IS_START_APP = "is_start_app";

    @Override
    protected void onCreateBaseView(@Nullable Bundle savedInstanceState) {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login_main;
    }
}
