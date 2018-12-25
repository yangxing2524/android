package com.growalong.android.ui.fragment;

import android.os.Bundle;
import android.view.View;

import com.growalong.android.R;
import com.growalong.android.ui.LoginPasswordActivity;

/**
 * Created by gangqing on 2016/8/15.
 */

public class LoginMainFragment extends NewBaseFragment implements View.OnClickListener {

    @Override
    protected void initEventAndData(Bundle savedInstanceState, View mView) {
        mView.findViewById(R.id.loginBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginPasswordActivity.startThis(activity);
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_login_main;
    }

    @Override
    public void onClick(View v) {

    }
}
