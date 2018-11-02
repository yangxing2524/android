package com.growalong.android.ui;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUriExposedException;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.growalong.android.R;
import com.growalong.android.app.MyApplication;
import com.growalong.android.util.FileProviderUtils;
import com.growalong.android.util.ToastUtil;

import java.io.File;

/**
 * Created by murphy on 2017/3/14.
 */

public class CameraProtectActivity extends QLActivity {
    private static final String TAG = "CameraProtectActivity";
    public static final String IMAGE_PATH = "image_path";
    public static final int PICK_FROM_CAMERA = 4;

    private Uri imageUri;

    private String localTempImgDir = "live";

    private int configure = Configuration.ORIENTATION_PORTRAIT;
    int totalCount = 5;

    public static void startThisActivityForResult(Context context) {
        Intent intent = new Intent(context, CameraProtectActivity.class);
        ((Activity) context).startActivityForResult(intent, CommonPhotoSelectorDialog.PHOTOREQUESTCODE1);
    }

    @Override
    protected void onCreateBaseView(@Nullable Bundle savedInstanceState) {
//        String file = getIntent().getStringExtra("file");
        if (savedInstanceState != null) {
            String imagePath = savedInstanceState.getString(IMAGE_PATH);

            if (!TextUtils.isEmpty(imagePath)) {
                File mFile = new File(imagePath);
                if (mFile.exists()) {
                    Intent rsl = new Intent();
                    rsl.putExtra(IMAGE_PATH, imagePath);
                    setResult(Activity.RESULT_OK, rsl);
                    finish();
                } else {
                    finish();
                }
            } else {
                finish();
            }
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File dir = new File(Environment.getExternalStorageDirectory() + "/"
                + localTempImgDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(Environment.getExternalStorageDirectory() + "/"
                + localTempImgDir + "/" + System.currentTimeMillis() + ".jpg");


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            imageUri = FileProviderUtils.getUriForFile(mContext, "com.growalong.android.fileProvider", file);
            intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            //                                intent.setDataAndType(contentUri, type);
        } else {
            imageUri = Uri.fromFile(file);
            intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            //                                intent.setDataAndType(Uri.fromFile(f), type);
        }

        try {
            startActivityForResult(intent, PICK_FROM_CAMERA);
        } catch (ActivityNotFoundException e) {
            ToastUtil.shortShow("没有找到储存目录");
            finish();
        } catch (Exception f) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (f instanceof FileUriExposedException) {
                    finish();
                }
            }
        }

    }

    @Override
    protected int getLayoutId() {
        return R.layout.transp;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (imageUri != null) {
            outState.putString(IMAGE_PATH, imageUri.getPath());
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        configure = newConfig.orientation;
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            finish();
            return;
        }
        if (imageUri != null) {
            Intent rsl = new Intent();
            rsl.putExtra(IMAGE_PATH, imageUri.getPath());
            setResult(RESULT_OK, rsl);
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    totalCount--;
                    if (configure == 1 || totalCount == 0) {
                        finish();
                    } else {
                        MyApplication.applicationHandler.postDelayed(this, 500);
                    }

                }
            };
            MyApplication.applicationHandler.postDelayed(runnable, 500);
//            finish();
        } else {
            finish();
        }
    }

}
