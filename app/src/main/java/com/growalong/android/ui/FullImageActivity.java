package com.growalong.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.growalong.android.R;

/**
 * Created by yangxing on 2018/12/19.
 */
public class FullImageActivity extends QLActivity {
    public static void startThis(QLActivity qlActivity, String url) {
        Intent intent = new Intent(qlActivity, FullImageActivity.class);
        intent.putExtra("url", url);
        qlActivity.startActivity(intent);
    }

    @Override
    protected void onCreateBaseView(@Nullable Bundle savedInstanceState) {
        ImageView img = findViewById(R.id.img);
        Glide.with(this).load(getIntent().getStringExtra("url")).asBitmap().into(img);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_fullscreen_img;
    }
}
