package com.growalong.android.ui.recyclerview;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.aspsine.swipetoloadlayout.SwipeRefreshTrigger;
import com.aspsine.swipetoloadlayout.SwipeTrigger;
import com.growalong.android.R;

/**
 * Created by Aspsine on 2015/11/5.
 * onPrepare：代表下拉刷新开始的状态
 * onMove：代表正在滑动过程中的状态
 * onRelease：代表手指松开后，下拉刷新进入松开刷新的状态
 * onComplete：代表下拉刷新完成的状态
 * onReset：代表下拉刷新重置恢复的状态
 * onRefresh：代表正在刷新中的状态
 */
public class RefreshHeaderView extends RelativeLayout implements SwipeTrigger, SwipeRefreshTrigger {

    private LottieAnimationView animation_view;
    private TextView tv_status;
    private String text;


    public RefreshHeaderView(Context context) {
        super(context);
    }

    public RefreshHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RefreshHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        animation_view = findViewById(R.id.animation_view);
        tv_status = findViewById(R.id.tv_status);
        animation_view.useHardwareAcceleration(true);
        LottieComposition composition = LottieComposition.Factory.fromFileSync(getContext(), "data.json");
        animation_view.setComposition(composition);
    }

    public void setText(String text) {
        if (tv_status != null)
            tv_status.setVisibility(GONE);
    }


    @Override
    public void onRefresh() {
        animation_view.playAnimation();
        if (text != null && TextUtils.equals("null", text)) {
            tv_status.setText("");
        } else {
            tv_status.setText("刷新中");
        }

    }

    @Override
    public void onPrepare() {
//        animation_view.pauseAnimation();
        if (text != null && TextUtils.equals("null", text)) {
            tv_status.setText("");
        } else {
            tv_status.setText("下拉刷新");
        }
    }

    @Override
    public void onMove(int y, boolean isComplete, boolean automatic) {
    }

    @Override
    public void onRelease() {
//        tv_status.setText("松开刷新");
//        animation_view.playAnimation();
    }

    @Override
    public void onComplete() {
        animation_view.pauseAnimation();
//        animation_view.cancelAnimation();
    }

    @Override
    public void onReset() {

    }
}
