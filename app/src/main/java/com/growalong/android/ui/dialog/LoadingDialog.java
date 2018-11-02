package com.growalong.android.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.growalong.android.R;

/**
 * Created by murphy on 10/9/16.
 */

public class LoadingDialog {
    private Dialog mDialog;
    private Context mContext;

    private TextView tipTextView;
    //    private ProgressWheel progress;
    private LottieAnimationView progress;

    public LoadingDialog(Context context) {
        this.mContext = context;
        initDialog(context);
    }

    private void initDialog(Context context) {
        mDialog = new Dialog(context, R.style.loading_dialog);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.view_loading_dialog, null);
        mDialog.setContentView(view);

        tipTextView = (TextView) view.findViewById(R.id.tip_view);
//        progress = (ProgressWheel) view.findViewById(R.id.progress1);
        progress = (LottieAnimationView) view.findViewById(R.id.progress1);
        progress.useHardwareAcceleration(true);
        LottieComposition composition = LottieComposition.Factory.fromFileSync(context, "data.json");
        progress.setComposition(composition);
        progress.playAnimation();
        // “返回键”可以取消进度框
        mDialog.setCancelable(true);
        // 点击进度框外不能取消
        mDialog.setCanceledOnTouchOutside(false);
    }


    public void showLoadingAnimation(boolean b) {
//        if (b) {
//            progress.playAnimation();
//        } else {
//            progress.cancelAnimation();
//        }
        progress.playAnimation();
//        progress.setShouldAnimate(b);
    }

    public boolean isShow() {
        return mDialog.isShowing();
    }

    public void show() {
        try {
            if (null != mDialog && !mDialog.isShowing()) {
                mDialog.show();
            }
        } catch (Exception e) {
        }
    }

    public void setText(String msg) {
        if (!TextUtils.isEmpty(msg))
            tipTextView.setText(msg);
    }

    public void show(String msg) {
        if (null != tipTextView) {
            tipTextView.setText(msg);

            if (TextUtils.isEmpty(msg))
                tipTextView.setVisibility(View.GONE);
            else
                tipTextView.setVisibility(View.VISIBLE);
        }

        show();
    }

    public void show(int resid) {
        show(mContext.getResources().getString(resid));
    }

    public void dismiss() {
        if (null != mDialog && mDialog.isShowing()) {
            progress.cancelAnimation();
            mDialog.dismiss();
        }
    }
}
