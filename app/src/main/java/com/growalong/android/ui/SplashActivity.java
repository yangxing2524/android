package com.growalong.android.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.growalong.android.R;
import com.growalong.android.account.AccountManager;
import com.growalong.android.app.MyApplication;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends QLActivity {
    private final int REQUEST_PHONE_PERMISSIONS = 0;

    @Override
    protected void onCreateBaseView(@Nullable Bundle savedInstanceState) {

        final List<String> permissionsList = new ArrayList<>();
//        if (ConnectionResult.SUCCESS != GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)){
//            Toast.makeText(this, getString(R.string.google_service_not_available), Toast.LENGTH_SHORT).show();
////            GoogleApiAvailability.getInstance().getErrorDialog(this, GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this),
////                    GOOGLE_PLAY_RESULT_CODE).show();
//        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if ((checkSelfPermission(Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED)) permissionsList.add(Manifest.permission.READ_PHONE_STATE);
            if ((checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)) permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionsList.size() == 0){
                init();
            }else{
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_PHONE_PERMISSIONS);
            }
        }else{
            init();
        }
    }

    private void init() {
        MyApplication.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                jump();
            }
        }, 1000);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    private void jump() {

        Intent intent;
        if (AccountManager.getInstance().isLogin()) {
            intent = new Intent(SplashActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        } else {
            intent = new Intent(SplashActivity.this, LoginMainActivity.class);
            startActivity(intent);
            finish();
        }
    }

}
