package com.growalong.android.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.growalong.android.R;
import com.growalong.android.util.ToastUtil;

/**
 * Created by murphy on 10/12/16.
 */

public class CommonPhotoSelectorDialog implements View.OnClickListener {
    private Dialog dialog;
    private Context mContext;

    private Button openPhones;
    private Button openCamera;
    private Button cancel;

    private String localTempImgDir = "live";
    private final String IMAGE_TYPE = "image/*";

    private String timeStamp;
    public final int CAMERAREQUESTCODE = 0x12;
    public static final int PHOTOREQUESTCODE = 0x13;
    public static final int PHOTOREQUESTCODE1 = 99;

    public CommonPhotoSelectorDialog(Context context) {
        super();
        this.mContext = context;
        initDialog(context);
    }

    private void initDialog(Context context) {
        dialog = new Dialog(context, R.style.transparentFrameWindowStyle);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(context).inflate(R.layout.photo_choose_dialog, null);
        dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        // 设置显示动画
        window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = ((Activity) context).getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        dialog.setCanceledOnTouchOutside(true);
        openPhones = (Button) dialog.findViewById(R.id.openPhones);
        openCamera = (Button) dialog.findViewById(R.id.openCamera);
        cancel = (Button) dialog.findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
        openCamera.setOnClickListener(this);
        openPhones.setOnClickListener(this);
    }

    public void show() {
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }

    public String getCameraPath() {
        return Environment.getExternalStorageDirectory()
                + "/" + localTempImgDir + "/" + timeStamp + ".jpg";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.openCamera:
                // 先验证手机是否有sdcard
                String status = Environment.getExternalStorageState();
                if (status.equals(Environment.MEDIA_MOUNTED)) {
                    try {
                        CameraProtectActivity.startThisActivityForResult(mContext);
                    } catch (Exception e) {
                        ToastUtil.shortShow("没有找到储存目录");
                    }
                } else {
                    ToastUtil.shortShow("没有储存卡");
                }
                dialog.dismiss();
                break;
            case R.id.openPhones:
                Intent getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
                getAlbum.setType(IMAGE_TYPE);
                ((Activity) mContext).startActivityForResult(getAlbum, PHOTOREQUESTCODE);
                dialog.dismiss();
                break;
            case R.id.cancel:
                dialog.dismiss();
                break;
            default:
                break;
        }
    }
}
