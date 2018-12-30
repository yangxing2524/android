package com.growalong.android.image;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.growalong.android.util.LogUtil;

/**
 * Created by murphy on 2017/11/7.
 */

public class InterceptLongClickLayout extends FrameLayout {

    boolean mIsConsumedLongClick = false;
    boolean mIsInLongClickArea = false;

    public InterceptLongClickLayout(@NonNull Context context) {
        super(context);
    }

    public InterceptLongClickLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public InterceptLongClickLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private OnLongClickLister mOnLongClickLister;

    float xDown, yDown, xUp, yUp;
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        boolean intercept = false;

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                LogUtil.d("InterceptLongClickLayout", "ACTION_DOWN");
                intercept = false;
                handleActionDown(ev);
                break;
            case MotionEvent.ACTION_MOVE:
                LogUtil.d("InterceptLongClickLayout", "ACTION_MOVE");
                intercept = false;
                handleActionMove(ev);
                break;
            case MotionEvent.ACTION_UP:
                LogUtil.d("InterceptLongClickLayout", "ACTION_UP");
                if (mIsConsumedLongClick) {
                    intercept = true;
                } else {
                    xUp = ev.getX();
                    yUp = ev.getY();
                    boolean isLongClick = isLongPressed(xDown, yDown, xUp,
                            yUp, ev.getDownTime(), ev.getEventTime(), 300);
                    if (isLongClick) {
                        intercept = true;
                        handleLongClick();
                    } else {
                        mIsConsumedLongClick = true;
                        intercept = false;
                    }
                }
                break;
            default:
                break;
        }

        return intercept;
    }

    private void handleActionDown(MotionEvent ev) {
        xDown = ev.getX();
        yDown = ev.getY();
        mIsInLongClickArea = true;
        mIsConsumedLongClick = false;
        handleLongClickByDown();
    }

    private void handleActionMove(MotionEvent ev) {
        mIsInLongClickArea = isLongPressed(xDown, yDown, ev.getX(),
                ev.getY(),ev.getDownTime() ,ev.getEventTime(),300);
        LogUtil.d("InterceptLongClickLayout",
                "xDown : " + xDown + "--xMove : " + ev.getX() + "yDown : " + yDown + "--yMove : " + ev.getY());
    }

    private void handleLongClick() {
        if (mOnLongClickLister != null && !mIsConsumedLongClick && mIsInLongClickArea) {
            mOnLongClickLister.onLongClick();
            mIsConsumedLongClick = true;
        }
    }

    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            handleLongClick();
        }
    };

    private void handleLongClickByDown() {
        postDelayed(mRunnable, 500);
    }

    private boolean isLongPressed(float lastX, float lastY,
                                  float thisX, float thisY,
                                  long lastDownTime, long thisEventTime,
                                  long longPressTime) {
        float offsetX = Math.abs(thisX - lastX);
        float offsetY = Math.abs(thisY - lastY);
        long intervalTime = thisEventTime - lastDownTime;
        if (offsetX <= 20 && offsetY <= 20 && intervalTime >= longPressTime) {
            return true;
        }
        return false;
    }

    public void setOnLongClickLister(OnLongClickLister onLongClickLister) {
        mOnLongClickLister = onLongClickLister;
    }

    public interface OnLongClickLister{
        void onLongClick();
    }
}
