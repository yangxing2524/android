package com.growalong.android.image;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;

import com.growalong.android.R;
import com.growalong.android.ui.customview.popupwindow.BasePopupWindow;

/**
 * Created by murphy on 2017/11/8.
 */

public class SavePicturePopupWindow extends BasePopupWindow {

    View tvSavePic;
    View tvClose;
    OnSaveClickListener mListener;

    public SavePicturePopupWindow(Context context, OnSaveClickListener listener) {
        super(context);
        mListener = listener;
        initView();
    }

    public SavePicturePopupWindow(Context context, int w, int h) {
        super(context, w, h);
        initView();
    }

    private void initView() {
        tvSavePic = findViewById(R.id.tv_popup_save_pic);
        tvClose = findViewById(R.id.tv_popup_close);

        tvSavePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.save();
                }
            }
        });
    }

    @Override
    public View onCreatePopupView() {
        return createPopupById(R.layout.view_save_pic);
    }

    @Override
    public View initAnimaView() {
        return null;
    }

    @Override
    protected Animation initShowAnimation() {
        return null;
    }

    @Override
    public View getClickToDismissView() {
        return getPopupWindowView();
    }

    public  interface OnSaveClickListener{
        void save();
    }
}
