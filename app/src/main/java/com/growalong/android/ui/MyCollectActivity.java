package com.growalong.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.growalong.android.R;

/**
 * Created by yangxing on 2018/11/15.
 */
public class MyCollectActivity extends QLActivity {

    public static void startThis(QLActivity activity) {
        activity.startActivity(new Intent(activity, MyCollectActivity.class));
    }

    @Override
    protected void onCreateBaseView(@Nullable Bundle savedInstanceState) {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_collect;
    }
}
